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

import java.io.IOException;
import java.io.StringReader;
import java.io.StreamTokenizer;

import org.mundo.rt.GUID;
import org.mundo.rt.Logger;

/**
 * A parser for the MundoCore Compact Interface Format used internally by stubs.
 */
public class MCIFParser
{
  public MCIFParser()
  {
  }
  public MInterface parseInterface(String interfaceName, String text) throws IOException
  {
    StreamTokenizer st = new StreamTokenizer(new StringReader(text));
    st.wordChars('$', '$');
    st.nextToken();
    MInterface mif=new MInterface(interfaceName);
    while (st.ttype!=st.TT_EOF)
    {
      if (st.ttype==st.TT_WORD)
        parseMethod(st, mif);
      else
      {
        log.warning("unknown token: "+st.ttype);
        st.nextToken();
      }
    }
    return mif;
  }
  public MClass parseClass(String className, String text) throws IOException
  {
    StreamTokenizer st = new StreamTokenizer(new StringReader(text));
    st.wordChars('$', '$');
    st.nextToken();
    MClass mcls = new MClass(className);
    while (st.ttype != st.TT_EOF)
    {
      if (st.ttype != st.TT_WORD)
      {
        parseError(st, "field type expected");
        return null;
      }
      MType type = parseTypecode(st);
      if (st.ttype != st.TT_WORD)
      {
        parseError(st, "field name expected");
        return null;
      }
      MField field = new MField(st.sval, type);
      mcls.addField(field);
      st.nextToken();
      if (st.ttype == ';')
        st.nextToken();
    }
    return mcls;
  }
  private boolean parseMethod(StreamTokenizer st, MInterface mif) throws IOException
  {
    if (st.ttype!=st.TT_WORD)
    {
      parseError(st, "return type expected");
      return false;
    }
    MType returnType=parseTypecode(st);
    if (st.ttype!=st.TT_WORD)
    {
      parseError(st, "method name expected");
      return false;
    }
    MMethod mtd=new MMethod(st.sval);
    mtd.setReturnType(returnType);
    st.nextToken();
    if (st.ttype!='(')
    {
      parseError(st, "'(' expected");
      return false;
    }
    st.nextToken();
    int i=0;
    while (st.ttype!=st.TT_EOF && st.ttype!=')')
    {
      if (st.ttype==st.TT_WORD)
      {
        mtd.addParam(new MParam("p"+i, parseTypecode(st)));
        i++;
      }
      if (st.ttype==',')
        st.nextToken();
      else if (st.ttype!=')')
      {
        parseError(st, "unexpected token "+st.ttype+" while parsing parameter list");
        return false;
      }
    }
    st.nextToken();
    mif.addMethod(mtd);
    return true;
  }
  private MType parseTypecode(StreamTokenizer st) throws IOException
  {
    String tc = st.sval;
    st.nextToken();
    MType t;
    if (tc.length()==1)
    {
      switch (tc.charAt(0))
      {
        case 's':
          t = new MClassType(String.class);
          break;
        case 'g':
          t = new MClassType(GUID.class);
          break;
        default:
          t = new MBaseType(tc);
      }
    }
    else
      t = new MClassType(tc);
    if (st.ttype=='[')
    {
      int dim=0;
      while (st.ttype=='[')
      {
        st.nextToken();
        dim++;
      }
      t = new MArray(t, dim);
    }
    return t;
  }
  private static void parseError(StreamTokenizer st, String err) throws IOException
  {
    throw new IOException(err+" "+st.toString());
  }
  
  private Logger log = Logger.getLogger("mcif");
}
