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
package org.letras.ps.rawdata.driver.nokia;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.letras.ps.rawdata.IPenAdapter;

/**
 * The Bluetooth Connector handles incoming Bluetooth SPP connections. 
 * On accepting a new connection it spawns a BluetoothConnectionHandler
 * thread responsible for reading data from the Bluetooth connection.
 * This allows for multiple nokia pens connecting to the driver.
 * <p>
 * Supported pens: <ul><li>"Nokia SU-1B" Streaming since version 0.0.1</li></ul>
 *
 * @author niklas
 * @version 0.0.2
 */
public class BluetoothConnector extends Thread{

	//logger 
	
	private static final Logger logger = Logger.getLogger("org.letras.ps.rawdata.driver.nokia");
	
	//bluetooth constants
	
	/**
	 * Used to advertise the serial port to connecting nokia pens. 
	 * "ANOTO STREAMING" is the service name on which
	 * the Nokia SU-1B connects to the SPP-Service
	 */
	private static final String SERVICE_NAME = "ANOTO STREAMING";
	
	/**
	 * UUID 1101 is the Universally Unique IDentifier for the SPP (Serial Port Profile)
	 */
	private static final UUID uuid = new UUID("1101", true);
	
	//members
	
	/**
	 * The NokiaPenDriver is used to retrieve IPenAdapters for connecting pens
	 */
	private NokiaPenDriver nokiaPenDriver;
	
	/**
	 * representation of the SPP-Service
	 */
	private StreamConnectionNotifier connectionNotifier;
	
	/**
	 * list of all pens currently connected
	 */
	private LinkedList<BluetoothConnectionHandler> activeConnectionHandler;
	
	/**
	 * condition for entering the main event loop
	 */
	private boolean running = true;
	
	/**
	 * Standard constructor
	 * @param nokiaPenDriver
	 */
	public BluetoothConnector(NokiaPenDriver nokiaPenDriver) {
		this.nokiaPenDriver = nokiaPenDriver;
		
		activeConnectionHandler = new LinkedList<BluetoothConnectionHandler>();	
	}
	
	/**
	 * The method called when starting the thread
	 */
	public void run() {

		running = true;
		
		// define the url for the local server
		String serverUrl = String.format("btspp://localhost:%s;name=%s", uuid.toString(), SERVICE_NAME);

		try {

			// open this server URL
			connectionNotifier = (StreamConnectionNotifier) Connector.open(serverUrl);
			logger.logp(Level.CONFIG, "BluetoothConnector", "run", String.format("Started local SPP service with name: \"%s\"", SERVICE_NAME));

			while (running) {
				try {
					// wait for the connection to be established
					StreamConnection connection = connectionNotifier.acceptAndOpen();

					// get hold of the remote device on the other end of the connection
					RemoteDevice device = RemoteDevice.getRemoteDevice(connection);
					String bluetoothAdress = device.getBluetoothAddress();
					
					// ask the nokia pen driver for a PenAdapter
					IPenAdapter penAdapter = nokiaPenDriver.getPenAdapterForToken(bluetoothAdress);
					
					//create a ByteStreamConverter for the connected pen
					ByteStreamConverter converter = new SU1BStreamConverter(penAdapter);
					
					// initialize a BluetoothConnectionHandler and run it
					BluetoothConnectionHandler handler = new BluetoothConnectionHandler(connection, converter, this, bluetoothAdress);

					activeConnectionHandler.add(handler);

					handler.start();

					logger.logp(Level.FINE, "BluetoothConnector", "run", String.format("connection to %s established", device.getBluetoothAddress()));
					
				} catch (IOException e) {
					if (running) {
						logger.logp(Level.WARNING, "BluetoothConnector", "run",  String.format("failed to open connection to %s: ", e.getMessage()));
						e.printStackTrace();
					}
				}
			}
			
	
		} catch (ClassCastException e){
			logger.logp(Level.SEVERE, "BluetoothConnector", "run", String.format("could not cast to StreamConnectionNotifier: %s", e.getMessage()));
			e.printStackTrace();
		} catch (IOException e) {
			logger.logp(Level.SEVERE, "BluetoothConnector", "run", String.format("could not start local SPP service: %s", e.getMessage()));
			logger.logp(Level.WARNING, "BluetoothConnector", "run", "Could not initialize Bluetooth. Maybe there is no Bluetooth-Dongle connected?");
		}
	}

	/**
	 * Stops the Bluetooth adapter from listening to new connections. If this 
	 * adapter is currently running then the thread should also end as a result 
	 * of calling this function.
	 */
	private void stopSPPService() {
		
		running = false;
		
		if (connectionNotifier != null) {
			try {
				connectionNotifier.close();
			} catch (IOException e) {
				logger.logp(Level.SEVERE, "BluetoothConnector", "stopSPPService", String.format("could not stop local SPP service: %s", e.getMessage()));
				e.printStackTrace();
			}
		}
		
	}
	
	

	/**
	 * shutdown the BluetoothConnector thread and terminate all Bluetooth connections
	 */
	void shutdown() {
		
		logger.logp(Level.FINE, "BluetoothConnector", "shutdown", "Shutting down Bluetooth SPP-Service");
		
		stopSPPService();
		
		for (BluetoothConnectionHandler handler : activeConnectionHandler) {
			disconnectHandler(handler);
		}
		
		activeConnectionHandler.clear();
	}

	/**
	 * 
	 * @param handler
	 */
	void disconnectHandler(BluetoothConnectionHandler handler) {
		handler.shutdown();
		try {
			//make sure the handler really terminated before going on
			handler.join();
		} catch (InterruptedException e) {
			logger.logp(Level.WARNING, "BluetoothConnector", "shutdown",  String.format("thread interrupted while waiting for handler to shutdown: %s", e.getMessage()));
			e.printStackTrace();
		}
	}
}
