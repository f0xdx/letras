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

package org.mundo.net.routing;

import org.mundo.rt.GUID;

/**
 * The basic interface for routing services.
 */
public interface IRoutingService
{
  /**
   * Signals emitted by a routing service on connection changes.
   * @author Erwin Aitenbichler
   */
  public interface IConn
  {
    /**
     * Raised when a new node is available.
     * @param id  the GUID of the new node.
     */
    public void nodeAdded(GUID id);
    /**
     * Raised when no routes are left to a node.
     * @param id  the GUID of the node lost.
     */
    public void nodeRemoved(GUID id);
  }
}
