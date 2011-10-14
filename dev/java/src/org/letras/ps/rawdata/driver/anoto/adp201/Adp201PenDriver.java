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
 * The Original Code is Letras (Java).
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
package org.letras.ps.rawdata.driver.anoto.adp201;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.rawdata.IPenAdapter;
import org.letras.ps.rawdata.IPenAdapterFactory;
import org.letras.ps.rawdata.IPenDriver;
import org.mundo.rt.TypedMap;
import org.mundo.service.IConfigure;

/**
 * The ADP-201 pen driver is the main class of the ADP-201 driver package.
 * It implements entry points to allow for loading the driver at runtime
 * through the driver plugin interface. This driver is able to connect to 
 * Anoto ADP-201 digital pens (and probably, as these are the same hardware, to
 * Maxell digital pens).
 * 
 * @see IPenDriver
 * @author felix 
 * @version 0.2.2
 */
public class Adp201PenDriver implements IPenDriver, IConfigure {

	// logger
	
	Logger logger = Logger.getLogger("org.letras.ps.rawdata.driver.anoto.adp201");
	
	// members
	
	/**
	 * The PenAdapterFactory for acquiring new IPenAdapter 
	 */
	private IPenAdapterFactory factory;
	
	/**
	 * The BluetoothConnector which handles bluetooth connections
	 */
	private BluetoothConnector bluetoothConnector;
	
	/**
	 * The Thread in which the BluetoothConnector is waiting for connections
	 */
	private Thread bluetoothThread;
	
	// constructor
	
	/**
	 * Nullary constructor. 
	 */
	public Adp201PenDriver() {
		bluetoothConnector = new BluetoothConnector(this);
		bluetoothThread  = new Thread(bluetoothConnector);
	}
	
	// methods
	
	/**
	 * Interface method from {@link IPenDriver}.
	 */
	@Override
	public void inject(IPenAdapterFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Interface method from {@link IPenDriver}.
	 */
	@Override
	public void shutdown() {
		logger.logp(Level.INFO, this.getClass().getSimpleName(), 
				"shutdown",  "Shutting down ADP-201 pen driver");
		
		bluetoothConnector.shutdown();
		try {
			bluetoothThread.join();
		} catch (InterruptedException e) {
			logger.logp(Level.WARNING, this.getClass().getSimpleName(), 
					"shutdown",  
					String.format("thread interrupted while waiting for handler to shutdown: %s", 
						e.getMessage()));
			e.printStackTrace();
		}
	}
	
	/**
	 * Interface method from {@link IPenDriver}.
	 */
	@Override
	public void init() {
		bluetoothThread.start();
		logger.logp(Level.INFO, this.getClass().getSimpleName(), 
				"init", "ADP-201 pen driver up and running");
	}
	
	/**
	 * Callback method for the BluetoothConnector to get the appropriate 
	 * IPenAdapter for a token.
	 * 
	 * @param token to forward to the adapter factory (e.g. the bluetooth address)
	 * @return {@link IPenAdapter} to be used by the handler
	 */
	IPenAdapter getPenAdapterForToken(String token) {	
		return factory.create(this, token);
	}

	@Override
	public Object getServiceConfig() {
		return null;
	}

	@Override
	public TypedMap getServiceConfigMap() {
		return null;
	}

	@Override
	public void setServiceConfig(Object arg0) {
		TypedMap map = (TypedMap) arg0;
		Adp201StreamConverter.xorigin = map.getInt("xorigin");
		Adp201StreamConverter.yorigin = map.getInt("yorigin");
		logger.logp(Level.CONFIG, this.getClass().getSimpleName(), "setServiceConfig", 
				String.format("Successfully configured ADP-201 driver to use %d as xorigin and %d as yorigin", 
				Adp201StreamConverter.xorigin, Adp201StreamConverter.yorigin));
		
	}

	@Override
	public void setServiceConfigMap(TypedMap arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
