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
package org.letras.ps.rawdata.driver.logitech;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.rawdata.IPenAdapter;
import org.letras.ps.rawdata.IPenAdapterFactory;
import org.letras.ps.rawdata.IPenDriver;
import org.mundo.rt.TypedMap;
import org.mundo.service.IConfigure;

/**
 * The Logitech Pen Driver is the top-level class of the logitechs driver package.
 * It implements entry points to allow for loading the driver at runtime
 * through the Mundo Generic Plugin interface.
 * @see IPenDriver
 * @author niklas
 * @version 0.0.2
 */
public class LogitechPenDriver implements IPenDriver, IConfigure {

	// logger
	
	Logger logger = Logger.getLogger("org.letras.ps.rawdata.driver.logitech");
	
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
	 * Nullary constructor 
	 */
	public LogitechPenDriver() {
		bluetoothConnector = new BluetoothConnector(this);
		bluetoothThread  = new Thread(bluetoothConnector);
	}
	
	// methods
	
	@Override
	public void setPenAdapterFactory(IPenAdapterFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public void shutdown() {
		logger.logp(Level.INFO, "LogitechPenDriver", "shutdown",  "Shutting down logitech Pen Driver");
		
		bluetoothConnector.shutdown();
		try {
			bluetoothThread.join();
		} catch (InterruptedException e) {
			logger.logp(Level.WARNING, "LogitechPenDriver", "shutdown",  String.format("thread interrupted while waiting for handler to shutdown: %s", e.getMessage()));
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() {
		bluetoothThread.start();
		logger.logp(Level.INFO, "LogitechPenDriver", "init", "Logitech Pen Driver up and running");
	}
	
	/**
	 * Callback method for the BluetoothConnector to get the appropriate IPenAdapter for the token
	 * @param token to forward to the adapter factory (e.g. the bluetooth address)
	 * @return IPenAdapter to be used by the handler
	 */
	IPenAdapter getPenAdapterForToken(String token) {	
		return factory.create(this, token);
	}

	@Override
	public Object getServiceConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedMap getServiceConfigMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServiceConfig(Object arg0) {
		TypedMap map = (TypedMap) arg0;
		IO2StreamConverter.xorigin = map.getInt("xorigin");
		IO2StreamConverter.yorigin = map.getInt("yorigin");
		logger.logp(Level.CONFIG, "LogitechPenDriver", "setServiceConfig", String.format("Successfully configured Logitech driver to use %d as xorigin and %d as yorigin", IO2StreamConverter.xorigin, IO2StreamConverter.yorigin));
		
	}

	@Override
	public void setServiceConfigMap(TypedMap arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
