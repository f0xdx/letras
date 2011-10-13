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
package org.letras.ps.region;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.region.broker.IRegionBroker;
import org.letras.ps.region.broker.simple.SimpleRegionBroker;
import org.letras.ps.region.penconnector.IPenAccessConfiguration;
import org.letras.ps.region.penconnector.PenAccessManager;
import org.letras.ps.region.sampleprocessor.ISampleProcessorFactory;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.TypedMap;
import org.mundo.service.IConfigure;


/**
 * The <code>RegionProcessor</code> is the applications main entry point and is
 * responsible for instantiating all relevant components that make up an instance
 * of the RegionProcessingStage.
 * <p>
 * A RegionProcessor is composed of the following components:
 * <ul>
 * <li>{@link RegionManager}</li>
 * <li>{@link PenAccessManager}</li>
 * <li>{@link IRegionBroker} (e.g. {@link SimpleRegionBroker}</li>
 * <li>{@link RegionSampleProcessor} (one for each connected pen)</li>
 * </ul>
 * <p>
 * Configuration of the <code>RegionProcessor</code> is handled via the Mundo
 * <code>IConfigure</code> service interface. This means, configuration options
 * will be specified in the <code>node.conf.xml</code>. Specific components might
 * include their own configuration options, however, the general configuration
 * options will be processed here. Supported options are
 * <ul>
 *  <li> <code>log-level</code>: The log-level to use for log messages
 *  <li> <code>pap-zone</code>: The Mundo zone in which pens will be discovered
 *  <li> <code>rap-zone</code>: The Mundo zone to which <code>RegionEvents</code> will be published
 *  <li> <code>broker</code>: 
 *  <li> <code>access</code>: Rules for allowing and rejecting discovered pens (see {@link IPenAccessConfiguration})
 * </ul>
 * 
 */

public class RegionProcessor extends Service implements IConfigure {

	// logger
	
	private static final Logger logger = Logger.getLogger("org.letras.ps.region");
	
	// defaults
	
	/**
	 * Delay used to sleep each wait cycle.
	 */
	private static final int DELAY = 500;
	
	/**
	 * The log level to use. Might be configured via the used properties.
	 */
	private static Level logLevel = Level.FINE;
	
	private static Handler logHandler = new ConsoleHandler();
	
	// first thing to do: bootstrap our login stuff
	
	// using this mechanism to initiate log level setting is ugly, no doubt
	// but we need to assure that it is the first thing to be done (bootstrap problem)
	static {
		// set the log level and handler as pre-configured (NOTE: this will set the
		// the log level for the initialization code, after the properties have been
		// parsed, the log level will be reset to the one (if any) determined in the
		// node.conf.xml
		RegionProcessor.setLogHandler(logHandler);
		RegionProcessor.setLogLevel(logLevel);
	}
	
	// singleton

	private static RegionProcessor instance;

	/**
	 * Method to retrieve the singleton instance of <code>RegionProcessor</code>.
	 */
	public static RegionProcessor getInstance() {
		try {
			return (instance == null) ? (instance = new RegionProcessor())
					: instance;
		} catch (IllegalAccessException e) {
			// should never happen !!!
			logger.logp(Level.SEVERE, "RegionProcessor", "getInstance",
					"message from the OTHER side...", e);
			return null;
		}
	}
	
	// members	
	
	private boolean run;
	
	private RpConsole console;
	
	private PenAccessManager penDiscoveryService;
	
	private ISampleProcessorFactory regionSampleProcessorFactory;
	
	private RegionManager regionManager;
	
	private IRegionBroker regionBroker;


	// constructors
	
	/**
	 * Set the RegionBroker for the RegionProcessor
	 * @param regionBroker the regionBroker to set
	 */
	public void setRegionBroker(IRegionBroker regionBroker) {
		this.regionBroker = regionBroker;
	}

