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

import org.letras.android.R;
import org.letras.android.ServiceListEntry;

import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class RDPSLetrasServiceEntry implements ServiceListEntry {

	RawDataProcessor rawDataProcessor;
	CheckBox checkBox;
	
	public RDPSLetrasServiceEntry(RawDataProcessor rawDataProcessor) {
		this.rawDataProcessor = rawDataProcessor;
	}
	
	@Override
	public void configureCheckBox(CheckBox checkBox) {
		if (rawDataProcessor != null) {
			this.checkBox = null;
			checkBox.setChecked(rawDataProcessor.isActive());
		
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						start();
					} else {
						stop();
					}
				}
			});
		} else {
			this.checkBox = checkBox;
		}
	}

	@Override
	public int getIcon() {
		return R.drawable.pen;
	}

	@Override
	public int getText() {
		return R.string.rdps_service_name;
	}

	private void start() {
		rawDataProcessor.startForLastPen();
	}
	
	private void stop() {
		rawDataProcessor.stop();
	}
	
	@Override
	public Intent getConfigurationIntent(Context context) {
		return new Intent(context, org.letras.android.rdps.RDPServicePreferencesActivity.class);
	}
	
	@Override
	public void discardEntry() {
	}
}
