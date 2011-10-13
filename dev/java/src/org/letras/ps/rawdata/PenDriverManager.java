/*******************************************************************************
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
 * Department of Computer Science, Technische Universität Darmstadt.
 * Portions created by the Initial Developer are
 * Copyright © 2009-2011 the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 * Felix Heinrichs
 * Niklas Lochschmidt
 * Jannik Jochem
 ******************************************************************************/
package org.letras.ps.rawdata;

import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.rawdata.penmanager.PenManager;
import org.mundo.rt.TypedMap;
import org.mundo.service.IConfigure;
import org.mundo.util.SyntaxErrorException;
import org.mundo.util.plugins.IPluginMonitor;
import org.mundo.xml.XMLDeserializer;

import org.w3c.dom.Node;

/**
 * The pen driver manager is used to monitor and control the currently installed
 * drivers. By using this class, a driver can be installed by simply putting into
 * the appropriate driver directory. Internally this class uses the Mundo plugin
 * mechanism by defining a custom plugin handler.
 * <P>
 * Pen drivers can be loaded and installed only once. Trying to load the same pen
 * driver a second time simply will be ignored unless the first driver has been
 * unloaded. 
 * 
 * @author felix_h
 * @version 0.0.1
 *
 */
public class PenDriverManager implements IPluginMonitor.IPluginHandler {

	// logger

	private static final Logger logger = 
		Logger.getLogger("org.letras.ps.rawdata");
	
	// defaults
	
	private static final String PLUGIN_NAMESPACE = "http://letras.org/2009/driver/pendriver";
	
	// used names for the relevant nodes in the plugin.xml for pen drivers
	private static final String MAIN_CLASS_NODE_NAME = "main-class";
	private static final String CONFIG_NODE_NAME = "config";
	
	// members

	private HashMap<String, IPenDriver> drivers;
	
	public HashMap<String, IPenDriver> getDrivers() {
		return drivers;
	}

	public void setDrivers(HashMap<String, IPenDriver> drivers) {
		this.drivers = drivers;
	}
	
	// constructors

	/**
	 * No-argument constructor.
	 */
	public PenDriverManager() {
		
		this.drivers = new HashMap<String, IPenDriver>();
		
	}
	
	// methods

	/**
	 * Helper method to find the child node with <i>name</i> of a given node.
	 * 
	 * @param node the node whose children should be searched
	 * @param name the name of the child to search
	 * @return the child node with name <code>name</code>, <code>null</code> if such
	 * a node does not exist
	 */
	private Node findChild(Node node, String name) {
		Node current = node.getFirstChild();
		
		while (current != null) {
			if (current.getNodeName().equals(name)) return current;
			current = current.getNextSibling();
		}
		
		return null;
	}
	
	/**
	 * Helper method to outsource the configuration of <code>IPenDrivers</code>.
	 * 
	 * @param driver a pen driver implementing the IConfigure interface
	 * @param config a node representing a serialized {@link org.mundo.rt.TypedMap}, as
	 * obtained from the plugin.xml file
	 */
	private void configureDriver(IPenDriver driver, Node configNode) {
		
		// deserialize the typed map out of the xml node
		try {
			TypedMap config = (new XMLDeserializer()).deserializeMap(configNode);
			
			// now configure the driver
			((IConfigure) driver).setServiceConfig(config);
		} 
		catch (Exception e) {
			logger.logp(Level.WARNING, "PenDriverManager", "configureDriver",
					"could not deserialize config map, skipping configuration", e);
		}
	}
	
