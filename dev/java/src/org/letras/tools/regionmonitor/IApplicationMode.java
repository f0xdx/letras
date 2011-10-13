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
package org.letras.tools.regionmonitor;

import javax.swing.JComponent;
import javax.swing.JMenu;

/**
 * The <code>IApplicationMode</code> is an interface that describes methods to
 * access the application modes view components as well as control the 
 * application mode running status.
 * 
 * @author niklas
 */
public interface IApplicationMode {

	/**
	 * Get the human readable description of this ApplicationMode
	 * @return
	 */
	public String getNameForMenuBar();

	/**
	 * Activates the ApplicationMode
	 * This is a notification for the ApplicationMode to set up all session
	 * specific components and connections.
	 */
	public void activate();
	
	/**
	 * Deactivate the ApplicationMode
	 * This is a notification for the ApplicationMode to reset session specific
	 * state so that it can be reactivated at later on.  
	 */
	public void deactivate();

	/**
	 * Used to retrieve a Menu that should be displayed when the
	 * ApplicationMode is active
	 * @return a menu or null if this option is not used
	 */
	public JMenu getMenuBarItem();

	/**
	 * Used to retrieve the Panel that should be displayed when the 
	 * ApplicationMode is active
	 * @return the panel to be displayed in the main area of the app
	 */
	public JComponent getPanel();
	
	

}
