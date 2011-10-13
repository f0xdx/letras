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

package org.mundo.net;

import java.io.IOException;
import java.io.InputStream;
import org.mundo.rt.Blob;
import org.mundo.rt.Message;
import org.mundo.net.ISerialize;

/**
 * @deprecated
 * @see BinSerializationHandler
 */
public class BinSerializerBundle implements ISerialize
{
  /**
   * Serializes a message.
   * @param msg  a message containing a number of chunks. A chunk can either
   *             be a map containing a passive object tree or a blob containing
   *             binary data.
   */
  public void serialize(Message msg) throws IOException
  {
    BinSerializer ser=new BinSerializer();
    ser.serialize(msg);
  }

  /**
   * Deserializes a message.
   * @param msg  a message containing blobs with the serialized data.
   */
  public void deserialize(Message msg) throws IOException
  {
    BinDeserializer deser=new BinDeserializer();
    deser.deserialize(msg);
  }

  /**
   * Deserializes an object.
   * @param is  an input stream providing the serialized data.
   * @return  the deserialized object.
   */
  public Object deserializeObject(InputStream is) throws IOException
  {
    Blob blob=new Blob();
    blob.readFrom(is);
    BinDeserializer deser=new BinDeserializer();
    return deser.deserialize(blob);
  }

  /**
   * Returns the chunk type string of a serialized chunk, which is
   * <code>binser</code>.
   * @return  the chunk type string of a serialized chunk.
   */  
  public String getChunkType()
  {
    return "binser";
  }
}
