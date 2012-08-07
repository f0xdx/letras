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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.letras.api.region.RegionData;
import org.letras.psi.iregion.IRegion;
import org.letras.util.region.document.RegionDocument;

public class ComplexModelTest {
	
	@Test
	public void testLeafletRegression1() {
		RegionDocument doc = RegionDocument.fromFile(new File("page2.regions"));
		RegionTreeNode pageNode = new RegionTreeNode(doc.getPage());
		System.out.println("Inserting page " + doc.getPage());
		List<IRegion> addedRegions = new ArrayList<IRegion>();
		addedRegions.add(pageNode.getRegion());
		for (RegionData regionToAdd: doc.getRegions()) {
			if (regionToAdd == doc.getPage())
				continue;
			System.out.println("Inserting " + regionToAdd);
			pageNode.add(new RegionTreeNode(regionToAdd));
			addedRegions.add(regionToAdd);
			assertTrue("Region Tree Inconsistent!", ConsistencyChecker.checkRegionTreeConsistency(pageNode));
			assertSameRegionsContained(addedRegions, pageNode);
		}
	}

	private static void assertSameRegionsContained(List<IRegion> addedRegions,
			RegionTreeNode pageNode) {
		for (IRegion region: addedRegions) 
			if (!contains(pageNode, region))
				fail("Missing from model: " + region);
	}

	private static boolean contains(RegionTreeNode pageNode, IRegion region) {
		if (pageNode.getRegion() == region)
			return true;
		return ((pageNode.getLeftChild() != null && contains(pageNode.getLeftChild(), region))
				|| (pageNode.getRightChild() != null && contains(pageNode.getRightChild(), region)));
	}

}
