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
package org.letras.android;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.letras.android.rdps.RDPSLetrasServiceEntry;
import org.letras.android.rdps.RawDataProcessor;
import org.letras.android.rps.RPSLetrasServiceEntry;
import org.letras.android.rps.RegionProcessorAdapter;
import org.mundo.rt.Metaclass;
import org.mundo.rt.Mundo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;


/**
 * The <code>LetrasService</code> coordinates access to the Android implementation of the 
 * Letras-Stages like the RawDataProcessing or the RegionProcessing in addition, the 
 * <code>LetrasService</code> is responsible for keeping the application process alive and
 * avoid forced shutdowns by the Android OS.
 * 
 * The <code>LetrasService</code> should be started by sending an explicit Intent like this:
 * <pre>
 * Intent intent = new Intent(this,LetrasService.class).setAction(LetrasService.START);
 * startService(intent);
 * </pre>
 * You can also check whether the LetrasService was already running by testing the return value
 * of {@link #startService(Intent)} like this:
 * <pre>
 * if (startService(intent) != null) {
 *	//LetrasService was already running
 * else
 *	//LetrasService is starting in the background
 * </pre>
 * 
 * If something has to be done as soon as the LetrasService has started, a {@link ServiceConnection}
 * together with {@link #bindService(Intent, ServiceConnection, int)} can be used to get a callback.
 * However, the {@link #startService(Intent)} must still be called to setup the Letras-stack.
 * 
 * @author niklas
 *
 */
public class LetrasService extends Service {

	public static final String EXTRA_MESSENGER = "org.letras.android.LetrasService.Messenger";
	public static final int RESULT_MUNDO_STARTED = 0;
	public static final int RESULT_ERROR = -1;
	
	public static final String START = "org.letras.android.LetrasService.START";
	public static final String STOP = "org.letras.android.LetrasService.STOP";
	
	
	private RawDataProcessor rawDataProcessor;
	private RegionProcessorAdapter regionProcessor;
	
	
	/**
	 * Used to access the Mundo service (only inside the same process).
	 */
	public class LetrasServiceBinder extends Binder {
		public LetrasService getService() {
			return LetrasService.this;
		}
	}

	private final IBinder binder = new LetrasServiceBinder();

	
	// constructors

	// methods

	/* (non-Javadoc)
	 * @see android.app.Service#onBind()
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (START.equals(intent.getAction())) {
			int result = RESULT_ERROR;
			
			this.startForeground(R.id.letras_service_id, this.createNotification());
			if (Mundo.getState() == Mundo.STATE_UNINITIALIZED) {
				try {
					Metaclass.loadFrom(this.getClass().getClassLoader());
					Mundo.setConfigXML(new InputStreamReader(this.getClass().
							getClassLoader().getResourceAsStream("assets/node.conf.xml"), "UTF-8"));
					Mundo.init();
					result = RESULT_MUNDO_STARTED;
					try { 
						getAssets().open("rdps.conf.xml").close();
						rawDataProcessor = new RawDataProcessor();
						rawDataProcessor.init(this);
						rawDataProcessor.startForLastPen();
					} catch (IOException e) {}
					try {
						getAssets().open("rps.conf.xml").close();
						regionProcessor = new RegionProcessorAdapter();
						regionProcessor.start();
					} catch (IOException e) {}
					
					
				} catch (UnsupportedEncodingException e) {
					
				}
			} else {
				result = RESULT_MUNDO_STARTED;
			}
			
			Bundle extras= intent.getExtras();
			if (extras!=null) {
				Messenger messenger=(Messenger)extras.get(EXTRA_MESSENGER);
				Message msg=Message.obtain();

				msg.arg1=result;

				try {
					messenger.send(msg);
				}
				catch (android.os.RemoteException e1) {
					Log.w(getClass().getName(), "Exception sending message", e1);
				}
			}
			
		}
		else if (STOP.equals(intent.getAction())) {
			//hide the service again and remove all notifications
			this.stopForeground(true);

			// shutdown mundo
			if (Mundo.getState() == Mundo.STATE_INITIALIZED) {
				Mundo.shutdown();
			}

			stopSelf();
		}
		return START_NOT_STICKY;		
	}

	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		//hide the service again and remove all notifications
		this.stopForeground(true);
		
		super.onDestroy();
	}
	
	/**
	 * Private helper method used to generate the displayed notification.
	 * 
	 * @return a new notification describing the Android Mundo Service
	 */
	private Notification createNotification() {
		CharSequence notificationText = this.getText(R.string.letras_node_running);
		
		Notification notification = 
				new Notification(
						R.drawable.letras_icon,
						notificationText,
						System.currentTimeMillis()
				);
		
		// The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
               new Intent(this, LetrasServiceUI.class), 0);
        
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.letras_service_name),
                       notificationText, contentIntent);
        
		return notification;
	}
	
	public List<ServiceListEntry> getLetrasServiceListEntries() {
		List<ServiceListEntry> serviceEntries = new ArrayList<ServiceListEntry>();
		serviceEntries.add(new RDPSLetrasServiceEntry(rawDataProcessor));
		serviceEntries.add(new RPSLetrasServiceEntry(regionProcessor));
		return serviceEntries;
	}	
	
}
