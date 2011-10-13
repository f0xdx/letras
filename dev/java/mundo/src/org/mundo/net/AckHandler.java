/*
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is MundoCore Java.
 *
 * The Initial Developer of the Original Code is Telecooperation Group,
 * Department of Computer Science, Darmstadt University of Technology.
 * Portions created by the Initial Developer are
 * Copyright (C) 2001-2008 the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Erwin Aitenbichler
 */

package org.mundo.net;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import org.mundo.rt.GUID;
import org.mundo.rt.IMessageHandler;
import org.mundo.rt.Message;
import org.mundo.rt.ProtocolStack;
import org.mundo.rt.Service;
import org.mundo.rt.TypedArray;
import org.mundo.rt.TypedMap;
import org.mundo.rt.Logger;
import org.mundo.rt.Signal;
import org.mundo.net.transport.TransportLink;
import org.mundo.net.routing.IRoutingService;
import org.mundo.net.ProtocolCoordinator;
import org.mundo.net.AbstractHandler;
import org.mundo.net.routing.IRoutingService;

/**
 * Provides a TCP-like flow/congestion control handler.
 *
 * @author Erwin Aitenbichler
 */
public class AckHandler extends AbstractHandler implements IRoutingService.IConn
{
  public AckHandler()
  {
  }
  @Override
  public void init()
  {
    super.init();
    ProtocolCoordinator.register(mimeType, this);
    Signal.connect("rt", IRoutingService.IConn.class, this);
    thread = new TimerThread();
    thread.start();
  }
  @Override
  public void shutdown()
  {
    thread.interrupt();
    super.shutdown();
  }
  public void nodeAdded(GUID id) // IRoutingService.IConn
  {
  }
  public synchronized void nodeRemoved(GUID id) // IRoutingService.IConn
  {
    log.fine("nodeRemoved: "+id.shortString());
    peers.remove(id);
  }
  public synchronized boolean down(Message msg) // IMessageHandler
  {
    msg = (Message)msg.clone();
    PeerInfo pi = getPeerInfo(msg);

    // add our header to message
    TypedMap map = new TypedMap();
    map.putInt("n", ++pi.sendInSeq);
    map.putString("mimeType", msg.getType());
    msg.put("ack", "passive", map);
    msg.setType(mimeType);

    // add message to send queue
    MessageInfo mi = new MessageInfo(msg);
    pi.sendQueue.add(mi);

    // send message immediately if there is room in the congestion window
    if (pi.sendOutSeq-pi.sendQueueHead+1 < pi.sendCW)
    {
      log.finest("send direct: " + pi.sendInSeq);
      return sendData(mi, pi);
    }
    log.finest("send enqueue: " + pi.sendInSeq);
    return true;
  }
  public synchronized boolean up(Message msg) // IMessageHandler
  {
    // get our header
    TypedMap ackmap = msg.getMap("ack", "passive");
    if (ackmap == null)
    {
      log.finest("no ack map in message");
      return emit_up(msg);
    }

    // check if this is a control packet
    PeerInfo pi = getPeerInfo(msg);
    String req = ackmap.getString("request", null);
    if (req != null)
    {
      if ("ack".equals(req))
      {
        int ack = ackmap.getInt("a");
        if (pi.sendQueueHead > ack)
        {
          log.fine("received duplicate ack: " + ack);
          pi.dupAcks++;
          // fast retransmit
          if (pi.dupAcks >= 3)
          {
            pi.dupAcks = 0;
            // fast recovery
            pi.decreaseWindow();
            if (pi.sendQueue.size() > 0)
            {
              MessageInfo mi = pi.sendQueue.get(0);
              log.fine("retransmit (fast): " + getSeq(mi.msg));
              sendData(mi, pi);
            }
          }
          return true;
        }
        pi.dupAcks = 0;
        log.fine("received ack: " + ack);
        pi.increaseWindow();
        while (pi.sendQueueHead <= ack)
        {
          if (pi.sendQueue.size()==0)
          {
            log.warning("sequence number of ack too high: ack="+ack+", sendQueueHead="+pi.sendQueueHead);
            return true;
          }
          pi.sendQueue.remove(0);
          pi.sendQueueHead++;
        }
        // send messages from send queue
        int i = pi.sendOutSeq + 1 - pi.sendQueueHead;
        int l = pi.sendQueue.size();
        for (; i<l; i++)
        {
          if (pi.sendOutSeq-pi.sendQueueHead+1 >= pi.sendCW)
            break;
          log.finest("send Q: " + (pi.sendQueueHead + i));
          MessageInfo mi = pi.sendQueue.get(i);
          if (!sendData(mi, pi))
            break;
        }
      }
      else
      {
        log.info("unknown request: " + req);
      }
      return true;
    }

    // restore message type
    msg.setType(ackmap.getString("mimeType"));
    
    // check sequence number
    int seq = ackmap.getInt("n");
    if (seq == pi.rcvHead)
    {
      log.finest("dispatch direct: " + seq);
      boolean success = emit_up(msg);
      if (!success)
        return false;
      if (pi.rcvQueue.size() > 0)
      {
        pi.rcvQueue.remove(0);
        pi.rcvHead++;
        while (pi.rcvQueue.size() > 0)
        {
          Message qmsg = pi.rcvQueue.get(0);
          if (qmsg == null)
            break;
          log.finest("dispatch Q: " + getSeq(qmsg));
          if (!emit_up(qmsg))
          {
            success = false;
            break;
          }
          pi.rcvQueue.remove(0);
          pi.rcvHead++;
        }
      }
      else
        pi.rcvHead++;
      if (!sendAck(pi))
        success = false;
      return success;
    }
    if (seq < pi.rcvHead)
    {
      log.finest("rcvd duplicate packet: " + seq);
      sendAck(pi);
      return true;
    }
    
    // enqueue message
    int i;
    for (i=pi.rcvHead+pi.rcvQueue.size(); i<=seq; i++)
      pi.rcvQueue.add(null);
    pi.rcvQueue.set(seq-pi.rcvHead, msg);
    sendAck(pi);
    
    return true;
  }
  private synchronized void timer()
  {
    long now = System.currentTimeMillis();
    for (PeerInfo pi : peers.values())
    {
      int i, l = pi.sendOutSeq - pi.sendQueueHead;
      for (i=0; i<=l; i++)
      {
        if (i >= pi.sendQueue.size())
        {
          log.severe("sendQueueHead="+pi.sendQueueHead+", sendOutSeq="+pi.sendOutSeq+", size="+pi.sendQueue.size());
          break;
        }
        MessageInfo mi = pi.sendQueue.get(i);
        if (mi.sendTime < now-rto)
        {
          log.finest("retransmit (timeout): " + (pi.sendQueueHead + i));
          pi.resetWindow();
          sendData(mi, pi);
        }
      }
    }
  }

