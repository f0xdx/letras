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

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.api.region.shape.Bounds;
import org.letras.ps.region.RegionManager;
import org.letras.ps.region.broker.IRegionBroker;
import org.letras.ps.region.broker.IRegionManager;
import org.letras.psi.iregion.IRegion;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.TypedMap;

/**
 * The <code>SimpleRegionBroker</code> is the connection between the incomplete local region model
 * in the {@link RegionManager} and the complete region model which can be distributed 
 * over several nodes participating in the system.<br>
 * When the internal model requests a list of regions at a specific coordinate (see {@link
 * #requestRegionsAtCoordinate(double, double)}) it will at the same time register itself
 * for any changes that happen to the requested regions as well as all enclosed/child regions.
 * <p>
 * This implementation of the RegionBroker interface is mainly for the purpose
 * of delivering a first working implementation of the Region Processing Stage.
 * It is especially bad regarding the performance of finding the appropriate 
 * regions for the coordinates. This implementation also stores all discovered
 * regions locally because of the absence of a observer mechanism for remote 
 * changes.
 * <p>
 * <b>Configuration</b> can be achieved through the IConfigure interface <br/>
 * Possible parameters are:
 * <ul>
 * <li>rap-zone(Optional default="lan"): specifies the zone in which the broker 
 * discovers regions</li>
 * <li>notification-delay(Optional default=0): specifies the amount of time in
 * milliseconds until {@link IRegionManager#addRegion(IRegion)} will be called 
 * if subregions of recently requested regions exist.
 * </li>
 * </ul>
 * <p>
 * This implementation is thread-safe so that discovered regions won't interfere with
 * lookups by the {@link IRegionManager}'s thread.
 * 
 * @author niklas
 * @version 0.0.1
 * 
 *
 */
public class SimpleRegionBroker extends Service implements IRegionBroker, IRegionProviderListener {
	
	//logger

	private static Logger logger = Logger.getLogger("org.letras.ps.region.broker.simple");

	//this is how long the broker waits by default until he notifies the region manager 
	//about children of the requested regions (Can be set in the node.conf.xml).
	private int notificationDelay = 0;
	
	//this is the Mundo zone in which regions will be discovered by default.
	private String searchZone = "lan";
	
	//members 

	private IRegionManager managedModel;
	private List<RegionProvider> regionProviders = new LinkedList<RegionProvider>();
	private LinkedList<IRegion> requestedRegions = new LinkedList<IRegion>();
	
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private Timer delayedNotificationTimer = new Timer();
	
	//methods

	@Override
	public void setServiceConfig(Object cfg) {
		if (cfg instanceof TypedMap) {
			final TypedMap map = (TypedMap) cfg;
			if (map.containsKey("rap-zone")) {
				searchZone = map.getString("rap-zone");
				logger.logp(Level.CONFIG, "SimpleRegionBroker", "setServiceConfig", String.format("region discovery zone set to: %s", searchZone));
			}
			if (map.containsKey("notification-delay")) {
				notificationDelay = map.getInt("notification-delay");
				logger.logp(Level.CONFIG, "SimpleRegionBroker", "setServiceConfig", String.format("notification delay set to: %s", notificationDelay));
			}
		}
		else {
			logger.logp(Level.WARNING, "SimpleRegionBroker", "setServiceConfig", "Configuration object was not of type TypedMap");
		}
	}
	
	/**
	 * Method called by Mundo when service is registered.<br>
	 * The initialization includes setting up a continuous query for remote IRegions.
	 */
	@Override
	public void init() {
		addProvider(new ServiceBasedRegionProvider(searchZone));
		addProvider(new RegionSetProvider(searchZone));
	}
	
	public void addProvider(RegionProvider regionProvider) {
		regionProviders.add(regionProvider);
		regionProvider.addListener(this);
		Mundo.registerService(regionProvider);
	}

	@Override
	public void setRegionManager(IRegionManager regionManager) {
		lock.writeLock().lock();
		
		requestedRegions.clear();
		managedModel = regionManager;
		
		lock.writeLock().unlock();
	}

	@Override
	public List<IRegion> requestRegionsAtCoordinate(double xcoordinate, double ycoordinate) {
		lock.readLock().lock();
		LinkedList<IRegion> result = new LinkedList<IRegion>();
		//we check the coordinates against all detected regions
		for (RegionProvider provider: regionProviders) {
			for (final IRegion region: provider.getRegions()) {
				if (region.shape().contains(xcoordinate, ycoordinate) && !requestedRegions.contains(region)) {
					//do two things: add region to result list and register region as requested region
					result.add(region);
					requestedRegions.add(region);
				}
			}
		}
		lock.readLock().unlock();
		if (result.size() > 0) delayedNotificationTimer.schedule(new DelayedRegionNotification(result), notificationDelay);
		return result;
	}

	/**
	 * This TimerTask implements the delayed notifications that
	 * a broker will receive from the system in future implementations 
	 * @author niklas
	 *
	 */
	private class DelayedRegionNotification extends TimerTask {

		private List<IRegion> regionsOfInterest;
		
		public DelayedRegionNotification(final List<IRegion> regions) {
			this.regionsOfInterest = regions;
		}
		
		@Override
		public void run() {
			lock.writeLock().lock();
			for (IRegion region : regionsOfInterest) {
				final Bounds regionBounds = region.shape().getBounds();
				for (RegionProvider provider: regionProviders) {
					for (IRegion knownRegion : provider.getRegions()) {
						if (!knownRegion.equals(region) && regionBounds.contains(knownRegion.shape().getBounds())) {
							if (!requestedRegions.contains(knownRegion)) {
								requestedRegions.add(knownRegion);
								managedModel.addRegion(knownRegion);
							}
						}
					}
				}
			}
			lock.writeLock().unlock();
		}
		
	}
	
	@Override
	public void shutdown() {
		delayedNotificationTimer.cancel();
		super.shutdown();
	}

	@Override
	public void regionAdded(IRegion addedRegion, RegionProvider source) {
		lock.writeLock().lock();
		// check if the local model should be notified
		final Bounds newRegionBounds = addedRegion.shape().getBounds();
		for (IRegion requestedRegion : requestedRegions) {
			final Bounds requestedRegionBounds = requestedRegion.shape().getBounds();
			if (requestedRegionBounds.contains(newRegionBounds)) {
				managedModel.addRegion(addedRegion);
				requestedRegions.add(addedRegion);
				break;
			}
		}
		lock.writeLock().unlock();
	}

	@Override
	public void regionRemoved(IRegion removedRegion, RegionProvider source) {
		lock.writeLock().lock();
		// check if the local model should be notified
		if (requestedRegions.contains(removedRegion)) {
			logger.logp(Level.FINEST, "SimpleRegionBroker", "regionRemoved", "Leaving region is in local model");
			managedModel.deleteRegion(removedRegion);
			requestedRegions.remove(removedRegion);
		}
		lock.writeLock().unlock();
	}
}
