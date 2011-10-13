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
 * Daniel Schreiber
 */

package org.mundo.rt;

/**
 * Generic wrapper forwarding requests to internal serializiers.
 */
public class ClassWrapper extends Metaclass
{
  ClassWrapper(Class c, String n)
  {
    cls=c;
    extName=n;
    metaClass = null;
  }
  ClassWrapper(Class c, String n, Class metaClass)
  {
    cls=c;
    extName=n;
    this.metaClass = metaClass;
  }
  
  public String getExternalTypeName()
  {
    return extName;
  }
  public Class getJavaClass()
  {
    return cls;
  }
  public String toString()
  {
    return "class="+cls.getName()+", extName="+extName;
  }
  public Object newInstance() throws InstantiationException
  {
    try
    {
      return cls.newInstance();
    }
    catch(IllegalAccessException x)
    {
      throw new InstantiationException("illegal access: "+x.toString());
    }
  }
  public void passivate(Object o, TypedMap m) throws Exception
  {
    if (metaClass!=null) {
      Object o2 = metaClass.newInstance();
      ((Metaclass)o2).passivate(o,m);
	} else
	  ((IActivate)o)._passivate(m);
  }
  public void activate(Object o, TypedMap m, TypedMap ctx) throws Exception
  {
    if (metaClass!=null) {
      Object o2 = metaClass.newInstance();
      ((Metaclass)o2).activate(o,m, ctx);
	}
	else
	  ((IActivate)o)._activate(m, ctx);
  }
  public static void register(Class cls)
  {
    register(new ClassWrapper(cls, cls.getName()));
  }
  public static void register(Class cls, String extName)
  {
    register(new ClassWrapper(cls, extName));
  }
  public static void register(Class cls, String extName, Class metaClass)
  {
    register(new ClassWrapper(cls, extName, metaClass));
  }
  public static void register(String cn, String extName)
  {
    try
    {
      register(new ClassWrapper(Class.forName(cn), extName));
    }
    catch(Exception x)
    {
      Logger.getLogger("meta").warning(x.toString());
    }
  }
  public boolean isOSGi() {
    return metaClass!=null;
  }

  private Class cls;
  private String extName;
  private Class metaClass;
}
