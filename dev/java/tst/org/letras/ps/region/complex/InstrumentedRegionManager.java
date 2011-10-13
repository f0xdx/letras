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
package org.letras.ps.region.complex;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.letras.ps.region.ConsistencyChecker;
import org.letras.ps.region.RegionManager;
import org.letras.ps.region.RegionTreeNode;
import org.letras.psi.iregion.IRegion;

public class InstrumentedRegionManager extends RegionManager {
	
	
	@Override
	public void addRegion(IRegion regionToAdd) {
		super.addRegion(regionToAdd);
		checkModel();
	}
	
	@Override
	public void deleteRegion(IRegion regionToDelete) {
		super.deleteRegion(regionToDelete);
		checkModel();
	}
	
	@Override
	public void updateRegion(IRegion regionToUpdate) {
		super.updateRegion(regionToUpdate);
		checkModel();
	}
	
	public Set<IRegion> getAllRegions() {
		modelLock.readLock().lock();
		Set<IRegion> result = new HashSet<IRegion>();
		Queue<RegionTreeNode> q = new LinkedList<RegionTreeNode>();
		q.add(model);
		while (!q.isEmpty()) {
			RegionTreeNode currentNode = q.remove();
			if (currentNode.getRegion() != null)
				result.add(currentNode.getRegion());
			if (currentNode.getLeftChild() != null)
				q.add(currentNode.getLeftChild());
			if (currentNode.getRightChild() != null)
				q.add(currentNode.getRightChild());
		}
		modelLock.readLock().unlock();
		return result;
	}
	
	@Override
	public boolean retrieveRegionAt(double x, double y) {
		boolean result = super.retrieveRegionAt(x, y);
		checkModel();
		return result;
	}

	private void checkModel() {
		modelLock.readLock().lock();
		if (!ConsistencyChecker.checkRegionTreeConsistency(model)) {
			throw new IllegalArgumentException("Region tree inconsistent!");
		}
		modelLock.readLock().unlock();
	}
}
