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
package org.letras.ps.region.penconnector;
/**
 * The IPenConnection encapsulates methods from the PenService and additional 
 * status request methods which should be visible to the RegionProcessor.
 * @author niklas
 */
public interface IPenConnection {
	
	/**
	 * check whether the pen connection is connected and can receive events or not. 
	 * If it is not connected this is probably due to the pen having been denied by
	 * the current PenAccessConfiguration.
	 * @return true if pen is fully connected; false otherwise
	 */
	public boolean isActive();
	
	
	/**
	 * get the penID of the pen
	 * @return penID as string
	 */
	public String getPenId();
	
	/**
	 * get the zone in which the PenService is located 
	 * @return zone as string
	 */
	public String getZone();
}
