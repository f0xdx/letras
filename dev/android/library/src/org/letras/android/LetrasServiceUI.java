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

import java.util.List;

import org.letras.android.LetrasService.LetrasServiceBinder;

import android.app.ListActivity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This activity displays the list of available Letras services.
 * <br>
 * The services can then be activated or deactivated by clicking on a checkbox and
 * they can optionally be configured in detail by pressing on the entry
 * in the list.
 * @author niklas
 *
 */
public class LetrasServiceUI extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ServiceListAdapter(this));
	}
	
	/**
	 * ListAdapter containing the logic to create and handle the list model for the
	 * Letras services
	 * @author niklas
	 */
	private static class ServiceListAdapter extends BaseAdapter {
		
		ServiceConnection letrasServiceConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				LetrasService letrasService = ((LetrasServiceBinder) service).getService();
				List<ServiceListEntry> serviceEntryList = letrasService.getLetrasServiceListEntries();
				serviceEntries = new ServiceListEntry[serviceEntryList.size()];
				serviceEntryList.toArray(serviceEntries);
				serviceListItems = new View[serviceEntries.length];
				ServiceListAdapter.this.notifyDataSetChanged();
			}
		};
		
		/**
		 * used to inflate menuitems
		 */
		private LayoutInflater inflater;
		
		/**
		 * context of enclosing Activity
		 */
		private Context context;
		
		/**
		 * entries in the list
		 */
		private ServiceListEntry[] serviceEntries = {};
		
		/**
		 * menu items conforming to the entries in the list
		 */
		private View[] serviceListItems = {};
		
		public ServiceListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			this.context = context;
			context.bindService(new Intent(context, LetrasService.class), letrasServiceConnection, Service.BIND_AUTO_CREATE);
		}
		
		@Override
		public int getCount() {
			return serviceListItems.length;
		}

		@Override
		public Object getItem(int arg0) {
			return serviceEntries[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (serviceListItems[arg0] == null) {
				ServiceListEntry serviceEntry = serviceEntries[arg0];
				
				//inflate menu item
				View serviceListItem = inflater.inflate(R.layout.icon_text_list_item, null);
				
				//set text
				TextView text = (TextView) serviceListItem.findViewById(R.id.list_text);
				text.setText(serviceEntry.getText());
				
				//set icon
				ImageView icon = (ImageView) serviceListItem.findViewById(R.id.list_icon);
				icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), serviceEntry.getIcon()));
				
				//configure checkbox
				CheckBox checkBox = (CheckBox) serviceListItem.findViewById(R.id.list_checkbox);
				serviceEntry.configureCheckBox(checkBox);
				
				//configure tapping intent
				final Intent configurationIntent = serviceEntry.getConfigurationIntent(context);
				if (configurationIntent != null) {
					View listItemView = serviceListItem.findViewById(R.id.list_item);
					listItemView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							context.startActivity(configurationIntent);
						}
					});
					TextView configurationText = (TextView) serviceListItem.findViewById(R.id.list_configure_text);
					configurationText.setVisibility(View.VISIBLE);
					configurationText.setText(R.string.tap_to_configure);
				}
				
				//store in list
				this.serviceListItems[arg0] = serviceListItem;
			}	
			return serviceListItems[arg0];
		}
		
		void onDestroy() {
			for (ServiceListEntry serviceEntry : serviceEntries) {
				serviceEntry.discardEntry();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		((ServiceListAdapter) getListAdapter()).onDestroy();
		super.onDestroy();
	}
}
