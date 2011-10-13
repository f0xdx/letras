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
package org.letras.android.rdps;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.letras.android.LetrasServiceUI;
import org.letras.android.R;
import org.letras.android.rdps.bluetooth.CombinedPenDriver;
import org.letras.android.rdps.bluetooth.PEN_MODELS;
import org.letras.android.rdps.bluetooth.StatusMessageListener;
import org.letras.ps.rawdata.penmanager.PenManager;
import org.mundo.rt.Mundo;
import org.mundo.rt.TypedMap;
import org.mundo.xml.XMLDeserializer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * The RawDataProcessor is the equivalent to the RawDataProcessor from android-java
 * and has been reimplemented for Android to be able to run without the support for
 * plugins by using a Android-specific {@link CombinedPenDriver} instead
 * @author niklas
 * 
 */
public class RawDataProcessor {
	// logger tag
	private final String TAG = "RDPService";

	// const
	private static final String DEFAULT_PEN_ZONE = "rt";
	private static final String DEFAULT_PEN_MODEL= "Nokia SU-1B";

	private SharedPreferences rdpPreferences;

	// members

	private CombinedPenDriver penDriver;
	private PenManager penManager;

	private Context context;
	
	
	public void init(Context c) {
		context = c;
		rdpPreferences = PreferenceManager.getDefaultSharedPreferences(c);
		
		if (rdpPreferences.getBoolean("firstStart", true)) {
			initializeSharedPreferencesFromXML();
			rdpPreferences.edit().putBoolean("firstStart", false).commit();
		}
		

		startPenManager();
		startPenDriver();
		
		Log.v(TAG, "initialized");
	}

	private void initializeSharedPreferencesFromXML() {
		try {
			TypedMap config = (TypedMap) new XMLDeserializer().deserializeObject(new InputStreamReader(this.getClass().
					getClassLoader().getResourceAsStream("assets/rdps.conf.xml"), "UTF-8"));
			if (config == null) {
				Log.e(TAG, "error reading config file from assets/rdps.conf.xml");
				return;
			}
			Editor editor = rdpPreferences.edit();
			String papZone = config.containsKey("pap-zone") ? config.getString("pap-zone") : DEFAULT_PEN_ZONE;
			String penModel = config.containsKey("pen-model") ? config.getString("pen-model") : DEFAULT_PEN_MODEL;
			editor.putString(context.getString(R.string.rdp_service_pap_zone_preference), papZone);
			editor.putString(context.getString(R.string.rdp_service_penmodel_preference), penModel);
			editor.commit();
		} catch (UnsupportedEncodingException e) {
			
		}
	}

	
	public void startForLastPen() {
		if (penManager == null) {
			startPenManager();
		}
		if (penDriver == null) {
			startPenDriver();
		}
		connectToPen(getLastPenModelNameFriendlyName());
	}
	
	public void changeToPen(String penModel) {
		setLastPenModelFriendlyName(penModel);
		penDriver.cancel();
		connectToPen(getLastPenModelNameFriendlyName());
	}

	private void startPenManager() {
		penManager = PenManager.getInstance();
		TypedMap configMap = new TypedMap();
		
		String zone = rdpPreferences.getString(context.getString(R.string.rdp_service_pap_zone_preference), DEFAULT_PEN_ZONE);
	
		configMap.putString("pap-zone", zone);
		penManager.setServiceConfig(configMap);
		Mundo.registerService(penManager);
	}

	private void startPenDriver() {
		penDriver = new CombinedPenDriver(new StatusMessageListener() {
			public void updateStatus(String statusMessage) {
				sendNotification(statusMessage);
			}
		});
		penDriver.inject(penManager.selectPenAdapterFactory(penDriver));
	}
	

	/**
	 * Request the default pen model currently configured to be used
	 * 
	 * @return
	 */
	public String getLastPenModelNameFriendlyName() {
		return rdpPreferences.getString(context.getString(R.string.rdp_service_penmodel_preference), DEFAULT_PEN_MODEL);
	}

	/**
	 * Set the current default pen model
	 * 
	 * @param friendlyName
	 */
	private void setLastPenModelFriendlyName(String friendlyName) {
		rdpPreferences.edit().putString(context.getString(R.string.rdp_service_penmodel_preference), friendlyName).commit();
	}
	

	public void stop() {
		if (penDriver != null) {
			penDriver.cancel();
			penDriver = null;
		}
		if (penManager != null) {
			Mundo.unregisterService(penManager);
			penManager = null;
		}
		clearNotifications();
		Log.v(TAG, "stopped");
	}

	/**
	 * get a collection of the human-friendly names of all pen models currently supported.
	 * 
	 * @return pen models human friendly names
	 */
	public String[] availablePenModels() {
		return PEN_MODELS.penModels();
	}

	/**
	 * Try connect to a pen that conforms to the given pen model
	 * 
	 * @param friendlyName
	 */
	private void connectToPen(String friendlyName) {
		PEN_MODELS model = PEN_MODELS
				.getPenModelWithFriendlyName(friendlyName);
		if (model != null) {
			penDriver.cancel();
			penDriver.connectPenWhenAvailable(model);
		}
	}
	
	public boolean isActive() {
		return penDriver != null;
	}
	
	/**
	 * convenience method for creating the ongoing notifications
	 * 
	 * @param message
	 * @return
	 */
	private void sendNotification(String message) {

		Notification notification = new Notification(R.drawable.pen, message,
				System.currentTimeMillis());

		// The PendingIntent to launch our control activity if the user selects
		// this notification
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, LetrasServiceUI.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(context,
				context.getText(R.string.rdps_service_name), message, contentIntent);

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(R.id.rdps_service_id, notification);
	}
	
	private void clearNotifications() {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(R.id.rdps_service_id);
	}
}