	/**
	 * Used to load a new driver into the active drivers. This will call the
	 * driver's <code>init()</code> method and add the driver to the list of
	 * currently active drivers in the system. Normally this method will be called
	 * automatically by the <code>PenDriverManager</code> whenever a new driver
	 * was discovered and pre-configured. 
	 * <P>
	 * It is however possible to load drivers explicitly via a call to this method.
	 * Test scenarios for example will rely on this feature. 
	 * 
	 * @param driver the driver to load
	 */
	public void loadDriver(IPenDriver driver) {
		
		if (driver == null) {
			logger.logp(Level.WARNING, "PenDriverManager", "loadDriver",
					"trying to add a null driver");
			return;
		}
		
		logger.logp(Level.FINE, "PenDriverManager", "loadDriver", 
				String.format("loading driver: %s", driver.getClass().getName()));
		
		// store the driver in the list of available drivers 
		this.drivers.put(driver.getClass().getName(), driver);
		
		// select the appropriate pen adapter factory for this driver from the pen manager in use
		driver.inject(PenManager.getInstance().selectPenAdapterFactory(driver));
		
		// initialize the driver
		driver.init();
	}
	
	/**
	 * Used to unload a driver from the active drivers. This will call the
	 * driver's <code>shutdown()</code> method and remove the driver from the list 
	 * of currently active drivers in the system. Normally this method will be called
	 * automatically by the <code>PenDriverManager</code> whenever a driver
	 * was removed. 
	 * <P>
	 * It is however possible to unload drivers explicitly via a call to this method.
	 * Test scenarios for example will rely on this feature. 
	 * 
	 * @param driver the driver to unload
	 */
	public void unloadDriver(IPenDriver driver) {
		
		if (driver == null) {
			logger.logp(Level.WARNING, "PenDriverManager", "unloadDriver",
					"trying to remove a null driver");
			return;
		}
		
		// remove the driver from the list of available drivers 
		if (this.isLoaded(driver)) {
			logger.logp(Level.FINE, "PenDriverManager", "unloadDriver", 
					String.format("unloading driver: %s", driver.getClass().getName()));
			this.drivers.remove(driver.getClass().getName());
			driver.shutdown();
		}
		else {
			logger.logp(Level.FINE, "PenDriverManager", "unloadDriver", 
					String.format("could not unload driver: %s : driver not loaded", driver.getClass().getName()));
		}
	}
	
	/**
	 * Checks whether a driver is already loaded. Returns true only if this specific driver
	 * instance is loaded.
	 * 
	 * @param driver the driver to check
	 * @return true if the driver is loaded, falls otherwise
	 */
	public boolean isLoaded(IPenDriver driver) {
		return ((driver != null) && driver.equals(this.drivers.get(driver.getClass().getName()))) ?
				true : false;
	}
	
	/**
	 * Interface method from {@link org.mundo.util.plugins.IPluginMonitor.IPluginHandler}. 
	 */
	@Override
	public String getNamespaceURI() {
		return PLUGIN_NAMESPACE;
	}

