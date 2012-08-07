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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.api.pen.PenSample;
import org.letras.api.region.shape.RectangularShape;
import org.letras.ps.region.broker.IRegionBroker;
import org.letras.ps.region.broker.IRegionManager;
import org.letras.psi.iregion.IRegion;

/**
 * The {@link RegionManager} owns the node's {@link RegionTreeNode} model.
 * It manages the {@link RegionSampleProcessor}s' and the {@link IRegionBroker}'s concurrent read/write
 * access to the model through a <a href="http://en.wikipedia.org/wiki/Readers-writer_lock">Readers-writer lock</a>.
 * 
 * @author Jannik Jochem
 *
 */
public class RegionManager implements IRegionManager {

	//logger
	private static Logger logger = Logger.getLogger("org.letras.ps.region");
	
	
	private static final double CoordinateSpaceWidth = Integer.MAX_VALUE;
	private static final double CoordinateSpaceHeight = Integer.MAX_VALUE;
	
	protected RegionTreeNode model;
	private IRegionBroker regionBroker;
	protected ReadWriteLock modelLock;
	
	private Map<IRegion, RegionTreeNode> regionToTreeNode;
	private String serviceZone; 
	
	public RegionManager() {
		model = new RegionTreeNode(
				new RectangularShape(0.0, 0.0, CoordinateSpaceWidth, CoordinateSpaceHeight), false);
		// We want a fair lock because there are potentially so many concurrent reads
		// that writers would commonly starve.
		modelLock = new ReentrantReadWriteLock(true);
		regionToTreeNode = new HashMap<IRegion, RegionTreeNode>();
	}
	
	/**
	 * Performs intersection of a sample with the model. Blocks if model updates are pending or
	 * currently executing. The first parameter allows to specify at which Region the intersection
	 * algorithm should be started. This is typically used to implement a simple caching model
	 * by passing in the previously returned {@link IRegion}.
	 * @param lastIntersectingRegionInfo the {@link IRegion} of the {@link RegionTreeNode} where the 
	 * 	intersection algorithm should be started or null to start from the root
	 * @param sample the sample for which to perform the intersection
	 * @return a list of the {@link IRegion}s of all {@link RegionTreeNode}s that intersect the sample, in postorder
	 */
	public List<IRegion> getIntersectingRegionInfos(IRegion lastIntersectingRegionInfo, PenSample sample) {
		modelLock.readLock().lock();

		RegionTreeNode lastIntersectingRegionTreeNode = null;
		if (lastIntersectingRegionInfo == null) {
			lastIntersectingRegionTreeNode = model;
		} else {
			lastIntersectingRegionTreeNode = regionToTreeNode.get(lastIntersectingRegionInfo);
			if (lastIntersectingRegionTreeNode == null) {
				lastIntersectingRegionTreeNode = model;
			}
		}
		RegionTreeNode intersectingRegionTreeNode = lastIntersectingRegionTreeNode.getIntersectingRegion(sample.getX(), sample.getY());

		if (intersectingRegionTreeNode == model) {
			modelLock.readLock().unlock();
			if (retrieveRegionAt(sample.getX(), sample.getY())) {
				return getIntersectingRegionInfos(lastIntersectingRegionInfo, sample);
			} else {
				modelLock.readLock().lock();
			}
		}
		RegionTreeNode currentRegionTreeNode = intersectingRegionTreeNode;
		List<IRegion> intersectedRegions = new LinkedList<IRegion>();
		while (currentRegionTreeNode != null) {
			if (currentRegionTreeNode.getRegion() != null)
				intersectedRegions.add(currentRegionTreeNode.getRegion());
			currentRegionTreeNode = currentRegionTreeNode.getParent();
		}

		modelLock.readLock().unlock();
		return intersectedRegions;

	}

	/**
	 * Retrieves the Region subtree that contains sample (x,y) from the {@link IRegionBroker} and adds it to the model atomically.
	 * Blocks if there are concurrent reads on the model.
	 * 
	 * @param x
	 * @param y
	 * @return true iff a Region was retrieved and added to the model
	 */
	protected boolean retrieveRegionAt(double x, double y) {
		List<IRegion> regionsAtCoordinate = regionBroker.requestRegionsAtCoordinate(x, y);
		if (regionsAtCoordinate.isEmpty()) {
			return false;
		} else {
			modelLock.writeLock().lock();
			for (IRegion region: regionsAtCoordinate) {
				RegionTreeNode newNode = new RegionTreeNode(region);
				model.add(newNode);
				regionToTreeNode.put(region, newNode);
			}
			modelLock.writeLock().unlock();
			return true;
		}
	}

	@Override
	public void addRegion(IRegion regionToAdd) {
		modelLock.writeLock().lock();
		try {
			RegionTreeNode newNode = new RegionTreeNode(regionToAdd);
			model.add(newNode);
			regionToTreeNode.put(regionToAdd, newNode);
		} finally {
			modelLock.writeLock().unlock();
		}
	}

	@Override
	public void deleteRegion(IRegion regionToDelete) {
		modelLock.writeLock().lock();
		try {
			RegionTreeNode node = regionToTreeNode.remove(regionToDelete);
			if (node != null)
				model.remove(node);
			else
				logger.logp(Level.WARNING, "RegionManager", "deleteRegion", 
						"Region should have been in model, but could not be retrieved");
		} finally {
			modelLock.writeLock().unlock();
		}
	}

	@Override
	public void updateRegion(IRegion regionToUpdate) {
		modelLock.writeLock().lock();
		try {
			// the lock is reentrant, so this should work & guarantee atomicity
			deleteRegion(regionToUpdate);
			addRegion(regionToUpdate);
		} finally {
			modelLock.writeLock().unlock();
		}
		
	}

	public void setServiceZone(String serviceZone) {
		this.serviceZone = serviceZone;
	}
	
	public String getServiceZone() {
		return serviceZone;
	}
	
	/**
	 * Accessor to the Region Model in case a subclass wants access.
	 * @return the region model 
	 */
	protected RegionTreeNode getModel() {
		return this.model;
	}

	public void setBroker(IRegionBroker regionBroker) {
		this.regionBroker = regionBroker;
		regionBroker.setRegionManager(this);
	}
}
