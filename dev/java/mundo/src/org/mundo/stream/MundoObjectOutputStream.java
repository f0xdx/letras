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
import java.io.ObjectOutput;
import java.nio.ByteBuffer;

import org.mundo.rt.Channel;
import org.mundo.rt.Message;
import org.mundo.rt.Session;

public class MundoObjectOutputStream extends MundoOutputStream implements
    ObjectOutput {

  public MundoObjectOutputStream(Channel ch, Session s) {
    super(ch, s);
  }

  public MundoObjectOutputStream(String channelName, Session s) {
    super(channelName, s);
  }

  public MundoObjectOutputStream(String channelName) {
    super(channelName);
  }

  @Override
  public void writeObject(Object obj) throws IOException {
    pub.send(Message.fromObject(obj));
  }

  @Override
  public void writeBoolean(boolean v) throws IOException {
    if (v) {
      write(1);
    } else {
      write(0);
    }
  }

  @Override
  public void writeByte(int v) throws IOException {
    write(v);
  }

  @Override
  public void writeBytes(String s) throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(s.getBytes());
    write(bb.array());
  }

  @Override
  public void writeChar(int v) throws IOException {
    char c = (char)v;
    ByteBuffer bb = ByteBuffer.wrap(new byte[2]);
    bb.putChar(c);
    write(bb.array());
  }

  @Override
  public void writeChars(String s) throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(s.getBytes());
    write(bb.array());
  }

  @Override
  public void writeDouble(double v) throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
    bb.putDouble(v);
    write(bb.array());
  }

  @Override
  public void writeFloat(float v) throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
    bb.putFloat(v);
    write(bb.array());
  }

  @Override
  public void writeInt(int v) throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
    bb.putInt(v);
    write(bb.array());
  }

  @Override
  public void writeLong(long v) throws IOException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
    bb.putLong(v);
    write(bb.array());
  }

  @Override
  public void writeShort(int v) throws IOException {
    short s = (short)v;
    ByteBuffer bb = ByteBuffer.wrap(new byte[2]);
    bb.putShort(s);
    write(bb.array());
  }

  @Override
  public void writeUTF(String s) throws IOException {
    writeChars(s);
  }
}
