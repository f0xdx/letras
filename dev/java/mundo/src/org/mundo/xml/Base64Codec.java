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
 * Encodes and decodes binary data as Base64.
 */
public class Base64Codec
{
  public Base64Codec()
  {
  }
  public void encode(Blob out, Blob in)
  {
    byte[] indata=in.getBuffer();
    int ixtext;
    int lentext;
    int ctremaining;
    int inbuf[]=new int[3];
    byte outbuf[]=new byte[4];
    int i;
    int charsonline=0;
    
    ixtext=0;
    lentext=indata.length;
    for(;;)
    {
      ctremaining=lentext-ixtext;
      if (ctremaining<=0)
        break;

      if (ixtext+3<=lentext)
      {
        inbuf[0]=b2i(indata[ixtext++]);
        inbuf[1]=b2i(indata[ixtext++]);
        inbuf[2]=b2i(indata[ixtext++]);
      }
      else
      {
        for (i=0; i<lentext-ixtext; i++)
          inbuf[i]=b2i(indata[ixtext+i]);
        for (; i<3; i++)
          inbuf[i]=0;
        ixtext+=3;
      }
      
      outbuf[0]=encodingTable[(inbuf[0]&0xFC)>>2];
      outbuf[1]=encodingTable[((inbuf[0]&0x03)<<4)|((inbuf[1]&0xF0)>>4)];
      outbuf[2]=encodingTable[((inbuf[1]&0x0F)<<2)|((inbuf[2]&0xC0)>>6)];
      outbuf[3]=encodingTable[inbuf[2]&0x3F];

      if (ctremaining<3)
      {
        outbuf[3]='=';
        if (ctremaining<2)
          outbuf[2]='=';
      }
      out.write(outbuf);
      charsonline+=4;
      if (linelength>0 && charsonline>=linelength)
      {
        charsonline=0;
        if (ixtext<lentext)
          out.write("\n");
      }
    }
  }
  
  // FIXME: b&0xff instead of b2i(b) should do
  private static int b2i(byte b)
  {
    if (b<0)
      return 0x100+(int)b;
    return (int)b;
  }

  public void decode(Blob out, String in)
  {
    decode(out, in.getBytes());
  }
  
  public void decode(Blob out, Blob in)
  {
    decode(out, in.getBuffer());
  }

  private void decode(Blob out, byte[] indata)
  {
    int ixtext;
    int lentext;
    byte ch;
    int inbuf[]=new int[4];
    byte outbuf[]=new byte[3];
    int ixinbuf;
    boolean flendtext=false;
    boolean flbreak=false;
    
    ixtext=0;
    lentext=indata.length;
    ixinbuf=0;

    while (ixtext<lentext)
    {
      ch=indata[ixtext++];
      if (ch=='=')
        flendtext=true;
      ch=decodingTable[ch];
      if (ch>=0 || flendtext)
      {
        int ctcharsinbuf=3;
        if (flendtext)
        {
          if (ixinbuf==0)
            break;
          if ((ixinbuf==1) || (ixinbuf==2))
            ctcharsinbuf=1;
          else
            ctcharsinbuf=2;
          ixinbuf=3;
          flbreak=true;
        }
        inbuf[ixinbuf++]=ch;
        if (ixinbuf==4)
        {
          ixinbuf=0;
          outbuf[0]=(byte)((inbuf[0]<<2)|((inbuf[1]&0x30)>>4));
          outbuf[1]=(byte)(((inbuf[1]&0x0F)<<4)|((inbuf[2]&0x3C)>>2));
          outbuf[2]=(byte)(((inbuf[2]&0x03)<<6)|(inbuf[3]&0x3F));
          out.write(outbuf, 0, ctcharsinbuf);
        }
        if (flbreak)
          break;
      }
    }
  }

  private int linelength=72;

  private static final byte encodingTable[]={
    'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
    'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
    'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
    'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/'
  };
  private static byte decodingTable[]=new byte[128];
  
  static
  {
    int i;
    for (i=0; i<128; i++)
      decodingTable[i]=-1;
    for (i=0; i<64; i++)
      decodingTable[encodingTable[i]]=(byte)i;
  }
}
