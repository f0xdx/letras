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

package org.mundo.net.transport.ip;

import java.net.DatagramSocket;
import java.net.SocketException;

class NetExtImpl
{
  void setReusePort(DatagramSocket sock, boolean reuse) throws SocketException
  {
    setSockOpt(sock, SO_REUSEPORT, reuse ? 1 : 0);
  }

  void setSockOpt(DatagramSocket sock, int field, int value) throws SocketException
  {
    int rc = setsockopt(sock, field, value);
    if (rc<0)
      throw new SocketException("setsockopt failed with "+rc);
  }
  
  static private native int setsockopt(DatagramSocket sock, int field, int value);

  private static final int SO_REUSEPORT = 0x200;

  static
  {
    System.loadLibrary("mundoext");
  }
}