  /**
   * Send Acknowledge.
   */
  private boolean sendAck(PeerInfo pi)
  {
    Message msg = new Message();
    msg.setType(mimeType);
    
    TypedMap map = new TypedMap();
    map.putString("request", "ack");
    map.putInt("a", pi.rcvHead-1);
    msg.put("ack", "passive", map);
    
    log.finest("send ack: " + (pi.rcvHead - 1));
    return sendControl(msg, pi);
  }
  private boolean sendControl(Message msg, PeerInfo pi)
  {
    // FIXME: should not use the default stack here
    msg.setStack(ProtocolCoordinator.getInstance().getDefaultStack(), AckHandler.class);
    
    TypedMap tsmap = new TypedMap();
    tsmap.put("link", pi.link);
    msg.put("ts", "param", tsmap);
    
    return emit_down(msg);
  }
  /**
   * Sends a data message to the remote peer.
   */
  private boolean sendData(MessageInfo mi, PeerInfo pi)
  {
    int seq = getSeq(mi.msg);
    if (seq > pi.sendOutSeq)
      pi.sendOutSeq = seq;
    mi.sendTime = System.currentTimeMillis();
    return emit_down(mi.msg);
  }
  private int getSeq(Message msg)
  {
    return msg.getMap("ack", "passive").getInt("n");
  }
  /**
   * Returns the peer information structure associated with the transport link
   * associated with this message.
   */
  private PeerInfo getPeerInfo(Message msg)
  {
    TypedMap map = msg.getMap("ts", "param");
    if (map == null)
    {
      log.warning("missing chunk ts:param");
      return null;
    }
    TransportLink link = (TransportLink)map.getObject("link");
    
    PeerInfo pi = (PeerInfo)peers.get(link.remoteId);
    if (pi != null)
    {
      // always update transport link
      pi.link = link;
      return pi;
    }
    pi = new PeerInfo(link.remoteId);
    pi.link = link;
    peers.put(link.remoteId, pi);
    return pi;
  }
  
  private class TimerThread extends Thread
  {
    TimerThread()
    {
    }
    @Override
    public void run()
    {
      try
      {
        for(;;)
        {
          try
          {
            timer();
          }
          catch(Exception x)
          {
            x.printStackTrace();
            log.exception(x);
          }
          Thread.sleep(100);
        }
      }
      catch (InterruptedException x) {}
      log.fine("FlowThread terminated");
    }
  }
  
  private class PeerInfo
  {
    PeerInfo(GUID id)
    {
      remoteId = id;
    }
    void increaseWindow()
    {
      // increase size of congestion window on ack
      if (sendCW <= sendThres/2)
      {
        // slow start
        sendCW *= 2;
        log.finest("slow start: " + sendCW);
      }
      else
      {
        // additive increase
        sendCW++;
        log.finest("additive increase: " + sendCW);
      }
    }
    void decreaseWindow()
    {
      if (sendCW > 1)
      {
        sendThres = sendCW / 2;
        sendCW = sendThres + 3;
        log.finest("loss detected: cw=" + sendCW + ", thres=" + sendThres);
      }
    }
    void resetWindow()
    {
      // multiplicative decrease
      if (sendCW > 1)
      {
        sendThres = sendCW / 2;
        sendCW = 1;
        log.finest("congestion: cw=" + sendCW + ", thres=" + sendThres);
      }
    }
    
    GUID          remoteId;
    TransportLink link;
    
    // send data structures
    int           sendInSeq = 0;
    int           sendOutSeq = 0;
    int           dupAcks = 0;
    int           sendQueueHead = 1;
    int           sendCW = 1;
    int           sendThres = 1;
    ArrayList<MessageInfo> sendQueue = new ArrayList<MessageInfo>();

    // receive data structures
    int           rcvHead = 1;
    ArrayList<Message> rcvQueue = new ArrayList<Message>();
  }
  
  private class MessageInfo
  {
    MessageInfo(Message m)
    {
      msg = m;
    }
    Message  msg;
    long     sendTime;
  }

  private int rto = 1000;              // retransmission timeout
  private TimerThread thread;
  private Logger log = Logger.getLogger("ack");
  private HashMap<GUID,PeerInfo> peers = new HashMap<GUID,PeerInfo>();
  private static final String mimeType = "message/mc-ack";
}
