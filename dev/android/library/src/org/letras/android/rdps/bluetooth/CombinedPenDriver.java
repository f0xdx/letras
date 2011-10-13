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
package org.letras.android.rdps.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.letras.android.rdps.bluetooth.decoder.Decoder;
import org.letras.ps.rawdata.IPenAdapterFactory;
import org.letras.ps.rawdata.IPenDriver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * The <code>CombinedPenDriver</code> is a special {@link IPenDriver} developed
 * for Android. The CombinedPenDriver supports several {@link PEN_MODELS} but only 
 * searches for one pen model at a time which must be given as an argument
 * when {@link #startConnectionAttempt(PEN_MODELS)} gets called. It also only
 * allows one pen of that specific pen model to connect in order to preserve 
 * battery life.
 *
 * The CombinedPenDriver implements {@link IPenDriver} but you still have to call
 * {@link #startConnectionAttempt(PEN_MODELS)} manually to make sure that
 * Bluetooth is activated beforehand.
 * 
 * @author niklas
 *
 */
public class CombinedPenDriver implements IPenDriver {

	//constants
	/**
	 * The UUID of the SPP service used for specifying the correct ServiceRecord 
	 * for the Bluetooth discovery protocol
	 */
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	/**
	 * Tag for the logger
	 */
	private static final String TAG = "rdps.bluetooth.Connector";
	
	//members
	
	/**
	 * the listener which is informed on status changes with a human readable status message
	 */
	private StatusMessageListener connectionListener;
	
	/**
	 * the factory that providing instances of IPenAdapter to be used when a pen connects
	 */
	private IPenAdapterFactory penAdapterFactory;
	
	/**
	 * the thread for handling bluetooth pen discovery
	 */
	private BluetoothPenSearcher bluetoothPenSearcher;

	//enums
	
	/**
	 * States of the CombinedPenDriver
	 */
	private enum STATES {
		IDLE,
		SEARCHING,
		CONNECTED
	}
	
	
	/**
	 * Standard constructor <br>
	 * Use {@link #CombinedPenDriver(StatusMessageListener)} if you want to receive status updates.
	 */
	public CombinedPenDriver() {
		//provide an empty implementation as status message listener.
		this(new StatusMessageListener() {
			@Override
			public void updateStatus(String statusMessage) {}
		});
	}
	
	/**
	 * Constructor with a specified receiver for status messages
	 * @param listener
	 */
	public CombinedPenDriver(StatusMessageListener listener) {
		this.connectionListener = listener;
	}
	
	/**
	 * start searching and eventually connect to the specified penModel. 
	 * If the a search is already a progress or a pen is already connected 
	 * the search or connection will be cancelled and a new search is 
	 * started for the given pen model.
	 * <p>
	 * This method is non-blocking and will spawn a Thread to handle
	 * searching and connecting to the pen.
	 * 
	 * @param penModel the pen model to connect to
	 * @return true: if the search has successfully been started
	 * 		   false: if the bluetooth module is disabled and the 
	 * search has not been started
	 */
	public synchronized boolean connectPenWhenAvailable(PEN_MODELS penModel) {
		switch (getCurrentState()) {
		case IDLE:
			return startConnectionAttempt(penModel);
		case SEARCHING:
		case CONNECTED:
			cancel();
			return startConnectionAttempt(penModel);
		default:
			return false;
		}
	}
	
	/**
	 * cancel any ongoing search or connection to a pen.
	 */
	public synchronized void cancel() {
		if (bluetoothPenSearcher != null) {
			//stop the background thread
			bluetoothPenSearcher.cancel();
			bluetoothPenSearcher = null;
		}
	}

	/**
	 * This does nothing because searching for a pen should not start automatically
	 */
	@Override
	public void init() {}

	@Override
	public void inject(IPenAdapterFactory factory) {
		penAdapterFactory = factory;
	}

	/**
	 * forwards to {@link BluetoothPenSearcher#cancel()}
	 */
	@Override
	public void shutdown() {
		this.cancel();
	}	
	
	/**
	 * return the drivers current state.
	 * @return drivers state as one of the {@link STATES}
	 */
	private STATES getCurrentState() {
		if (bluetoothPenSearcher == null) {
			return STATES.IDLE;
		}
		return bluetoothPenSearcher.connected ? STATES.CONNECTED : STATES.SEARCHING;
		
	}
	
	/**
	 * start connecting to the pen
	 * @param penModel pen model to connect to
	 */
	private boolean startConnectionAttempt(PEN_MODELS penModel) {
		bluetoothPenSearcher = new BluetoothPenSearcher(penModel);
		if (bluetoothPenSearcher.isReady()) {
			new Thread(bluetoothPenSearcher).start();
			connectionListener.updateStatus("Searching for a " + penModel.friendlyName);
			return true;
		} else { 
			bluetoothPenSearcher = null;
			connectionListener.updateStatus("Problem: Bluetooth disabled");
			return false;
		}
			
	}
	
	/**
	 * The BluetoothPenSearcher handles searching for pens and connecting a pen according
	 * to the specified pen model.
	 * 
	 * @author niklas
	 */
	class BluetoothPenSearcher implements Runnable {
		
		/**
		 * the bluetooth server socket for SPP.<br>
		 * If the serverSocket is null, it means the BluetoothPenSearches is not ready.
		 * The main reason for that is a disabled Bluetooth radio
		 */
		private final BluetoothServerSocket serverSocket;
		
		/**
		 * the pen model to search for
		 */
		private final PEN_MODELS penModel;
		
		/**
		 * this is true iff a pen is connected to the appropriate decoder
		 */
		private boolean connected = false;
		
		/**
		 * Constructer with pen model argument
		 * @param penModel the pen model for which to search and connect.
		 */
		public BluetoothPenSearcher(PEN_MODELS penModel) {
			this.penModel = penModel;
			
			
			 //Try to get the btAdapter. this returns null if the bt radio is disabled
			BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
			
			if (btAdapter == null) {
				Log.e(TAG, "can't get the BluetoothAdapter");
				serverSocket = null;
				return;
			}
			
			//Bluetooth is enabled, so let's create a SPP socket
			BluetoothServerSocket bts = null;
			try {
				Log.v(TAG, "creating SPP Port with name: " + penModel.sppChannelName);
				bts = btAdapter.listenUsingRfcommWithServiceRecord(penModel.sppChannelName, SPP_UUID);
			} catch (IOException ioe) {
				Log.e(TAG, "can't get a BluetoothServerSocket", ioe);
			}
			serverSocket = bts;
		}
		
		/**
		 * check if the BluetoothPenSearcher is all setup to run
		 * @return state
		 */
		public boolean isReady() {
			return serverSocket != null;
		}
		
		public void run() {
			if (serverSocket != null) {
			try {
				Log.v(TAG, "waiting for pen to connect");
				//infinity loop is broken when the serverSocket gets closed
				while(true) {
					BluetoothSocket socket = serverSocket.accept();
					connectionListener.updateStatus("Connected to a " + penModel.friendlyName);
					connected = true;
					Log.v(TAG, "pen connected");
					beginProcessing(socket.getInputStream(), socket.getRemoteDevice().getAddress());
					connectionListener.updateStatus("Connection lost");
					connectionListener.updateStatus("Searching for a " + penModel.friendlyName);
				}
			} catch (IOException ioe) {
				return;
			}
			} else {
				Log.w(TAG, "bluetooth not available");
				bluetoothPenSearcher = null;
			}
		}
		
		/**
		 * stops the search or breaks the connection to a pen and stops the thread
		 */
		public void cancel() {
			try {
				serverSocket.close();
			} catch (IOException e) {
				return;
			}
		}
		
		/**
		 * create a decoder and instruct to process the stream
		 * @param inputStream the stream to decode
		 * @param bluetoothAddress the streams origin
		 */
		private void beginProcessing(InputStream inputStream, String bluetoothAddress) {
			Decoder decoder;
			try {
				decoder = (Decoder) penModel.decoderClass.getConstructor((Class[]) null).newInstance((Object[]) null);
				decoder.beginDecoding(inputStream, penAdapterFactory.create(CombinedPenDriver.this, bluetoothAddress));
			} catch (Exception e) {
				Log.e(TAG, "the decoder could not be instantiated");
			}
		}
	}


	
}
