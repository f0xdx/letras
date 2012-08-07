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
package org.letras.psi.iregion;

import org.letras.api.region.RegionData;
import org.mundo.annotation.mcMethod;
import org.mundo.annotation.mcRemote;
import org.mundo.rt.TypedArray;

/**
 * A region set is a container for the publication of large amounts of regions.
 * When large numbers of regions are published through the normal means of 
 * {@link IRegion} or {@link RegionDocumentPublisher}, a large amount of RMC
 * calls take place. When network latency is high, this may cause problems.
 * 
 * RegionSets allow the publication of regions with a minimum amount of RMC calls.
 * However, they should only be used when the set of regions rarely change. Updates
 * to the region set cause all regions to be re-transmitted and may thus cause
 * large amounts of network traffic.
 * 
 * Information about the regions is send as a list of RegionData objects.
 * 
 * @author Jannik Jochem
 *
 */
@mcRemote
public interface IRegionSet {
	
	/**
	 * @return the URI that uniquely identifies this region set
	 */
	@mcMethod
	public String uri();

	/**
	 * @return the channel on which the region set publishes change notifications
	 */
	@mcMethod
	public String channel();
	
	/**
	 * @return all regions in the set send as {@link RegionData} objects
	 */
	@mcMethod
	public TypedArray regions();
}
