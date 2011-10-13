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

import java.util.concurrent.Delayed;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import org.mundo.rt.Message;
import org.mundo.rt.Logger;
import org.mundo.net.AbstractHandler;

/**
 * Delays sending of all packets.
 *
 * @author Erwin Aitenbichler
 */
public class DelayHandler extends AbstractHandler
{
  public DelayHandler()
  {
  }
  @Override
  public void init()
  {
    super.init();
    thread = new SendThread();
    thread.start();
  }
  @Override
  public void shutdown()
  {
    thread.interrupt();
    super.shutdown();
  }
  @Override
  public boolean down(Message msg) // IMessageHandler
  {
    queue.add(new MessageItem(msg));
    return true;
  }
  @Override
  public boolean up(Message msg) // IMessageHandler
  {
    // must never be called, because we did not register a MIMEType 
    throw new UnsupportedOperationException();
  }
  
  private class SendThread extends Thread
  {
    @Override
    public void run()
    {
      try
      {
        for(;;)
        {
          MessageItem mi = queue.take();
          log.finest("message delayed: " + (System.currentTimeMillis()-mi.expiry+delay)+" ms");
          emit_down(mi.msg);
        }
      }
      catch (InterruptedException x) {}
    }
  }
  
  private class MessageItem implements Delayed
  {
    MessageItem(Message m)
    {
      msg = m;
      expiry = System.currentTimeMillis() + delay;
    }
    public long getDelay(TimeUnit unit)
    {
      if (unit != TimeUnit.NANOSECONDS)
        throw new IllegalArgumentException("unit==NANOSECONDS expected");
      return 1000000L * (expiry - System.currentTimeMillis());
    }
    public int compareTo(Delayed d)
    {
      MessageItem mi = (MessageItem)d;
      if (expiry < mi.expiry)
        return -1;
      if (expiry > mi.expiry)
        return 1;
      return 0;
    }
    private Message msg;
    private long expiry;
  }
  
  private int delay = 100;
  private SendThread thread;
  private Logger log = Logger.getLogger("delay");
  private DelayQueue<MessageItem> queue = new DelayQueue<MessageItem>();
}
