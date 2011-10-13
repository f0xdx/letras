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
package org.letras.ps.rawdata.penmanager;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.rawdata.IPenAdapter;
import org.letras.ps.rawdata.IPenAdapterFactory;
import org.letras.ps.rawdata.IPenDriver;

/**
 * Generic implementation of the <code>IPenAdapterFactory</code> interface
 * suitable for all drivers.
 * 
 * @author felix_h
 * @version 0.0.l
 */
public class GenericPenAdapterFactory implements IPenAdapterFactory {

	// logger

	private static final Logger logger = Logger.getLogger("org.letras.ps.rawdata.penmanager");
	
	// defaults
	
	// members

	// constructors

	// methods
	
	/**
	 * Method defined in {@link org.letras.ps.rawdata.IPenAdapterFactory}
	 */
	@Override
	public IPenAdapter create(IPenDriver driver, String token) {

		// first create a suitable id
		String penId = PenManager.penId(driver.getClass().getName(), token);
		
		// now obtain the appropriate pen service (for this id)
		PenService ps = PenManager.getInstance().penService(penId);

		logger.logp(Level.FINE, "GenericPenAdapterFactory", "create",
				String.format("creating pen adapter (id=%s/service=%s)", penId, ps));
		
		// create a new pen adapter bound to this penId and service
		return new GenericPenAdapter(penId, ps);
	}
}
