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

import org.mundo.rt.GUID;
import org.mundo.rt.Semaphore;
import org.mundo.rt.IntegerMonitor;

/**
 * Abstract base class for all transport links. Transport link data structures
 * are shared between transport and routing services. They are created by
 * transport services when new neighbors are discovered and then added to
 * the routing tables of routing services. When sending messages to neighbors,
 * <code>TransportLink</code> data structures are used to indicate the
 * destinations.
 * 
 * @author Erwin Aitenbichler
 */
public abstract class TransportLink
{
  public TransportLink()
  {
    timeout = 10;
    metric = Integer.MAX_VALUE;
    rsNotified = false;
  }
  public abstract ITransportService getService();
  public abstract ITransportConnection getConnection();
  public int getConnectionTimeout()
  {
    return TIMEOUT_NA;
  }
  public int getMetric()
  {
    return metric;
  }

  public GUID      remoteId;
  public String    remoteName;
  public int       timeout;
  public boolean   rsNotified;
  public Semaphore sem = new Semaphore(1);
  public IntegerMonitor lease = new IntegerMonitor(0);

  protected int    metric;
  
  public static final int TIMEOUT_INFINITE = -1;
  public static final int TIMEOUT_NA       = -2;
}
