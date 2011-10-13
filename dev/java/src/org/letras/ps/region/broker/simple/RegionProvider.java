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
package org.letras.ps.region.broker.simple;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.letras.psi.iregion.IRegion;
import org.mundo.rt.Service;

/**
 * A mechanism that exposes {@link IRegion}s and their update notifications
 * to the {@link SimpleRegionBroker}.
 * 
 * @author Jannik Jochem
 *
 */
public abstract class RegionProvider extends Service {

	private List<IRegionProviderListener> listeners = new LinkedList<IRegionProviderListener>();

	/**
	 * @return all regions managed by this provider
	 */
	public abstract Collection<IRegion> getRegions();
	
	/**
	 * Add an update listener (e.g. {@link SimpleRegionBroker})
	 * @param listener
	 */
	public void addListener(IRegionProviderListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove an update listener
	 * @param listener
	 */
	public void removeListener(IRegionProviderListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Notify the listeners that newRegion was discovered
	 * @param newRegion
	 */
	protected void fireRegionAdded(IRegion newRegion) {
		for (IRegionProviderListener listener: listeners) {
			listener.regionAdded(newRegion, this);
		}
	}
	
	/**
	 * Notify the listeners that removedRegion is no longer available
	 * @param removedRegion
	 */
	protected void fireRegionRemoved(IRegion removedRegion) {
		for (IRegionProviderListener listener: listeners) {
			listener.regionRemoved(removedRegion, this);
		}
	}

}
