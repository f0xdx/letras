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
package org.letras.app.demo;

import org.letras.android.LetrasService;
import org.letras.android.LetrasServiceUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple activity designed to show how to invoke the LetrasServiceUI
 * from inside your application by using Androids OptionMenu.
 * 
 * @author niklas
 */
public class Example extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView textView = new TextView(this);
		textView.setText("Press the menu button to show the options menu.");
		setContentView(textView);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this,LetrasService.class).setAction(LetrasService.START);
		if (startService(intent) != null) {
			Toast.makeText(this, "Letras was already started", 1200).show();
		} else {
			Toast.makeText(this, "Starting Letras...", 1200).show();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem letrasPreferences = menu.add("Letras Settings");
		letrasPreferences.setIntent(new Intent(this,LetrasServiceUI.class));
		letrasPreferences.setIcon(android.R.drawable.ic_menu_preferences);	
		return true;
	}
	
}
