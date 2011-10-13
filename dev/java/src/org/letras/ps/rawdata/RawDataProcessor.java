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

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.rawdata.penmanager.PenManager;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.TypedMap;
import org.mundo.service.IConfigure;
import org.mundo.util.plugins.PluginManager;


/**
 * The RawDataProcessor is the applications main entry point. It fires up
 * all needed components such as the PenManager, the driver management
 * functionality and the Mundo node hosting the processing stage. It is realized
 * as a singleton, which may be obtained via its <code>getInstance()</code>
 * method. However, since this service is instantiated via the Mundo environment,
 * the constructor is left accessible. Do not use the constructor! Use the
 * aforementioned <code>getInstance()</code> method instead.
 * <P>
 * This class will not install any pen drivers, it will only setup the
 * environment used to deploy pen drivers on the fly. Additionally the pen
 * manager component to be used will be initialized.
 * After initialization, the application will be running in a wait state. This
 * means, that it runs, until it receives a stop request by someone. A stop
 * request might be issued from a user interface, such as the basic console
 * UI, or another caller.
 * <P>
 * Configuration of the <code>RawDataProcessor</code> is handled via the Mundo
 * <code>IConfigure</code> service interface. This means, configuration options
 * will be specified in the <code>node.conf.xml</code>. Specific components might
 * include their own configuration options, however, the general configuration
 * options will be processed here. Supported options are
 * <ul>
 *  <li> <code>driver-directory</code>: The directory which will be monitored
 *  for deployment of pen drivers
 *  <li> <code>log-level</code>: The log-level to use for log messages
 * </ul>
 * 
 * @author felix_h
 * @version 0.0.1
 * 
 */

public class RawDataProcessor extends Service implements IConfigure {

	// logger
	
	private static final Logger logger = Logger.getLogger("org.letras.ps.rawdata");
	
	// defaults
	
	/**
	 * The default directory monitored for pen drivers.
	 */
	public static final String DEFAULT_DRIVER_DIRECTORY = "./drivers/";
	
	/**
	 * Delay used to sleep each wait cycle.
	 */
	private static final int DELAY = 500;
	
	/**
	 * The log level to use. Might be configured via the used properties.
	 */
	private static Level logLevel = Level.INFO;
	
	private static Handler logHandler = new ConsoleHandler();
	
	// first thing to do: bootstrap our login stuff
	
	// using this mechanism to initiate log level setting is ugly, no doubt
	// but we need to assure that it is the first thing to be done (bootstrap problem)
	static {
		// set the log level and handler as pre-configured (NOTE: this will set the
		// the log level for the initialization code, after the properties have been
		// parsed, the log level will be reset to the one (if any) determined in the
		// node.conf.xml
		RawDataProcessor.setLogHandler(logHandler);
		RawDataProcessor.setLogLevel(logLevel);
	}
	
	// singleton

	private static RawDataProcessor instance;

	/**
	 * Method to retrieve the singleton instance of <code>RawDataProcessor</code>.
	 */
	public static RawDataProcessor getInstance() {
		try {
			return (instance == null) ? (instance = new RawDataProcessor())
					: instance;
		} catch (IllegalAccessException e) {
			// should never happen !!!
			logger.logp(Level.SEVERE, "RawDataProcessor", "getInstance",
					"message from the OTHER side...", e);
			return null;
		}
	}
	
	// members	
	
	private boolean run;
	
	private RdpConsole console;
	
	private File driverDirectory;
	
	private PenDriverManager driverManager;

	/**
	 * @return the driverDirectory
	 */
	public File getDriverDirectory() {
		return driverDirectory;
	}

	/**
	 * @param driverDirectory the driverDirectory to set
	 */
	public void setDriverDirectory(File driverDirectory) {
		this.driverDirectory = driverDirectory;
	}

	public PenDriverManager getDriverManager() {
		return driverManager;
	}

	public void setDriverManager(PenDriverManager driverManager) {
		this.driverManager = driverManager;
	}

