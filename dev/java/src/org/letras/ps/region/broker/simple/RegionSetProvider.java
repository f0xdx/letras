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
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.region.RegionSet;
import org.letras.psi.iregion.IRegion;
import org.mundo.rt.GUID;
import org.mundo.rt.Mundo;
import org.mundo.service.ResultSet;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;

/**
 * A RegionProvider that watches the network for {@link RegionSet}s and manages their
 * regions through individual sub-providers {@link SingleRegionSetProvider}.
 * 
 * @author Jannik Jochem
 *
 */
public class RegionSetProvider extends RegionProvider implements IRegionProviderListener {
	
	private static Logger logger = Logger.getLogger("org.letras.ps.region.broker.simple");

	private String searchZone;
	
	private Map<GUID, SingleRegionSetProvider> regionSetProviders = new HashMap<GUID, SingleRegionSetProvider>();
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public RegionSetProvider(String searchZone) {
		this.searchZone = searchZone;
		setServiceZone(searchZone);
	}
	
	/** 
	 * inner class for implementation of IResultSet.ISignal
	 * an instance of this class is used as listener for the continuous query.
	 * 
	 * @author niklas
	 *
	 */
	private class ResultHandler implements ResultSet.ISignal {
		@Override
		public void removing(ResultSet rs, int offset, int n) {
			for (Object obj  : rs.getList().subList(offset, offset + n)) {
				if (obj instanceof ServiceInfo) {
					final ServiceInfo regionSetServiceInfo = (ServiceInfo) obj;
					logger.logp(Level.FINEST, "RegionSetProvider", "removing", String.format("RegionSet %s is leaving",
							regionSetServiceInfo.guid.toString()));
					notifyRegionSetRemoved(regionSetServiceInfo);
				}
			}
		}

		@Override
		public void removed(ResultSet rs, int offset, int n) {}

		@Override
		public void propChanging(ResultSet rs, int offset) {}

		@Override
		public void propChanged(ResultSet rs, int offset) {}

		@Override
		public void inserted(ResultSet rs, int offset, int n) {
			for (Object obj  : rs.getList().subList(offset, offset + n)) {
				final ServiceInfo regionSetServiceInfo = (ServiceInfo) obj;
				logger.logp(Level.FINEST, "RegionSetProvider", "inserted", String.format("RegionSet %s detected",
						regionSetServiceInfo.guid.toString()));
				notifyDetectedNewRegionSet(regionSetServiceInfo);
			}
		}
		
	}
	
	public void init() {
		ServiceInfoFilter serviceInfoFilter = new ServiceInfoFilter();
		serviceInfoFilter.filterInterface("org.letras.psi.iregion.IRegionSet");
		serviceInfoFilter.zone = searchZone;
		serviceInfoFilter._op_zone = ServiceInfoFilter.OP_EQUAL;
		logger.logp(Level.FINE, "RegionSetProvider", "init", "Setting up continuous query for IRegionSet");
		try {
			ServiceManager.getInstance().contQuery(serviceInfoFilter, getSession(), new ResultHandler());
		} catch (Exception e) {
			logger.logp(Level.SEVERE, "RegionSetProvider", "init", e.getMessage());
		}
	};
	
	/**
	 * performs all relevant actions for when a new region set is detected
	 * @param serviceInfo the detected region set
	 */
	protected void notifyDetectedNewRegionSet(ServiceInfo serviceInfo) {
		lock.writeLock().lock();
		SingleRegionSetProvider provider = new SingleRegionSetProvider(serviceInfo, searchZone);
		provider.addListener(this);
		regionSetProviders.put(serviceInfo.guid, provider);
		Mundo.registerService(provider);
		lock.writeLock().unlock();
	}
	
	/**
	 * performs all relevant actions for when a known region set is being deleted
	 * @param serviceInfo the deleted region set
	 */
	protected void notifyRegionSetRemoved(ServiceInfo serviceInfo) {
		lock.writeLock().lock();
		SingleRegionSetProvider provider = regionSetProviders.remove(serviceInfo.guid);
		Collection<IRegion> removedRegions = null;
		if (provider != null) {
			removedRegions = provider.getRegions();
			provider.removeListener(this);
			Mundo.unregisterService(provider);
		}
		lock.writeLock().unlock();
		
		if (removedRegions != null) {
			for (IRegion removedRegion: removedRegions) {
				fireRegionRemoved(removedRegion);
			}
		} else {
			logger.logp(Level.WARNING, "ServiceBasedRegionProvider", "notifyRegionRemoved", 
					String.format("Received removed notification for previously unknown region %s",
							serviceInfo.guid));
		}
	}

	@Override
	public Collection<IRegion> getRegions() {
		lock.readLock().lock();
		List<IRegion> result = new ArrayList<IRegion>();
		for (SingleRegionSetProvider provider: regionSetProviders.values()) {
			result.addAll(provider.getRegions());
		}
		lock.readLock().unlock();
		return result;
	}
	
	@Override
	public void regionAdded(IRegion addedRegion, RegionProvider source) {
		fireRegionAdded(addedRegion);
	}

	@Override
	public void regionRemoved(IRegion removedRegion, RegionProvider source) {
		fireRegionRemoved(removedRegion);
	}
}
