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

import org.mundo.rt.TypedMap;
import org.mundo.rt.Message;

/**
 * Transport services implement this interface. A transport service provides
 * connections to adjacent nodes. Since this definition should provide a unified
 * interface to transport services, methods must not throw connection technology
 * specific exceptions.
 *
 * @author Erwin Aitenbichler
 */
public interface ITransportService
{
  /**
   * Connects to the specified peer. The method asynchronously starts a connect
   * operation to the specified peer. The map contains service-specific parameters
   * and is usually provided in neighbor messages.
   *
   * @param map  a map containing the parameters.
   * @return  <code>true</code>: Indicates that the operation was successfully
   *          started; or if a connection to the specified address and port is
   *          already open; or a connect operation is pending.<br/>
   *          <code>false</code>: if a parameter is missing in the map;
   *          or if an immediate error occurred.
   */
  public boolean openAsync(TypedMap map);
  /**
   * Connects to the specified peer. The method asynchronously starts a connect
   * operation to the specified peer.
   *
   * @param link  the transport link to open.
   * @return  <code>true</code>: Indicates that the operation was successfully
   *          started; or if a connection to the specified address and port is
   *          already open; or a connect operation is pending.<br/>
   *          <code>false</code>: if the specified host is unknown.
   */
  public boolean openAsync(TransportLink link);
  /**
   * Sends the specified message over the specified route. If the route does
   * not have an open connection, the operation fails.
   * @param link   the transport link object.
   * @param msg    the message to send.
   * @return     <code>true</code> if the message could be sent successfully;
   *             <code>false</code> otherwise.
   */
  public boolean send(TransportLink link, Message msg);
  
  /**
   * Signals emitted by a transport service on connection changes.
   * @author Erwin Aitenbichler
   */
  public interface IConn
  {
    /**
     * Raised when a new transport link becomes available.
     * @param link  the transport link object.
     */
    public void linkAdded(TransportLink link);
    /**
     * Raised when a transport link was removed.
     * @param link  the transport link object.
     */
    public void linkRemoved(TransportLink link);
    /**
     * Raised when a new transport connection has been established.
     * @param link  the transport link object.
     */
    public void connectionOpened(TransportLink link);
    /**
     * Raised when a transport connection has been closed.
     * @param link  the transport link object.
     */
    public void connectionClosed(TransportLink link);
  }
}
