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

package org.mundo.xml;

import java.io.FilterWriter;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Formats an XML document by adding line breaks and indentations. There are two
 * different ways to use this class:
 * <ul>
 * <li>Create an instance of <code>XMLFormatter</code> and use it as a
 *     <code>FilterWriter</code>. Example:<pre>
 *     XMLFormatter fmt=new XMLFormatter(new FileWriter("out.xml"));
 *     fmt.write(...);</pre></li>
 * <li>Call the static method <code>format</code> to format a string.</li>
 * </ul>
 * 
 * @author Erwin Aitenbichler
 */
public class XMLFormatter extends FilterWriter
{
  /**
   * Creates a new <code>XMLFormatter</code>.
   * @param baseWriter  a Writer object to provide the underlying stream.
   */
  public XMLFormatter(Writer baseWriter)
  {
    super(baseWriter);
    lineLength=Integer.MAX_VALUE;
    hardBreaks=false;
  }
  /**
   * Sets the maximum length of a line.
   * @param l   Maximum number of characters in a line.
   * @param hb  Permit breaks at non-whitespace positions.
   */
  public void setLineLength(int l, boolean hb)
  {
    lineLength=l;
    hardBreaks=hb;
  }
  /**
   * Closes the stream.
   * @throws IOException  if an I/O error occurs
   * @see java.io.FilterWriter#close()
   */
  public void close() throws IOException
  {
    flush();
    out.close();
  }
  /**
   * Flushes the stream.
   * @throws IOException  if an I/O error occurs
   * @see java.io.FilterWriter#flush()
   */
  public void flush() throws IOException
  {
    if (buf!=-1)
    {
      if (buf=='\n')
      {
        writeIndent();
        col=0;
      }
      else
        out.write(buf);
      buf=-1;
      if (end)
        writeIndent();
    }
    out.flush();
  }
  /**
   * Writes a portion of a string.
   * @param s    String to be written.
   * @param off  Offset from which to start reading characters.
   * @param len  Number of characters to be written.
   * @see java.io.FilterWriter#write(String,int,int)
   */
  public void write(String s, int off, int len) throws IOException
  {
    for (int i=0; i<len; i++)
      write(s.charAt(i+off));
  }
  /**
   * Writes a portion of an array of characters.
   * @param b    String to be written.
   * @param off  Offset from which to start reading characters.
   * @param len  Number of characters to be written.
   * @see java.io.FilterWriter#write(char[],int,int)
   */  
  public void write(char[] b, int off, int len) throws IOException
  {
    for (int i=0; i<len; i++)
      write(b[i+off]);
  }
  /**
   * Writes a single character.
   * @param c  int specifying a character to be written.
   * @see java.io.FilterWriter#write(int)
   */  
  public void write(int c) throws IOException
  {
    if (buf=='<')
    {
      if (c=='/')
        endElement();
      else if (c!='?')
        startElement();
    }
    else if (buf=='/' && c=='>')
    {
      end=true;
      level--;
    }
    if (buf!=-1)
    {
      if (buf=='\n')
      {
        writeIndent();
        col=0;
      }
      else
        out.write(buf);
      if (++col>=lineLength && (hardBreaks || Character.isWhitespace((char)c)))
      {
        out.write("\n");
        col=(level-1)*2;
        for (int i=0; i<level-1; i++)
          out.write("  ");
        if (Character.isWhitespace((char)c))
          c=-1;
      }
    }
    buf=c;
  }
  /**
   * Formats the XML document passed as string.
   * @param s   String to format.
   * @throws IOException
   */
  public static String format(String s) throws IOException
  {
    StringWriter out=new StringWriter();
    PrintWriter w=new PrintWriter(new XMLFormatter(out));
    w.print(s);
    w.flush();
    return out.toString();
  }
  /**
   * Formats the XML document passed as string.
   * @param s   String to format.
   * @param l   Maximum number of characters in a line.
   * @param hb  Permit breaks at non-whitespace positions.
   * @throws IOException
   */
  public static String format(String s, int l, boolean hb) throws IOException
  {
    StringWriter out=new StringWriter();
    XMLFormatter f=new XMLFormatter(out);
    f.setLineLength(l, hb);
    PrintWriter w=new PrintWriter(f);
    w.print(s);
    w.flush();
    return out.toString();
  }

  private void startElement() throws IOException
  {
    if (start || end)
      writeIndent();
    start=true;
    level++;
  }
  private void endElement() throws IOException
  {
    level--;
    if (end)
      writeIndent();
    end=true;
  }
  private void writeIndent() throws IOException
  {
    out.write("\n");
    col=level*2;
    for (int i=0; i<level; i++)
      out.write("  ");
    start=end=false;
  }
  
  private int buf=-1;
  private int level=0;
  private int col=0;
  private boolean start=false;
  private boolean end=false;
  private int lineLength;
  private boolean hardBreaks;
}
