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

package org.mundo.service;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.mundo.rt.TypedMap;

/**
 * The base class for all mcc-generated RMI server stubs.
 */
public class RMIStub extends UnicastRemoteObject
{
  public RMIStub() throws RemoteException
  {
  }
  protected boolean isLocal()
  {
    // If we have a local reference, then the object is local
    if (localObj!=null)
      return true;
    // If we previously tried to resolve the object, then return the cached answer
    if (notLocal)
      return false;
    // Try to resolve the object
    localObj = RMIInvocationService.getInstance().getLocalObj(name);
    if (localObj==null)
    {
      notLocal=true;
      return false;
    }
    return true;
  }
  protected TypedMap invoke(TypedMap m) throws RemoteException
  {
    return new TypedMap();
  }
  public String name;
  protected Object localObj;
  private boolean notLocal=false;
}
