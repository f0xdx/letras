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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.microedition.io.StreamConnection;

/**
 * The BluetoothConnectionHandler initializes the inputStream to the pen and
 * reads bytes from the bluetooth connection to forward them to the stream converter.
 * <p>
 * If a connection gets interrupted or terminated the connection handler will notice
 * and notifies the stream converter.  
 * <p>
 * NOTE: this class is currently in a test state - data will be dumped to a file
 * named "adp201-xxxx.data", where xxxx corresponds to the BT address of the used
 * pen
 * 
 * @author niklas, felix
 * @version 0.2.2
 */
class BluetoothConnectionHandler extends Thread {
	
	//logger
	
	private static final Logger logger = 
			Logger.getLogger(BluetoothConnectionHandler.class.getPackage().getName());

	// TODO: remove this after test phase
	private static final String dumpFile = "adp201-%s.data";
	
	//members
	
	private StreamConnection connection;

	/**
	 * Route the received bytes and pen on/off state to this converter
	 */
	private ByteStreamConverter converter;
	
	/**
	 * reference to the bluetooth connector in case the handler needs to be shut down
	 */
	private BluetoothConnector connector;
	
	/**
	 * The String which has been given to the IPenAdapterFactory on creating a 
	 * new instance of IPenAdapter.
	 */
	private String token;
	
	private boolean streaming;
	
	private boolean running;

	private InputStream stream;

	// TODO: remove this after test phase
	private java.io.BufferedOutputStream bos;
	
	//constructor
	
	/**
	 * Standard constructor taking the relevant configuration.
	 * 
	 * @param connection
	 * @param converter
	 * @param connector
	 * @param token
	 */
	public BluetoothConnectionHandler(StreamConnection connection, 
			ByteStreamConverter converter, 
			BluetoothConnector connector, 
			String token) {
		this.connection = connection;
		this.converter = converter;
		this.connector = connector;
		this.token = token;
		this.streaming = true;
		// TODO: remove this after test phase
		try { this.bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream(String.format(dumpFile, token))); } 
		catch (FileNotFoundException ex) { logger.log(Level.WARNING, "could not open dump file", ex); }
	}
	
	@Override
	public void run() {
		running = true;
		try {
			stream = this.connection.openInputStream();
			
			int data;
			
			converter.penConnected();
			// now read until the end of the stream has been reached (i.e. the 
			// next value returned is -1)
			while (running) {
				data = stream.read();
				if (data == -1) {
					if (streaming) {
						streaming = false;
						shutdown();
						logger.logp(Level.FINE, this.getClass().getSimpleName(), 
								"run", String.format("pen has been disconnected: %s", token) );
					}
				} else {
					streaming = true;
					converter.handleByte(data);
					// TODO: remove after test phase
					if (bos!=null) bos.write(data);
				}
			}
		} catch (IOException e) {
			logger.logp(Level.WARNING, this.getClass().getSimpleName(), 
					"run", String.format("could not read from stream, trying to shutdown: %s", e.getMessage()));
			e.printStackTrace();
			connector.disconnectHandler(this);
		}
	}
	
	/**
	 * Used to shutdown the handler.
	 * <p>	
	 * This method should only be called by the BluetoothConnector
	 */
	void shutdown() {
		running = false;
		logger.logp(Level.FINE, this.getClass().getSimpleName(), 
				"shutdown", String.format("Shutting down bluetooth connection to: %s", this.token));
		
		if (streaming) {
			converter.penError();
			streaming = false;
		} else {
			converter.penDisconnected();
		}
		
		try {
			this.connection.close();
			this.stream.close();
			// TODO: remove this after test phase
			if (bos!=null) bos.close();
		} catch (IOException e) {
			logger.logp(Level.WARNING, this.getClass().getSimpleName(), 
					"shutdown", String.format("could not close connection: %s", e.getMessage()));
			e.printStackTrace();
		}
	}

}