	// constructors
	
	/**
	 * No-argument constructor. The only reason for it to be publicly accessible is the
	 * instantiation policy of Mundo. Normally this constructor will never be used, use the
	 * <code>getInstance()</code> method instead. If the constructor is used after the singleton has
	 * been instantiated, it will throw an <code>IllegalAccessException</code>.
	 * 
	 * @throws IllegalAccessException If this class has already been instantiated (instance != null),
	 * this should never happen, since the contract is to use the <code>getInstance()</code> method
	 */
	public RawDataProcessor() throws IllegalAccessException {
		
		if (instance == null) {
			this.run = false;

			// make a console if available (WARNING: console might be not available,
			// check availability with console.isAvailable())
			this.console = new RdpConsole();
			
			// build a pen driver manager
			this.driverManager = new PenDriverManager();
			
			// set the default directory
			try {
				this.driverDirectory = new File(DEFAULT_DRIVER_DIRECTORY).getCanonicalFile();
			}
			catch (IOException e) {
				logger.logp(Level.WARNING, "RawDataProcessor", "RawDataProcessor",
						"could not access the default drivers directory", e);
			}

			// set the singleton instance (NOTE ugly but needed by Mundo)
			// this will set the instance the first time a raw data processor is created
			instance = this;
		}
		else throw new IllegalAccessException("Tried to access Singleton constructor");
	}
	
	// methods
	
	/**
	 * Overwritten method from {@link org.mundo.rt.Service}.
	 * Used to initialize the needed components; this method will be
	 * called automatically when the <code>RawDataProcessor</code> service is 
	 * registered at the Mundo node. 
	 */
	@Override
	public void init() {

		super.init();
		
		logger.logp(Level.FINE, "RawDataProcessor", "init", "initializing processing stage");
		
		// initialize the pen driver manager, as the driver dir has already been set
		// it can be used (setServiceConfig() was called already by contract)
		
		try {
			PluginManager.addPluginHandler(this.driverManager, this.driverDirectory);
		} 
		catch (IOException e) {
			logger.logp(Level.WARNING, "RawDataProcessor", "init", 
					String.format("could not access driver directory : %s", this.driverDirectory), e);
		}
		
		this.driverManager.init();
		
		// initialize the pen manager: register the the singleton as a service at the mundo node
		Mundo.registerService(PenManager.getInstance());
	}
	
	/**
	 * Overwritten method from {@link org.mundo.rt.Service}. Shuts down
	 * the <code>RawDataProcessor</code> service, called automatically upon
	 * shutting down the Mundo node.
	 */
	@Override
	public void shutdown() {
		logger.logp(Level.FINE, "RawDataProcessor", "shutdown", "shutting down processing stage");
		
		// shutdown the driver manager and all the currently loaded drivers
		this.driverManager.shutdown();
		
		super.shutdown();
	}
	
	/**
	 * Overwritten method from {@link org.mundo.rt.Service}.
	 */
	@Override
	public Object getServiceConfig() {
		return null;
	}

