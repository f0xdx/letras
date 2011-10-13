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

import org.mundo.rt.Blob;

/**
 * Helper class to generate SOAP documents.
 * @author Erwin Aitenbichler
 */
public class SOAPDocument
{
  public SOAPDocument()
  {
  }
  public SOAPDocument(String request, String xmlns)
  {
    this.request=request;
    this.xmlns=xmlns;
  }
  public void writeHeader(Blob b)
  {
    b.write("<SOAP-ENV:Envelope "+
            "SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" "+
            "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
            "xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\">\n"+
            "<SOAP-ENV:Body>\n"+
            "<m:"+request+" xmlns:m=\""+xmlns+"\">\n");
  }
  public void writeTrailer(Blob b)
  {
    b.write("</m:"+request+">\n"+
            "</SOAP-ENV:Body>\n"+
            "</SOAP-ENV:Envelope>\n");
  }
  protected String xmlns;
  protected String request;
}
