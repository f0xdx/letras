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

package org.mundo.util.plugins;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureClassLoader;

/**
 * A common supertype for classloaders that are used in plugins. A plugin class
 * loader always work on a File object that represents the source of the plugin.
 * Which file is going to be used depends on the acutal subtype. There may be
 * JARs, ordinary files or some things.
 * 
 * Subclasses MUST redefine the <code>findClass</code> and
 * <code>getResourceAsStream</code> methods in order to make use of this
 * abstract class loader. They may use the loadClassData for easy access to a
 * class data element.
 * 
 * @author AHa
 */
abstract class PluginClassLoader extends SecureClassLoader {

  /**
   * This size of a chunk of data to be read
   */
  private static final int CHUNKSIZE = 8192;

  /**
   * Contains the file to load classes from
   */
  protected File sourcefile;

  /**
   * Create a new PluginClassLoader using the system's default class loader as a
   * parent instance.
   * 
   * @param parent the parent class loader of this instance
   * @param sourcefile the file to load the classes from
   */
  public PluginClassLoader(File sourcefile) {
    super();
    init(sourcefile);
  }

  /**
   * Create a new PluginClassLoader using a given class loader as the parent
   * instance.
   * 
   * @param parent the parent class loader of this instance
   * @param sourcefile the file to load the classes from
   */
  public PluginClassLoader(ClassLoader parent, File sourcefile) {
    super(parent);
    init(sourcefile);
  }

  /**
   * Initializes this class instance
   * 
   * @param jarfile the file this instance should load classes from
   */
  protected void init(File sourcefile) {
    this.sourcefile = sourcefile;
  }

  /**
   * Convert the binary name to a valid filename accoding to the Java class
   * placing schematics by substituting dots by slashes and adding the extension
   * ".class"
   * 
   * @param binaryname the binary name of a Java class
   * @return the path denoted by this binary name
   */
  protected String binaryNameToPath(String binaryname) {
    return binaryname.replace('.', '/') + ".class";
  }

  /**
   * Loads the class data from a given input stream.
   * 
   * @param in the input stream where to read teh data from
   * @param size the size of the data in bytes or -1 if the size is not known
   * @return a byte array containing the class data
   * @throws IOException if reading fails for any reason
   */
  protected final byte[] loadClassData(InputStream in, int size) throws IOException {
    byte[] data;
    try {
      if(size != -1) {
        // size is known - load it as a whole
        data = new byte[size];
        int offset = 0;
        while(offset != size) {
          int len = in.read(data, offset, size - offset);
          if(len == -1)
            throw new IOException("Wrong size of input data");
          offset += len;
        }
      } else {
        // read data in chunks
        byte chunk[] = new byte[CHUNKSIZE];
        int partsize;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(CHUNKSIZE);
        while((partsize = in.read(chunk)) == CHUNKSIZE)
          buffer.write(chunk, 0, partsize);
        data = buffer.toByteArray();
      }
    } finally {
      in.close();
    }
    return data;
  }

}
