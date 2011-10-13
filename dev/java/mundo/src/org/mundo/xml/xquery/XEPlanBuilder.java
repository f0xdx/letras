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

package org.mundo.xml.xquery;

import org.mundo.filter.AttributeFilter;

class XEPlanBuilder
{
  void document(String name)
  {
    plan.add(new XEDocument(name));
  }
  void forall(String nodeName)
  {
    plan.add(new XEForAll(nodeName));
  }
  void store(XQVar var)
  {
    plan.add(new XEStore(var.name));
  }
  void load(XQVar var)
  {
    plan.add(new XELoad(var.name));
  }
  void push(Object obj)
  {
    plan.add(new XEPush(obj));
  }
  void stringToLong()
  {
    plan.add(new XEStringToLong());
  }
  void stringToDouble()
  {
    plan.add(new XEStringToDouble());
  }
  void compare(int op)
  {
    plan.add(new XECompare(op));
  }
  void print()
  {
    plan.add(new XEPrint());
  }
  XEPlan plan=new XEPlan();
}
