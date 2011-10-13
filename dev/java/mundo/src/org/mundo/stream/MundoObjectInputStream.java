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
import java.io.ObjectInput;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;

import org.mundo.rt.Channel;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Session;

public class MundoObjectInputStream extends MundoInputStream implements
    ObjectInput {

  @SuppressWarnings("unchecked")
  protected Deque objectQueue = new LinkedList();

  public MundoObjectInputStream(Channel ch, Session s) {
    super(ch, s);
  }

  public MundoObjectInputStream(String channelName, Session s) {
    super(channelName, s);
  }

  public MundoObjectInputStream(String channelName) {
    super(channelName);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void received(Message msg, MessageContext ctx) {
    super.received(msg, ctx);
    synchronized (this) {
      if (msg.getMap() != null && msg.getMap().containsKey("eof")
          && msg.getMap().getBoolean("eof")) {
        eof = true;
      }
      if (msg.getMap() != null && msg.getMap().containsKey("object") && msg.getObject() != null) {
        try {
          objectQueue.add(msg.getObject());
          notify();
        } catch (ClassCastException cce) {
          log.exception(cce);
        }
      }
    }
  }

  private void waitForObject() throws IOException {
    synchronized (this) {
      // wait for new data to arrive
      long deadline = System.currentTimeMillis() + TIMEOUT;
      while (objectQueue.size() == 0 && !eof) {
        try {
          long d = deadline - System.currentTimeMillis();
          if (d < 1)
            throw new IOException("Timeout waiting for new data");
          wait(d);
        } catch (IllegalMonitorStateException x) {
          // current thread is not the owner of the object's monitor;
          // won't happen
          log.exception(x);
        } catch (InterruptedException x) {
          // TODO handle this case
          // new IOException("Wait has been interrupted", x);
          log.warning("Wait has been interrupted");
        }
      }
    }
  }

  @Override
  public Object readObject() throws ClassNotFoundException, IOException {
    waitForObject();
    return objectQueue.removeFirst();
  }

  @Override
  public boolean readBoolean() throws IOException {
    return (read() > 0);
  }

  @Override
  public byte readByte() throws IOException {
    return (byte) read();
  }

  @Override
  public char readChar() throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[2]);
    int read = 0;
    while(read < bb.array().length) {
      read += read(bb.array());
    }
    return bb.getChar();
  }

  @Override
  public double readDouble() throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
    int read = 0;
    while(read < bb.array().length) {
      read += read(bb.array());
    }
    return bb.getDouble();
  }

  @Override
  public float readFloat() throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
    int read = 0;
    while(read < bb.array().length) {
      read += read(bb.array());
    }
    return bb.getFloat();
  }

  @Override
  public void readFully(byte[] b) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void readFully(byte[] b, int off, int len) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readInt() throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
    int read = 0;
    while(read < bb.array().length) {
      read += read(bb.array());
    }
    return bb.getInt();
  }

  @Override
  public String readLine() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readLong() throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
    int read = 0;
    while(read < bb.array().length) {
      read += read(bb.array());
    }
    return bb.getLong();
  }

  @Override
  public short readShort() throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[2]);
    int read = 0;
    while(read < bb.array().length) {
      read += read(bb.array());
    }
    return bb.getShort();
  }

  @Override
  public String readUTF() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readUnsignedByte() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readUnsignedShort() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int skipBytes(int n) throws IOException {
    byte[] tmp = new byte[n];
    int read = 0;
    while(read < tmp.length) {
      read += read(tmp, read, tmp.length - read);
    }
    return read;
  }
}
