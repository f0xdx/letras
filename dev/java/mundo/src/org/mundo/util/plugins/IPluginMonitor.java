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

import java.util.jar.JarFile;

import org.mundo.util.SyntaxErrorException;

import org.w3c.dom.Node;

/**
 * An IPluginMonitor is responsible for handling a specific namespace prefix
 * in the plugin configuration file.
 * 
 * @author AHa
 */
public interface IPluginMonitor
{
  /**
   * This interface defines the events raised by a plugin monitor. It gets called
   * for each element under the root element that has this prefix and is responsible
   * to perform whatever actions that are defined by the plugin.
   * 
   * @author AHa
   */
  public interface IPluginHandler
  {
    /**
     * Called whenever a new plugin is registered for the specific
     * IPluginHandler. It may throw a SyntaxErrorException if the configuration
     * is missing required parts. The method has transaction semantics: In case
     * an exception gets thrown, the system's state after calling
     * pluginRegistered MUST remain the same as before calling it.
     * 
     * @param namespaceuri the XML namespace of the current configuration node
     * @param configuration the XML configuration to parse
     * @param sourcefile the JarFile which contained the plugin defintion
     * @param jarclassloader a classloader already initialized to load classes
     *              out of the package's jar file
     * 
     * @throws SyntaxErrorException if the XML document has errors in it
     */
    public void pluginRegistered(String namespaceuri, Node configuration, JarFile sourcefile, ClassLoader jarclassloader) throws SyntaxErrorException;

    /**
     * Called whenever a plugin is unregistered for a specific IPluginHandler.
     * As unregistration is done via deleting files, there is no way to prevent
     * this. A plugin may be unregistered even if it's registration did not
     * succeed.
     * 
     * @param namespaceuri the XML namespace of the current configuration node
     * @param configuration the XML configuration for the plugin
     */
    public void pluginUnRegistered(String namespaceuri, Node configuration);

    /**
     * The XML namespace which this plugin handler is responsible for.
     * 
     * @return A String indicating the namespace
     */
    public String getNamespaceURI();
  }
}
