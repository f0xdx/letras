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

import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;

/**
 * Common interface for entries to be displayed in the LetrasServiceUI list.
 * Each entry is usually backed by a Letras Service representing a specific
 * stage in the Letras Processing pipeline.
 * @author niklas
 *
 */
public interface ServiceListEntry {
	/**
	 * get the ressource id of a drawable to be used as icon in the list
	 * @return
	 */
	public int getIcon();
	
	/**
	 * get the ressource id of a string to be used as main text in the list
	 * @return
	 */
	public int getText();
	
	/**
	 * configure the given checkbox to represent the state of the service and
	 * attach appropriate eventhandlers.
	 * @param checkBox
	 */
	public void configureCheckBox(CheckBox checkBox);
	
	/**
	 * get the Intent that should be activated when the area of the text is pressed
	 * @param context the android context in which to start the intent
	 * @return the Intent or null if no action should occur
	 */
	public Intent getConfigurationIntent(Context context);
	
	/**
	 * callback method that is invoked when the entry is about to be discarded from the
	 * list or the application closes.
	 */
	public void discardEntry();
}
