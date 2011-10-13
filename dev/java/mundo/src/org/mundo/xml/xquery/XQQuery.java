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

/**
 * query
 */
public class XQQuery extends XQObject
{
  XQQuery()
  {
  }
  void buildMapFilter(XQMapBuilder ctx) throws XQuery.BuildException
  {
    fors.buildMapFilter(ctx);
    cond.buildMapFilter(ctx);
  }
  void buildPlan(XEPlanBuilder ctx) throws XQuery.BuildException
  {
    fors.buildPlan(ctx);
    if (cond!=null)
      cond.buildPlan(ctx);
    if (ret!=null)
      ret.buildPlan(ctx);
  }
  public String toString()
  {
    StringBuffer sb=new StringBuffer();
    sb.append(fors.toString());
    if (cond!=null)
    {
      sb.append(" ");
      sb.append(cond.toString());
    }
    return sb.toString();
  }
  public XQFor       fors;
  public XQObject    cond;
  public XQReturn    ret;
}
