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
import java.io.OutputStream;

import org.mundo.rt.Blob;
import org.mundo.rt.Channel;
import org.mundo.rt.Logger;
import org.mundo.rt.Message;
import org.mundo.rt.Mundo;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;
import org.mundo.rt.Session;
import org.mundo.rt.TypedMap;

public class MundoOutputStream extends OutputStream {

  protected Publisher pub;
  protected Channel channel;
  protected static Logger log = Logger.getLogger("ostream");
  private int seq = 0;
  private byte[] buffer = new byte[1024];
  private int bufferPos = 0;

  /**
   * Create a new OutputStream with a channel object and a session.
   * 
   * @param ch
   *          The channel object to be used.
   * @param s
   *          The session to use.
   */
  public MundoOutputStream(Channel ch, Session s) {
    pub = s.publish(ch);
  }

  /**
   * Create a new OutputStream on the given channel name, using the given
   * session.
   * 
   * @param channelName
   *          The name of the channel to publish on
   * @param s
   *          The session to be used.
   */
  public MundoOutputStream(String channelName, Session s) {
    pub = s.publish("lan", channelName);
    channel = pub.getChannel();
  }

  /**
   * Create a new OutputStream on the specified channel name.
   * 
   * A new session is created, using a dummy service.
   * 
   * @param channelName
   *          The channel's name to publish on
   */
  public MundoOutputStream(String channelName) {
    Service dummy = new Service();
    Mundo.registerService(dummy);
    pub = dummy.getSession().publish("lan", channelName);
    channel = pub.getChannel();
  }

  /**
   * Publish further output on another channel.
   * 
   * @param channelName
   *          The new channelName to publish on.
   * @throws IOException
   */
  public void setChannelName(String channelName) {
    if (!pub.getChannel().equals(channelName)) {
      try {
        flush();
      } catch (IOException e) {
        log.exception(e);
      }
      pub = pub.getSession().publish("lan", channelName);
    }
  }

  /**
   * Get the channel's name we are publishing on.
   * 
   * @return Publishing channel's name
   */
  public String getChannelName() {
    return channel.getName();
  }

  @Override
  public void write(int data) throws IOException {
    byte[] tmp = new byte[1];
    // tmp[0] = (byte) (data >> 24);
    // tmp[1] = (byte) ((data << 8) >> 24);
    // tmp[2] = (byte) ((data << 16) >> 24);
    // tmp[3] = (byte) ((data << 24) >> 24);
    tmp[0] = (byte) (data & 0xff);
    write(tmp);
  }

  /**
   * Write data directly into the output buffer
   */
  @Override
  public void write(byte[] data, int off, int len) throws IOException {
    byte[] tmp = new byte[len];

    System.arraycopy(data, off, tmp, 0, len);
    write(tmp);
  }

  @Override
  public synchronized void write(byte[] data) throws IOException {
    if (data.length + bufferPos > buffer.length) {
      // fragment and send in chunks
      int offset = 0;
      while (offset < data.length) {
        int l = Math.min(buffer.length - bufferPos, data.length - offset);
        byte[] frag = new byte[l];
        System.arraycopy(data, offset, frag, 0, frag.length);
        write(frag, 0, l);
        offset += l;
        // this does not have to be true for the last fragment
        if (bufferPos == buffer.length) {
          publish(new TypedMap());
        }
      }
    } else {
      System.arraycopy(data, 0, buffer, bufferPos, data.length);
      bufferPos += data.length;
      if (bufferPos == buffer.length) {
        TypedMap map = new TypedMap();
        publish(map);
      }
    }
  }

  /**
   * Flush the output stream
   */
  @Override
  public synchronized void flush() throws IOException {
    TypedMap map = new TypedMap();
    publish(map);
  }

  @Override
  public synchronized void close() throws IOException {
    super.close();
    TypedMap map = new TypedMap();
    map.putBoolean("eof", true);
    publish(map);
  }

  /**
   * Actually publish the message.
   * 
   * If the buffer is full, write all of it via the publisher, otherwise (e.g.
   * when flushed) publish just the subset with actual data.
   * 
   * @param map
   *          Meta information to be included with the message.
   */
  private synchronized void publish(TypedMap map) {
    map.putInt("sequence", seq++);
    Blob b = new Blob();
    if (bufferPos == buffer.length) {
      // send the whole buffer
      b.write(buffer);
    } else {
      // send only the subset with actual data
      byte[] rest = new byte[bufferPos];
      System.arraycopy(buffer, 0, rest, 0, bufferPos);
      b.write(rest);
    }
    //System.out.println("MundoOutputStream publishing on " + pub.getChannel().getName());
    // pub.getChannel().getName());
    Message msg = new Message(map);
    msg.put("data", "bin", b);
    pub.send(msg);
    bufferPos = 0;
  }
}
