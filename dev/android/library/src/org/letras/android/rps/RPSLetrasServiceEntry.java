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
package org.letras.android.rps;

import org.letras.android.R;
import org.letras.android.ServiceListEntry;

import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class RPSLetrasServiceEntry implements ServiceListEntry{

	CheckBox checkBox;
	private RegionProcessorAdapter regionProcessorAdapter;
	
	public RPSLetrasServiceEntry(RegionProcessorAdapter rpa) {
		this.regionProcessorAdapter = rpa;
	}
	
	@Override
	public void configureCheckBox(CheckBox checkBox) {
		if (regionProcessorAdapter != null) {
			this.checkBox = null;
			checkBox.setChecked(regionProcessorAdapter.isActive());
		
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
		return R.drawable.region;
	}

	@Override
	public int getText() {
		return R.string.rps_service_name;
	}

	private void start() {
		regionProcessorAdapter.start();
	}
	
	private void stop() {
		regionProcessorAdapter.stop();
	}
	
	@Override
	public Intent getConfigurationIntent(Context context) {
		return null;
	}
	
	@Override
	public void discardEntry() {
	}
	
}