	/**
	 * Overwritten method from {@link org.mundo.rt.Service}. Used to obtain
	 * a configuration object (should be a <code>TypedMap</code>), which contains
	 * the properties set in the <code>node.conf.xml</code>. This method will be called
	 * prior to calling the <code>init()</code> method.
	 */
	@Override
	public void setServiceConfig(Object cfg) {
		
		// if the service is already running, it does not support configuration anymore
		if (this.run) {
			logger.logp(Level.INFO, "RawDataProcessor", "setServiceConfig",
					"no dynamic reconfiguration supported");
			return;
		}
		
		// check whether the passed object is a TypedMap
		if ((cfg == null)||!(cfg instanceof TypedMap)) {
			logger.logp(Level.WARNING, "RawDataProcessor", "setServiceConfig",
					"configuration parameter must be a TypedMap");
			return;
		}
		
		// obtain the typed map holding the configuration options
		TypedMap options = (TypedMap) cfg;
		
		// first obtain the desired log level
		String ll = (options.containsKey("log-level")) ? options.getString("log-level")
				: null;
		
		if (ll != null) { 
			try {
				RawDataProcessor.setLogLevel(Level.parse(ll));
			}
			catch (IllegalArgumentException e) {
				logger.logp(Level.WARNING, "RawDataProcessor", "setServiceConfig",
						String.format("unknown log level specified: %s", ll), e);
			}
		}
		
		// now obtain the needed properties and check whether their values are present/correct
		String dd = (options.containsKey("driver-directory")) ? options.getString("driver-directory")
				: null;
		
		if (dd != null) {
			try {
				this.driverDirectory = new File(dd).getCanonicalFile();
			} catch (IOException e) {
				logger.logp(Level.WARNING, "RawDataProcessor",
						"setServiceConfig", "cannot configure specified driver directory", e);
			}
		}
		
		logger.logp(Level.CONFIG, "RawDataProcessor", "setServiceConfig",
				String.format("configured parameters (driver-directory=%s/log-level=%s)",this.driverDirectory, ll));
		
		// check whether the currently configured driver directory is present and is a directory
		if ((this.driverDirectory== null)||(!this.driverDirectory.isDirectory()))
			logger.logp(Level.WARNING, "RawDataProcessor", "setServiceConfig",
					"driver directory not available");
		
		// configure the pen manager also (manually)
		PenManager.getInstance().setServiceConfig(options);
	}

	/**
	 * Used to stop the application. Remotely called, usually through the UI
	 * or a hosting application.
	 */
	public void stop() {
		synchronized (this) {
			this.run = false;
		}
	}

	/**
	 * Starts the application.
	 */
	public void start() {
	
		// start to run the rdp
		synchronized (this) {
			this.run = true;
		}
		
		// initialize the console
		if (this.console.isAvailable()) {
			logger.logp(Level.CONFIG, "RawDataProcessor", "start", "console available: starting interactive mode");
			Thread t = new Thread(this.console);
			t.start();
		}
		else
			logger.logp(Level.CONFIG, "RawDataProcessor", "start", "console not available: starting demon mode");
		
		while (run) {
			
			try {
				Thread.sleep(DELAY);
				Thread.yield();
			} 
			catch (InterruptedException e) {
				logger.logp(Level.FINE, "RawDataProcessor", "start",
						"interrupted while sleeping", e);
			}
		}
	}

	/**
	 * Sets the used log level and handler for the raw data processing stage
	 * (<code>org.letras.ps.rawdata</code> and sub packages).
	 * 
	 * @param logLevel the level to use
	 */
	public static void setLogLevel(Level logLevel) {
		// set up the static logging configuration for all loggers in the rdps
		// top-level package in the current virtual machine
		
		Logger rdpsLogger = Logger.getLogger("org.letras.ps.rawdata");
		rdpsLogger.setLevel(logLevel);
		rdpsLogger.setUseParentHandlers(false);
		
		logHandler.setLevel(logLevel);
	}
	
	public static void setLogHandler(Handler logHandler) {
		RawDataProcessor.logHandler = logHandler;
		Logger.getLogger("org.letras.ps.rawdata").addHandler(logHandler);
	}
	
	/**
	 * The main entry point of the raw data processing stage implementation.
	 * It starts up a Mundo node, initializes needed components and waits until
	 * a receives a shutdown request.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// initialize the mundo node
		Mundo.init();
		
		// start the raw data processor
		RawDataProcessor rdp = RawDataProcessor.getInstance();
		// not needed anymore: Mundo.registerService(rdp); --> handled via node.conf.xml
		
		logger.logp(Level.INFO, "RawDataProcessor", "main", "starting");
		
		rdp.start();
		
		logger.logp(Level.INFO, "RawDataProcessor", "main", "shutting down");
		
		// shutdown the mundo node
		Mundo.shutdown();
	}
}