	/**
	 * No-argument constructor. The only reason for it to be publicly accessible is the
	 * instantiation policy of Mundo. Normally this constructor will never be used, use the
	 * <code>getInstance()</code> method instead. If the constructor is used after the singleton has
	 * been instantiated, it will throw an <code>IllegalAccessException</code>.
	 * 
	 * @throws IllegalAccessException If this class has already been instantiated (instance != null),
	 * this should never happen, since the contract is to use the <code>getInstance()</code> method
	 */
	public RegionProcessor() throws IllegalAccessException {
		
		if (instance == null) {
			this.run = false;

			// make a console if available (WARNING: console might be not available,
			// check availability with console.isAvailable())
			this.console = new RpConsole();
			
			
			this.penDiscoveryService = new PenAccessManager();
			
			
			
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
	 * called automatically when the <code>RegionProcessor</code> service is 
	 * registered at the Mundo node. 
	 */
	@Override
	public void init() {

		super.init();
		
		logger.logp(Level.FINE, "RegionProcessor", "init", "initializing processing stage");
		
		if (regionManager == null) {
			logger.logp(Level.WARNING, "RegionProcessor", "init", "using default RegionManager");
			this.regionManager = new RegionManager();
		}

		regionManager.setServiceZone(this.getServiceZone());

		if (regionBroker != null) {
			regionManager.setBroker(this.regionBroker);
			Mundo.registerService((Service) this.regionBroker);			
		} else {
			logger.logp(Level.SEVERE, "RegionProcessor", "init", "no region broker set");			
		}
		
		this.regionSampleProcessorFactory = new RegionSampleProcessorFactory(this.regionManager);
		
		this.penDiscoveryService.setSampleProcessorFactory(this.regionSampleProcessorFactory);
		
		this.penDiscoveryService.init();
		
	}
	
	/**
	 * Overwritten method from {@link org.mundo.rt.Service}. Shuts down
	 * the <code>RegionProcessor</code> service, called automatically upon
	 * shutting down the Mundo node.
	 */
	@Override
	public void shutdown() {
		logger.logp(Level.FINE, "RegionProcessor", "shutdown", "shutting down processing stage");
		
		this.penDiscoveryService.shutdown();
		
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
			logger.logp(Level.INFO, "RegionProcessor", "setServiceConfig",
					"no dynamic reconfiguration supported");
			return;	
		}
		
		// check whether the passed object is a TypedMap
		if ((cfg == null)||!(cfg instanceof TypedMap)) {
			logger.logp(Level.WARNING, "RegionProcessor", "setServiceConfig",
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
				RegionProcessor.setLogLevel(Level.parse(ll));
			}
			catch (IllegalArgumentException e) {
				logger.logp(Level.WARNING, "RegionProcessor", "setServiceConfig",
						String.format("unknown log level specified: %s", ll), e);
			}
		}
		
		//obtain the specified region discovery zone
		String rap = (options.containsKey("rap-zone")?options.getString("rap-zone"):null);
		
		if (rap != null) {
			this.setServiceZone(rap);
		}
		
		TypedMap broker = (options.containsKey("broker")?options.getMap("broker"):null);
	
		if (broker != null) {
			String brokerClassName = (broker.containsKey("classname")?broker.getString("classname"):null);
			if (brokerClassName != null) {
				try {
					Class<?> brokerClass = Class.forName(brokerClassName);
					try {
						this.regionBroker = (IRegionBroker) brokerClass.getConstructor().newInstance();
						
					} catch (SecurityException e) {
						logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", e.getMessage());
					} catch (NoSuchMethodException e) {
						logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", "The selected RegionBroker does not provide a required nullary constructor");
					} catch (IllegalArgumentException e) {
						logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", e.getMessage());
					} catch (InstantiationException e) {
						logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", e.getMessage());
					} catch (IllegalAccessException e) {
						logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", e.getMessage());
					} catch (InvocationTargetException e) {
						logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", e.getMessage());
					}
				} catch (ClassNotFoundException e) {
					logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", String.format("Specified broker %s could not be found", brokerClassName));
				}
				if (this.regionBroker != null) {
					TypedMap brokerConfiguration = (broker.containsKey("config")?broker.getMap("config"):null);
					if (brokerConfiguration != null) {
						this.regionBroker.setServiceConfig(brokerConfiguration);
					}
				}
			} else {
				logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", "You forgot to specify a broker in node.conf.xml");
			}
		}
		
		TypedMap manager = (options.containsKey("manager")?options.getMap("manager"):null);
		
		if (manager != null) {
			String managerClassName = (manager.containsKey("classname")?manager.getString("classname"):null);
			if (managerClassName != null) {
				try {
					Class<?> managerClass = Class.forName(managerClassName);
					try {
						this.regionManager = (RegionManager) managerClass.getConstructor().newInstance();
						
					} catch (Exception e) {
						logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", e.getMessage());
					}
				} catch (ClassNotFoundException e) {
					logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", String.format("Specified manager %s could not be found", managerClassName));
				}
			} else {
				logger.logp(Level.SEVERE, "RegionProcessor", "setServiceConfig", "You forgot to specify a broker in node.conf.xml");
			}
		} else {
			this.regionManager = new RegionManager();
		}
		
		TypedMap penAccessConfig = (options.containsKey("access")?options.getMap("access"):null);
		
		if (penAccessConfig != null) {
			this.penDiscoveryService.setServiceConfig(penAccessConfig);
		}
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
			logger.logp(Level.CONFIG, "RegionProcessor", "start", "console available: starting interactive mode");
			Thread t = new Thread(this.console);
			t.start();
		}
		else
			logger.logp(Level.CONFIG, "RegionProcessor", "start", "console not available: starting demon mode");
		
		while (run) {
			
			try {
				Thread.sleep(DELAY);
				Thread.yield();
			} 
			catch (InterruptedException e) {
				logger.logp(Level.FINE, "RegionProcessor", "start",
						"interrupted while sleeping", e);
			}
		}
	}

	/**
	 * Sets the used log level and handler for the region processing stage
	 * (<code>org.letras.ps.region</code> and sub packages).
	 * 
	 * @param logLevel the level to use
	 */
	public static void setLogLevel(Level logLevel) {
		// set up the static logging configuration for all loggers in the rdps
		// top-level package in the current virtual machine
		
		Logger rdpsLogger = Logger.getLogger("org.letras.ps.region");
		rdpsLogger.setLevel(logLevel);
		rdpsLogger.setUseParentHandlers(false);
		
		logHandler.setLevel(logLevel);
	}
	
	public static void setLogHandler(Handler logHandler) {
		RegionProcessor.logHandler = logHandler;
		Logger.getLogger("org.letras.ps.region").addHandler(logHandler);
	}
	
	/**
	 * The main entry point of the region processing stage implementation.
	 * It starts up a Mundo node, initializes needed components and waits until
	 * a receives a shutdown request.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// initialize the mundo node
		Mundo.init();
		
		// start the raw data processor
		RegionProcessor rp = RegionProcessor.getInstance();
		// not needed anymore: Mundo.registerService(rp); --> handled via node.conf.xml
		
		logger.logp(Level.INFO, "RegionProcessor", "main", "starting");
		
		rp.start();
		
		logger.logp(Level.INFO, "RegionProcessor", "main", "shutting down");
		
		// shutdown the mundo node
		Mundo.shutdown();
	}

	/**
	 * get the region manager
	 * @return current region manager
	 */
	public RegionManager getRegionManager() {
		return regionManager;
	}
}
