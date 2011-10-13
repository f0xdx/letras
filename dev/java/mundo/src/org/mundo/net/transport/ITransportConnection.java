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

package org.mundo.net.transport;

import java.io.IOException;

import org.mundo.rt.GUID;
import org.mundo.rt.Message;

/**
 * Transport connections implement this interface. A transport connection is
 * a point-to-point link to an adjacent node. Transport connections are
 * opened and managed by transport services.
 *
 * @see ITransportService
 * @author Erwin Aitenbichler
 */
public interface ITransportConnection
{
  /**
   * Returns the associated transport link object.
   * @return  the associated transport link object.
   */
  public TransportLink getLink();
  /**
   * Returns the metric value of this connection. The routing service
   * always tries to use the connection with the lowest value. This method
   * always returns a valid value. If the connection is closed, the
   * returned value is as if the connection were open (or only minimally
   * different).
   * @return  the metric value.
   */
  public int getMetric();

  /**
   * Opens the connection.
   */
  public boolean connect() throws Exception;

  /**
   * Closes the connection.
   */
  public void disconnect();

  /**
   * Returns whether the connection is open.
   * @return  <code>true</code> if the connection is open;
   *          <code>false</code> otherwise.
   */
  public boolean isConnected();

  /**
   * Periodically called by a routing service to update the timeout counters
   * and to close unused connections.
   * @return  true if the connection is still open, false if not.
   */
  public boolean checkTimeout(int elapsed);
  
  /**
   * Sends the specified message to the remote peer.
   * @param msg  the message to send.
   */
  public boolean send(Message msg);
}
