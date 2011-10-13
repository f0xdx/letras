package org.mundo.xml.xqparser;

// This is in here solely to debug the parser

/* Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */


//package VTransformer;

import java.io.PrintStream;

public class UnparseVisitor implements XParserVisitor
{

  protected PrintStream out;


  public UnparseVisitor(PrintStream o)
  {
    out = o;
  }


  public Object print(SimpleNode node, Object data) {
    Token t1 = node.getFirstToken();
    Token t = new Token();
    t.next = t1;

    SimpleNode n;
    for (int ord = 0; ord < node.jjtGetNumChildren(); ord++) {
      n = (SimpleNode)node.jjtGetChild(ord);
      while (true) {
	t = t.next;
	if (t == n.getFirstToken()) break;
	print(t);
      }
      n.jjtAccept(this, data);
      t = n.getLastToken();
    }

    while (t != node.getLastToken()) {
      t = t.next;
      print(t);
    }
    return data;
  }


  protected void print(Token t) {
    Token tt = t.specialToken;
    if (tt != null) {
      while (tt.specialToken != null) tt = tt.specialToken;
      while (tt != null) {
        out.print(addUnicodeEscapes(tt.image));
        tt = tt.next;
      }
    }
    out.print(addUnicodeEscapes(t.image));
  }


  private String addUnicodeEscapes(String str) {
    String retval = "";
    char ch;
    for (int i = 0; i < str.length(); i++) {
      ch = str.charAt(i);
      if ((ch < 0x20 || ch > 0x7e) &&
	  ch != '\t' && ch != '\n' && ch != '\r' && ch != '\f') {
  	String s = "0000" + Integer.toString(ch, 16);
  	retval += "\\u" + s.substring(s.length() - 4, s.length());
      } else {
        retval += ch;
      }
    }
    return retval;
  }


  public Object visit(SimpleNode node, Object data)
  {
    return print(node, data);
  }


}
