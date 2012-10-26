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
 * @author niklas
 * @version 0.0.1
 */
class BluetoothConnectionHandler extends Thread {
	
	//logger
	
	private static final Logger logger = Logger.getLogger("org.letras.ps.rawdata.driver.logitech");
	
	//members
	
	private final StreamConnection connection;

	/**
	 * Route the received bytes and pen on/off state to this converter
	 */
	private final ByteStreamConverter converter;
	
	/**
	 * reference to the bluetooth connector in case the handler needs to be shut down
	 */
	private final BluetoothConnector connector;
	
	/**
	 * The String which has been given to the IPenAdapterFactory on creating a new instance of IPenAdapter
	 * For debugging purposes only.
	 */
	private final String token;
	
	private boolean streaming;
	
	private boolean running;

	private InputStream stream;
	
	//constructor
	
	/**
	 * 
	 * @param connection
	 * @param converter
	 * @param connector
	 * @param token
	 */
	public BluetoothConnectionHandler(StreamConnection connection, ByteStreamConverter converter, BluetoothConnector connector, String token) {
		this.connection = connection;
		this.converter = converter;
		this.connector = connector;
		this.token = token;
		this.streaming = true;
	}
	
	@Override
	public void run() {
		running = true;
		try {
			stream = this.connection.openInputStream();
			
			int data;
			
			converter.penConnected();
			// now read until the end of the stream has been reached (i.e. the next value returned is -1)
			while (running) {
				data = stream.read();
				if (data == -1) {
					if (streaming) {
						streaming = false;
						shutdown();
						logger.logp(Level.FINE, "BluetoothConnectionHandler", "run", String.format("pen has been disconnected: %s", token) );
					}
				} else {
					streaming = true;
					converter.handleByte(data);
				}
			}
			
			
				
		} catch (final IOException e) {
			logger.logp(Level.WARNING, "BluetoothConnectionHandler", "run", String.format("could not read from stream. I will try to shutdown: %s", e.getMessage()));
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
		logger.logp(Level.FINE, "BluetoothConnectionHandler", "shutdown", String.format("Shutting down bluetooth connection to: %s", this.token));
		
		if (streaming) {
			converter.penError();
			streaming = false;
		} else {
			converter.penDisconnected();
		}
		
		try {
			this.connection.close();
			this.stream.close();
		} catch (final IOException e) {
			logger.logp(Level.WARNING, "BluetoothConnectionHandler", "shutdown", String.format("could not close connection: %s", e.getMessage()));
			e.printStackTrace();
		}
	}

}
