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
package org.letras.ps.region.broker;

import org.letras.psi.iregion.IRegion;

/**
 * IRegionManager specifies the interface utilized by the region broker to inform
 * the region manager about new, changed or deleted regions.
 * @author niklas
 */
public interface IRegionManager {
	
	/**
	 * Deletes a region from the internal model
	 * @param regionToDelete the region to delete
	 */
	public void deleteRegion(IRegion regionToDelete);
	
	
	/**
	 * Add a new Region to the internal model.
	 * The region to add is supposed to be a region that is fully contained in at least
	 * one of the regions already in the internal model.
	 * @param regionToAdd the region to add 
	 */
	public void addRegion(IRegion regionToAdd);
	
	/**
	 * Updates a region and recalculates subtree if necessary
	 * @param regionToUpdate the region to update
	 */
	public void updateRegion(IRegion regionToUpdate);

}
