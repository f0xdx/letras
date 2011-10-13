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
 * Objects of this class represent arrays.
 */
public class MArray extends MType
{
  public MArray(MType type)
  {
    componentType = type;
    dimension = 1;
  }
  public MArray(MType type, int dim)
  {
    componentType = type;
    dimension = dim;
  }
  public MType getComponentType()
  {
    return componentType;
  }
  public int getDimension()
  {
    return dimension;
  }
  /**
   * Returns the type code.
   */
  public String getCode()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(componentType.getCode());
    for (int i=0; i<dimension; i++)
      sb.append("[");
    return sb.toString();
  }
  /**
   * Returns a string representation for this object.
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(componentType.toString());
    for (int i=0; i<dimension; i++)
      sb.append("[]");
    return sb.toString();
  }
 
  private MType componentType;
  private int dimension;
}
