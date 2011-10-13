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
 * This is the base class for types.
 * @author Erwin Aitenbichler
 */
public class MType extends MMeta
{
  /**
   * Initializes a new type object.
   */
  public MType()
  {
  }
  /**
   * Sets the language-specific type name.
   */
  public void setName(String n)
  {
    name=n;
  }
  /**
   * Returns the language-specific type name.
   */
  public String getName()
  {
    return name;
  }
  /**
   * Sets the language.
   */
  public void setLang(String l)
  {
    lang=l;
  }
  /**
   * Returns the language.
   */
  public String getLang()
  {
    return lang;
  }
  /**
   * Returns the type code.
   */
  public String getCode()
  {
    return null;
  }
  /**
   * Returns a string representation of this object.
   */
  public String toString()
  {
    return lang+":"+name;
  }
  
  private String name;
  private String lang;
}