	/**
	 * Interface method from {@link org.mundo.util.plugins.IPluginMonitor.IPluginHandler}
	 */
	@Override
	public void pluginRegistered(String namespaceuri, Node node, JarFile jarfile,
			ClassLoader jarclassloader) throws SyntaxErrorException {
		
		Node mainClassNode, configNode;
		
		// check whether the name space is handled by this handler
		if (!PLUGIN_NAMESPACE.equals(namespaceuri)) {
			logger.logp(Level.FINE, "PenDriverManager", "pluginRegistered",
					String.format("name space: %s : not handled",namespaceuri));
			return;
		}
		
		// check whether the node has a child node named main-class as demanded by
		// the contract, and whether this main-class node specifies some class name
		if ((!node.hasChildNodes()) || 
			((mainClassNode = this.findChild(node, MAIN_CLASS_NODE_NAME)) == null) ||
			(mainClassNode.getTextContent() == null)) {
			logger.logp(Level.WARNING, "PenDriverManager", "pluginRegistered",
					String.format("malformed plugin.xml : no main class specified (%s)", jarfile.getName()));
			return;
		}
		
		// now instantiate this main class 
		try {
			logger.logp(Level.FINE, "PenDriverManager", "pluginRegistered",
					String.format("loading driver : main class = %s\n", mainClassNode.getTextContent()));
			
			Class<?> mainClass = Class.forName(mainClassNode.getTextContent(), true, jarclassloader);
			IPenDriver driver = (IPenDriver) mainClass.newInstance();
			
			// check whether this pen driver requires configuration and we have a config node available
			// this node is optional, so it should be ok if this node cannot be found
			if ((driver instanceof IConfigure) &&
				((configNode = this.findChild(node, CONFIG_NODE_NAME)) != null)) {
				this.configureDriver(driver, configNode);
			}
			
			// finally load the driver
			this.loadDriver(driver);
			
			logger.logp(Level.CONFIG, "PenDriverManager", "pluginRegistered",
					String.format("driver loaded : %s", driver.getClass().getName()));
		}
		catch (ClassNotFoundException e) {
			logger.logp(Level.WARNING, "PenDriverManager", "pluginRegistered",
					String.format("could not load driver (%s): main class not found: (%s)", 
									jarfile.getName(), mainClassNode.getTextContent()), e);
			return;
		} 
		catch (InstantiationException e) {
			logger.logp(Level.WARNING, "PenDriverManager", "pluginRegistered",
					String.format("could not load driver (%s): main class cannot be instantiated: (%s)", 
							jarfile.getName(), mainClassNode.getTextContent()), e);
			return;
		} 
		catch (IllegalAccessException e) {
			logger.logp(Level.WARNING, "PenDriverManager", "pluginRegistered",
					String.format("could not load driver (%s): main class constructor cannot be accessed: (%s)", 
							jarfile.getName(), mainClassNode.getTextContent()), e);
			return;
		}
		catch (ClassCastException e) {
			logger.logp(Level.WARNING, "PenDriverManager", "pluginRegistered",
					String.format("could not load driver (%s): main class is no IPenDriver: (%s)", 
							jarfile.getName(), mainClassNode.getTextContent()), e);
			return;
		}
	}

	/**
	 * Interface method from {@link org.mundo.util.plugins.IPluginMonitor.IPluginHandler}
	 */
	@Override
	public void pluginUnRegistered(String namespaceuri, Node node) {

		Node mainClassNode;
		
		// check whether the name space is handled by this handler
		if (!PLUGIN_NAMESPACE.equals(namespaceuri)) {
			logger.logp(Level.FINE, "PenDriverManager", "pluginUnRegistered",
					String.format("name space: %s : not handled",namespaceuri));
			return;
		}
		
		// check whether the node has a child node named main-class as demanded by
		// the contract, and whether this main-class node specifies some class name
		if ((!node.hasChildNodes()) || 
			((mainClassNode = this.findChild(node, MAIN_CLASS_NODE_NAME)) == null) ||
			(mainClassNode.getTextContent() == null)) {
			logger.logp(Level.WARNING, "PenDriverManager", "pluginUnRegistered",
					String.format("malformed plugin.xml : no main class specified"));
			return;
		}
		
		logger.logp(Level.FINE, "PenDriverManager", "pluginUnRegistered",
				String.format("unloading driver : main class = %s\n", mainClassNode.getTextContent()));
		
		// obtain the driver by its main class name
		
		IPenDriver driver = this.getDrivers().get(mainClassNode.getTextContent());
		
		// and unload it if it is available
		if (driver != null) {
			this.unloadDriver(driver);
			logger.logp(Level.CONFIG, "PenDriverManager", "pluginUnRegistered", 
					String.format("driver unloaded : %s", driver.getClass().getName()));
		}
		else {
			logger.logp(Level.WARNING, "PenDriverManager", "pluginUnRegistered", 
					String.format("could not unload driver : no such driver loaded (%s)",
							mainClassNode.getTextContent()));
		}
	}
	
	/**
	 * Used to initialize the pen driver manager. Called after creation, to allow
	 * the <code>PenDriverManager</code> to get ready for usage.
	 */
	public void init() {
		// check whether there are some drivers which must be pre-loaded
	}
	
	/**
	 * Used to shutdown the <code>PenDriverManager</code>. Shuts down all currently
	 * registered drivers. 
	 */
	public void shutdown() {
		// unload each loaded driver
		IPenDriver[] driverArray = new IPenDriver[drivers.size()];
		drivers.values().toArray(driverArray);
		for (IPenDriver driver : driverArray) this.unloadDriver(driver);
	}
}
