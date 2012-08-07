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
package org.letras.ps.region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.letras.api.region.RegionData;
import org.letras.psi.iregion.IRegionSet;
import org.letras.util.TypedArrayIdiom;
import org.mundo.rt.GUID;
import org.mundo.rt.TypedArray;

/**
 * A RegionSet is a container that allows batch publication of Regions.
 * To create a RegionSet programmatically, use this class. To load a RegionSet
 * from a region document file (.regions), use {@link RegionDocumentRegionSet}.
 * 
 * @author Jannik Jochem
 *
 */
public class RegionSet extends AbstractRegionSet implements IRegionSet {
	/**
	 * uri -> region
	 */
	private Map<String, RegionData> regions;
	
	public RegionSet(String uri, String channel, Iterable<? extends RegionData> regions) {
		super(uri, channel);
		this.regions = new HashMap<String, RegionData>();
		for (RegionData region: regions) {
			this.regions.put(region.uri(), region);
		}
	}
	
	public RegionSet(String uri, Collection<? extends RegionData> regions) {
		this(uri, "RegionSet." + new GUID().shortString(), regions);
	}
	
	@Override
	public TypedArray regions() {
		return TypedArrayIdiom.fromCollection(regions.values());
	}
	
	@Override
	public void addRegion(RegionData regionToAdd) {
		regions.put(regionToAdd.uri(), regionToAdd);
		fireUpdate();
	}
	
	@Override
	public void updateRegion(RegionData updatedRegion) {
		if (regions.containsKey(updatedRegion.uri()))
				addRegion(updatedRegion);
		fireUpdate();
	}
	
	@Override
	public void removeRegion(RegionData regionToRemove) {
		regions.remove(regionToRemove.uri());
		fireUpdate();
	}
}
