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

/**
 * Objects of this class represent parameters.
 * @author Erwin Aitenbichler
 */
public class MField extends MMeta
{
  /**
   * Initializes a new parameter object.
   */
  public MField(String name, MType type)
  {
    this.name = name;
    this.type = type;
  }
  /**
   * Returns the parameter name.
   */
  public String getName()
  {
    return name;
  }
  /**
   * Returns the parameter type.
   */
  public MType getType()
  {
    return type;
  }
  /**
   * Sets the class this field belongs to.
   */
  public void setMClass(MClass c)
  {
    mclass = c;
  }
  /**
   * Gets the class this field belongs to.
   */
  public MClass getMClass()
  {
    return mclass;
  }
  /**
   * Returns a string representation of this object.
   */
  public String toString()
  {
    StringBuffer sb=new StringBuffer();
    sb.append(type.toString()).append(" ").append(name);
    return sb.toString();
  }
  
  private String name;
  private MType type;
  private MClass mclass;
}
