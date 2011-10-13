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
 * logical or
 */
public class XQOr extends XQObject
{
  XQOr(XQObject l, XQObject r)
  {
    left=l;
    right=r;
  }
  void buildMapFilter(XQMapBuilder ctx) throws XQuery.BuildException
  {
    throw new XQuery.BuildException("the 'or' operation is not supported by map filters");
  }
  public XQObject[] children()
  {
    return new XQObject[] { left, right };
  }
  public String toString()
  {
    return left.toString()+" or "+right.toString();
  }
  public XQObject left;
  public XQObject right;
}
