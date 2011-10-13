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
package org.letras.psi.ipen;

import org.mundo.annotation.mcMethod;
import org.mundo.annotation.mcRemote;

/**
 * This interface defines the methods provided by each pen as part
 * of the interface between the raw data and the region processing stages.
 * Implementing classes (or distributed  objects generated of this interface
 * already bound to
 * 
 * @author felix_h
 * @version 0.0.1
 */
@mcRemote
public interface IPen {

	/**
	 * This method can be used to obtain the unique id of this pen.
	 * 
	 * @return the unique pen id
	 */
	@mcMethod
	public String penId();
	
	/**
	 * Used to obtain the name of the channel this pen will publish
	 * its samples on. Usually the channel will already be used to receive
	 * the RMC, but its name is not necessarily known on the calling side.
	 * 
	 * @return name of the individual channel this pen publishes on
	 */
	@mcMethod
	public String channel();
	
	/**
	 * This method can be used to determine the current state of a pen.
	 * 
	 * @return state of the pen as defined in {@link org.letras.psi.ipen.IPenState}
	 */
	@mcMethod
	public int penState();
}
