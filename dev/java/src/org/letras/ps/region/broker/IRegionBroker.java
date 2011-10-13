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

import java.util.List;

import org.letras.psi.iregion.IRegion;
import org.mundo.service.IConfigure;

/**
 * <code>IRegionBroker</code> defines an abstracted interface for selecting and querying 
 * {@link IRegion} objects from the distributed collection of all currently available 
 * regions in some distributed system.
 * The system used is defined by the class implementing the RegionBroker interface.
 * <p>
 * All classes implementing <code>IRegionBroker</code> also implement {@link IConfigure}
 * so that they can be configured through the node.conf.xml. To lookup possible configuration
 * parameters look at the documentation of the concrete RegionBroker.
 * 
 * @author niklas
 *
 */
public interface IRegionBroker extends IConfigure {

	/**
	 * Request a list of {@link IRegion} objects at the given location.<br>
	 * Note: a call to this function can take some time depending on the underlying implementation
	 * 
	 * @param xcoordinate
	 * @param ycoordinate
	 * @return a list of regions at the given coordinate. Returns empty list if no appropriate region found
	 */
	public List<IRegion> requestRegionsAtCoordinate(double xcoordinate, double ycoordinate); 
	
	/**
	 * Set the {@link IRegionManager}
	 * The region manager will be notified when regions of interest depart, change or get new children.
	 * Regions of interest are regions that have already been requested by the region manager with a call
	 * to {@link #requestRegionsAtCoordinate(double, double)}
	 * @param regionManager to be notified
	 */
	void setRegionManager(IRegionManager regionManager);
	
	
}
