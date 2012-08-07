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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.api.region.RegionData;
import org.letras.ps.region.RegionSet;
import org.letras.psi.iregion.DoIRegionSet;
import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.IRegionSetListener;
import org.letras.util.TypedArrayIdiom;
import org.mundo.rt.RMCException;
import org.mundo.rt.Signal;
import org.mundo.service.ServiceInfo;

/**
 * A region provider that manages the regions of a single {@link RegionSet}, which is in 
 * turn managed by {@link RegionSetProvider}.
 * 
 * @author Jannik Jochem
 *
 */
public class SingleRegionSetProvider extends RegionProvider {

	private static Logger logger = Logger.getLogger("org.letras.ps.region.broker.simple");

	private ServiceInfo serviceInfo;
	private String searchZone;
	private DoIRegionSet regionSet;

	/**
	 * uri -> region
	 */
	private Map<String, RegionData> regions = new HashMap<String, RegionData>();
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private IRegionSetListener listener = new IRegionSetListener() {
		@Override
		public void regionSetUpdated() {
			synchronizeRegionSet();
		}
	};
	public SingleRegionSetProvider(ServiceInfo serviceInfo, String searchZone) {
		this.serviceInfo = serviceInfo;
		this.searchZone = searchZone;
		setServiceZone(searchZone);
	}
	
	@Override
	public void init() {
		regionSet = new DoIRegionSet(serviceInfo.doService);
		try {
			lock.writeLock().lock();
			List<RegionData> initialRegions = TypedArrayIdiom.toList(regionSet.regions(), RegionData.class);
			Signal.connect(getSession().subscribe(searchZone, regionSet.channel()), listener);
			for (RegionData region: initialRegions) {
				regions.put(region.uri(), region);
			}
			lock.writeLock().unlock();
			lock.readLock().lock();
			for (RegionData region: initialRegions) {
				fireRegionAdded(region);
			}
			lock.readLock().unlock();
		} catch (RMCException e) {
			logger.logp(Level.WARNING, SingleRegionSetProvider.class.getName(), "init", "Error connecting to RegionSet", e);
		}
	}
	
	@Override
	public Collection<IRegion> getRegions() {
		lock.readLock().lock();
		List<IRegion> result = new ArrayList<IRegion>(regions.values());
		lock.readLock().unlock();
		return result;
	}

	/**
	 * Because {@link RegionSet}s are RESTful, i.e. they are only transferred whole,
	 * the actual changes that have taken place between two updates have to be computed
	 * by comparing the previous version of the region set to the updated version.<p>
	 * This is somewhat complicated, but still better than the alternative of sending
	 * individual updates over an unreliable network, which would cause the local version
	 * of the region set to diverge from its real contents pretty quickly.
	 */
	protected void synchronizeRegionSet() {
		try {
			/*
			 * We need to partition the regions into three sets:
			 * 1. newly added regions
			 * 2. removed regions
			 * 3. updated regions (represented as 1 \cap 2)
			 */
			List<RegionData> newRegions = TypedArrayIdiom.toList(regionSet.regions(), RegionData.class);
			Set<String> newRegionUris = new HashSet<String>();
			List<RegionData> addedRegions = new ArrayList<RegionData>();
			List<RegionData> removedRegions = new ArrayList<RegionData>();
			lock.readLock().lock();
			for (RegionData region: newRegions) {
				newRegionUris.add(region.uri());
				RegionData originalRegion = regions.get(region.uri());
				if (originalRegion != null) {
					if (!originalRegion.deepEquals(region)) {
						// region updated
						addedRegions.add(region);
						removedRegions.add(originalRegion);
					}
				} else {
					// region added
					addedRegions.add(region);
				}
			}
			for (String uri: regions.keySet()) {
				if (!newRegionUris.contains(uri)) {
					// region removed
					removedRegions.add(regions.get(uri));
				}
			}
			lock.readLock().unlock();
			lock.writeLock().lock();
			/*
			 * Commit the changes to the local cache
			 */
			for (RegionData removedRegion: removedRegions) {
				regions.remove(removedRegion.uri());
			}
			for (RegionData addedRegion: addedRegions) {
				regions.put(addedRegion.uri(), addedRegion);
			}
			lock.writeLock().unlock();
			lock.readLock().lock();
			/*
			 * Notify the observers
			 */
			for (RegionData removedRegion: removedRegions) {
				fireRegionRemoved(removedRegion);
			}
			for (RegionData addedRegion: addedRegions) {
				fireRegionAdded(addedRegion);
			}
			lock.readLock().unlock();
		} catch (RMCException e) {
			logger.logp(Level.WARNING, SingleRegionSetProvider.class.getName(), "init", "Error updating RegionSet", e);
		}
	}

}
