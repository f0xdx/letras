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

package org.mundo.reflect;

import org.mundo.rt.GUID;

/**
 * Note: Since we can get class names from a remote node, we might not have
 * the corresponding class objects locally.
 */
public class MClassType extends MType
{
  public MClassType(Class c)
  {
    cls=c;
  }
  public MClassType(String cn)
  {
    clsName=cn;
  }
  /**
   * Returns a string representation for this object.
   */
  public String toString()
  {
    if (clsName!=null)
      return clsName;
    return cls.toString();
  }
  /**
   * Returns the type code.
   */
  public String getCode()
  {
    if (clsName!=null)
      return clsName;
    if (cls==String.class)
      return "s";
    if (cls==GUID.class)
      return "g";
    return cls.getName();
  }
  
  private String clsName;
  private Class cls;
}
