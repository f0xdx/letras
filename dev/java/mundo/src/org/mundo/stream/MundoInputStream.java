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
 * Copyright (C) 2001-2010 the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Daniel Schreiber
 * Stefan Radomski
 */

package org.mundo.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.mundo.rt.Channel;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Logger;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Session;
import org.mundo.rt.Subscriber;

public class MundoInputStream extends InputStream implements IReceiver {
  protected boolean eof = false;

  /**
   * When read() is called with no data available, throw an IOException. If set
   * to 0 and read will just return 0 when no data is availabe.
   * 
   */
  public int TIMEOUT = 1000;

  protected int nextSeq = 0;
  protected static Logger log = Logger.getLogger("istream");
  private IOException pendingIOException;
  protected Subscriber sub;
  protected Channel channel;

  // do not keep more than this many buffers - 0 means unlimited
  public int MAX_BUFFERS = 0;

  // we use a linked list with bytebuffer wrappers to avoid copying
  protected Deque<ByteBuffer> byteBuffers = new LinkedList<ByteBuffer>();

  protected MundoInputStream() {
  }

  /**
   * Create a new InputStream with a channel object and a session.
   * 
   * @param ch
   *          The channel object to be used.
   * @param s
   *          The session to use.
   */
  public MundoInputStream(Channel ch, Session s) {
    sub = s.subscribe(ch, this);
    channel = ch;
  }

  /**
   * Create a new InputStream on the given channel name, using the given
   * session.
   * 
   * @param channelName
   *          The name of the channel to listen on
   * @param s
   *          The session to be used.
   */
  public MundoInputStream(String channelName, Session s) {
    sub = s.subscribe("lan", channelName, this);
    channel = sub.getChannel();
  }

  /**
   * Create a new InputStream on the specified channel name.
   * 
   * A new session is created, using a dummy service.
   * 
   * @param channelName
   *          The channel's name to listen on
   */
  public MundoInputStream(String channelName) {
    Service dummy = new Service();
    Mundo.registerService(dummy);
    sub = dummy.getSession().subscribe("lan", channelName, this);
    channel = sub.getChannel();
  }

  @Override
  public void close() throws IOException {
    super.close();
    sub.unsubscribe();
  }

  /**
   * Listen on another channel for input.
   * 
   * @param channelName
   *          The new channelName to listen on.
   */
  public void setChannelName(String channelName) {
    // TODO: should we flush first?
    if (!sub.getChannel().equals(channelName)) {
      System.out.println("Subscribing to " + channelName);
      sub = sub.getSession().subscribe("lan", channelName, this);
    }
  }

  public String getChannelName() {
    return channel.getName();
  }

  /**
   * Read a single byte and cast it to int.
   */
  @Override
  public int read() throws IOException {
    if (pendingIOException != null) {
      throw pendingIOException;
    }
    waitForData();

    if (eof && available() == 0) {
      return -1; // end of stream reached
    }
    int ret;
    synchronized (byteBuffers) {
      // TODO: is this correct behavior?
      if (available() == 0) {
        return 0;
      }
      // result is UNSIGNED byte OR -1
      ByteBuffer head = byteBuffers.getFirst();
      ret = head.get() & 0xff;
      if (!head.hasRemaining()) {
        byteBuffers.removeFirst();
      }
    }
    return ret;
  }

  @Override
  public int read(byte[] data) throws IOException {
    waitForData();
    if (eof && available() == 0) {
      return -1; // end of stream reached
    }
    return read(data, 0, data.length);
  }

  @Override
  public int read(byte[] data, int off, int len) throws IOException {
    waitForData();
    if (eof && available() == 0) {
      return -1; // end of stream reached
    }
    synchronized (byteBuffers) {
      int toBeRead = Math.min(len, available());
      int read = 0;
      while (read < toBeRead) {
        // get oldest byte buffer
        ByteBuffer head = byteBuffers.getFirst();

        // how many bytes we can read from it
        int length = Math.min(toBeRead - read, head.remaining());

        // copy data into output buffer
        System.arraycopy(head.array(), head.position(), data, read + off,
            length);

        // reposition the read index
        head.position(head.position() + length);

        // delete byte buffer if empty
        if (!head.hasRemaining()) {
          byteBuffers.removeFirst();
        }
        read += length;
      }
      return toBeRead;
    }
  }

  @Override
  public int available() throws IOException {
    synchronized (byteBuffers) {
      // TODO: we can do this in constant time by introducing a new variable
      int available = 0;
      Iterator<ByteBuffer> bbIter = byteBuffers.iterator();
      while (bbIter.hasNext()) {
        ByteBuffer curr = bbIter.next();
        available += curr.remaining();
      }
      return available;
    }
  }

  private void waitForData() throws IOException {
    synchronized (this) {
      if (TIMEOUT > 0) {
        // wait for new data to arrive
        long deadline = System.currentTimeMillis() + TIMEOUT;
        while (byteBuffers.peek() == null && !eof) {
          try {
            long d = deadline - System.currentTimeMillis();
            if (d < 1) {
              throw new IOException("Timeout waiting for new data");
            }
            wait(d);
          } catch (IllegalMonitorStateException x) {
            log.exception(x);
          } catch (InterruptedException x) {
            log.warning("Wait has been interrupted");
          }
        }
      }
      // if (byteBuffers.peek() != null) {
      // System.err.println("There is data");
      // }
      // if (eof) {
      // System.err.println("There is eof");
      // }
    }
  }

  @Override
  public void received(Message msg, MessageContext ctx) {
    //System.out.println("MundoInputStream received on " + ctx.channel.getName());
    // we need to synchronize on this for notify()/wait()
    synchronized (this) {
      // TODO: we are ignoring sequences for now
      // if (msg.getMap().getInt("sequence") != nextSeq) {
      // } else {
      // nextSeq++;
      // }
      if (msg.getMap() != null && msg.getMap().containsKey("eof")
          && msg.getMap().getBoolean("eof")) {
        eof = true;
      }
      if (msg.getBlob("data") != null
          && msg.getBlob("data").getBuffer() != null
          && msg.getBlob("data").getBuffer().length > 0) {
        synchronized (byteBuffers) {
          // System.err.println("Adding ByteBuffer with "
          // + msg.getBlob("data").getBuffer().length + " bytes");
          // System.err.print(".");
          ByteBuffer buffer = ByteBuffer.wrap(msg.getBlob("data").getBuffer());
          byteBuffers.addLast(buffer);

          // drop old buffers
          if (MAX_BUFFERS > 0) {
            for (int i = 0; i < byteBuffers.size() - MAX_BUFFERS; i++) {
              byteBuffers.removeFirst();
               System.err.println("Dropped");
            }
          }
        }
      }
      notify();
    }
  }
}
