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

package org.mundo.net;

import java.util.ArrayList;
import org.mundo.rt.TypedMap;
import org.mundo.rt.ProtocolStack;

public class ProtocolStackBuilder
{
  public ProtocolStackBuilder()
  {
  }
  public void add(Class c)
  {
    list.add(new ProtocolStack.Handler(c, new TypedMap()));
  }
  public ProtocolStack getStack()
  {
    ProtocolStack.Handler prev = null;
    for (ProtocolStack.Handler h : list)
    {
      h.setUp(prev);
      if (prev!=null)
        prev.setDown(h);
      prev = h;
    }
    return new ProtocolStack(list);
  }

  private ArrayList<ProtocolStack.Handler> list = new ArrayList<ProtocolStack.Handler>(); 
}
