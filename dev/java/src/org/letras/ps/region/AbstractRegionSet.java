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

import org.letras.api.region.RegionData;
import org.letras.psi.iregion.DoIRegionSetListener;
import org.letras.psi.iregion.IRegionSet;
import org.mundo.rt.DoObject;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.rt.TypedArray;

/**
 * Abstract base class for {@link RegionSet}s that provides change notification
 * services to implementors.
 * 
 * @author Jannik Jochem
 *
 */
public abstract class AbstractRegionSet extends Service implements IRegionSet {
	private static final String DefaultServiceZone = "lan";
	private String uri;
	private String channel;
	
	private DoIRegionSetListener listener;
	
	public AbstractRegionSet(String uri, String channel) {
		this.uri = uri;
		this.channel = channel;
	}
	
	@Override
	public void init() {
		super.init();
		setServiceZone(DefaultServiceZone);
		Publisher pub = getSession().publish(DefaultServiceZone, channel());
		listener = new DoIRegionSetListener();
		Signal.connect(listener, pub);
	}

	@Override
	public String uri() {
		return uri;
	}

	@Override
	public String channel() {
		return channel;
	}

	/**
	 * Notify all subscribers that the set was updated
	 */
	protected void fireUpdate() {
		listener.regionSetUpdated(DoObject.ONEWAY);
	}
	
	public abstract TypedArray regions();
	public abstract void addRegion(RegionData regionToAdd);
	public abstract void updateRegion(RegionData updatedRegion);
	public abstract void removeRegion(RegionData regionToRemove);
}
