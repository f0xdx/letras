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

import java.net.InetAddress;

import org.mundo.rt.GUID;
import org.mundo.rt.TypedMap;
import org.mundo.net.transport.ITransportConnection;

/**
 * IP-based transport connections implement this interface. A transport
 * connection is a point-to-point link to an adjacent node. Transport
 * connections are opened and managed by transport services.
 *
 * @see IPTransportService
 * @author Erwin Aitenbichler
 */
public interface IIPTransportConnection extends ITransportConnection
{
  /**
   * Returns the IP address of the remote peer.
   * @return  the IP address of the remote peer.
   */
  public InetAddress getRemoteAddress();
  /**
   * Returns the remote port number.
   * @return  the remote port number.
   */
  public int getRemotePort();
  /**
   * Waits until all messages in the send queue have been sent and closes
   * the socket.
   * @return  <code>true</code> if it was a clean shutdown, or
   *          <code>false</code> otherwise.
   */
  public boolean disconnectWait();
  /**
   * Called by the service during connection setup to provide information
   * about the peer.
   *
   * Issues with this method:
   * - nodeId should be stored in the route, not in the connection.
   * - addr and port are sometimes initialized through constructors and
   *   sometimes via this method. There should only be one way to do it.
   * - Many options are written to the route object by code in the
   *   connection implementation. This is pointless. The service should
   *   directly write these options to the route.
   *
   * @param nodeId  node ID of the peer
   * @param addr    remote internet address
   * @param port    remote port number
   * @param opts    a map containing optional information
   */
  void init(GUID nodeId, InetAddress addr, int port, TypedMap opts);
  /**
   * Returns the state of this connection.
   * @return  the state of this connection.
   */
  public int getState();

  static final int STATE_NULL                 = 0;
  static final int STATE_CONNECTING_1         = 1;
  static final int STATE_CONNECTING_2         = 2;
  static final int STATE_CONNECTED            = 3;
  static final int STATE_DISCONNECT_REQUESTED = 4;
  static final int STATE_SELF_DISCONNECTING   = 5;
  static final int STATE_DISCONNECTED         = 6;
  static final int STATE_PEER_DISCONNECTED    = 7;
  static final int STATE_SELF_DISCONNECTED    = 8;
  static final int STATE_DISCONTINUED         = 9;
}
