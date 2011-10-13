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
package org.letras.region.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.letras.ps.region.RegionTreeNode;
import org.letras.psi.iregion.shape.RectangularShape;

public class RegionModelTest {
	
	@Test
	public void testSimpleHierarchy() {
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		RegionTreeNode bigRegion = new RegionTreeNode(new RectangularShape(20.0, 20.0, 60.0, 60.0), false);
		RegionTreeNode smallRegion1 = new RegionTreeNode(new RectangularShape(30.0, 30.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion2 = new RegionTreeNode(new RectangularShape(60.0, 60.0, 10.0, 10.0), false);
		
		toplevelRegion.add(bigRegion);
		toplevelRegion.add(smallRegion1);
		toplevelRegion.add(smallRegion2);
		
		assertEquals(toplevelRegion, toplevelRegion.getIntersectingRegion(10, 10));
		assertEquals(toplevelRegion, smallRegion1.getIntersectingRegion(10, 10));
		assertEquals(toplevelRegion, smallRegion2.getIntersectingRegion(10, 10));
		assertEquals(smallRegion1, toplevelRegion.getIntersectingRegion(35, 35));
		assertEquals(smallRegion1, bigRegion.getIntersectingRegion(35, 35));
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
	}
	
	@Test
	public void testWeakIntersectionProperty() {
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		RegionTreeNode bigRegion = new RegionTreeNode(new RectangularShape(20.0, 20.0, 60.0, 60.0), false);
		RegionTreeNode smallRegion1 = new RegionTreeNode(new RectangularShape(30.0, 30.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion2 = new RegionTreeNode(new RectangularShape(60.0, 60.0, 10.0, 10.0), false);
		
		toplevelRegion.add(bigRegion);
		toplevelRegion.add(smallRegion1);
		toplevelRegion.add(smallRegion2);
		
		assertEquals(toplevelRegion, toplevelRegion.getIntersectingRegion(0, 0));
		assertEquals(toplevelRegion, toplevelRegion.getIntersectingRegion(100, 100));
		assertEquals(bigRegion, smallRegion2.getIntersectingRegion(30, 20));
		assertEquals(bigRegion, smallRegion2.getIntersectingRegion(20, 20));
		assertEquals(bigRegion, smallRegion2.getIntersectingRegion(80, 20));
		assertEquals(bigRegion, smallRegion2.getIntersectingRegion(80, 80));
		assertEquals(smallRegion1, toplevelRegion.getIntersectingRegion(30, 30));
		assertEquals(smallRegion2, bigRegion.getIntersectingRegion(70, 70));
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
	}
	
	@Test
	public void testSimpleHierarchyReordering() {
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		RegionTreeNode bigRegion = new RegionTreeNode(new RectangularShape(20.0, 20.0, 60.0, 60.0), false);
		RegionTreeNode smallRegion1 = new RegionTreeNode(new RectangularShape(30.0, 30.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion2 = new RegionTreeNode(new RectangularShape(60.0, 60.0, 10.0, 10.0), false);
		
		toplevelRegion.add(smallRegion1);
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
		toplevelRegion.add(smallRegion2);
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
		toplevelRegion.add(bigRegion);
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
		
		assertEquals(toplevelRegion, toplevelRegion.getIntersectingRegion(10, 10));
		assertEquals(toplevelRegion, smallRegion1.getIntersectingRegion(10, 10));
		assertEquals(toplevelRegion, smallRegion2.getIntersectingRegion(10, 10));
		assertEquals(smallRegion1, toplevelRegion.getIntersectingRegion(35, 35));
		assertEquals(smallRegion1, bigRegion.getIntersectingRegion(35, 35));
	}
	
	@Test
	public void testRegionHierarchyWithVirtualRegions() {
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		RegionTreeNode smallRegion1 = new RegionTreeNode(new RectangularShape(30.0, 30.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion2 = new RegionTreeNode(new RectangularShape(60.0, 60.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion3 = new RegionTreeNode(new RectangularShape(45.0, 45.0, 10.0, 10.0), false);
		
		toplevelRegion.add(smallRegion1);
		toplevelRegion.add(smallRegion2);
		toplevelRegion.add(smallRegion3);
		
		assertEquals(toplevelRegion, toplevelRegion.getIntersectingRegion(10, 10));
		assertEquals(toplevelRegion, smallRegion1.getIntersectingRegion(10, 10));
		assertEquals(toplevelRegion, smallRegion2.getIntersectingRegion(10, 10));
		assertEquals(toplevelRegion, smallRegion3.getIntersectingRegion(10, 10));
		assertEquals(smallRegion1, toplevelRegion.getIntersectingRegion(35, 35));
		assertEquals(smallRegion2, toplevelRegion.getIntersectingRegion(65, 65));
		assertEquals(smallRegion3, toplevelRegion.getIntersectingRegion(50, 50));
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
	}
	
	@Test
	public void testRemove() {
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		RegionTreeNode bigRegion = new RegionTreeNode(new RectangularShape(20.0, 20.0, 60.0, 60.0), false);
		RegionTreeNode smallRegion1 = new RegionTreeNode(new RectangularShape(30.0, 30.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion2 = new RegionTreeNode(new RectangularShape(60.0, 60.0, 10.0, 10.0), false);
		
		toplevelRegion.add(bigRegion);
		toplevelRegion.add(smallRegion1);
		toplevelRegion.add(smallRegion2);
		
		assertEquals(toplevelRegion, toplevelRegion.getIntersectingRegion(10, 10));
		assertEquals(toplevelRegion, smallRegion1.getIntersectingRegion(10, 10));
		assertEquals(toplevelRegion, smallRegion2.getIntersectingRegion(10, 10));
		assertEquals(smallRegion1, toplevelRegion.getIntersectingRegion(35, 35));
		assertEquals(smallRegion2, toplevelRegion.getIntersectingRegion(65, 65));
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
		
		toplevelRegion.remove(smallRegion2);
		
		assertEquals(bigRegion, toplevelRegion.getIntersectingRegion(65, 65));
		assertEquals(bigRegion, bigRegion.getIntersectingRegion(65, 65));
		assertEquals(bigRegion, smallRegion1.getIntersectingRegion(65, 65));
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddConstraints() {
		RegionTreeNode bigRegion = new RegionTreeNode(new RectangularShape(20.0, 20.0, 60.0, 60.0), false);
		RegionTreeNode smallRegion = new RegionTreeNode(new RectangularShape(30.0, 30.0, 10.0, 10.0), false);
		
		smallRegion.add(bigRegion);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntersectionOutside() {
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		toplevelRegion.getIntersectingRegion(101, 101);
	}
	
	@Test
	public void regressionTestIntersectingVirtualProblem() {
		// regression test 1 for #44
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		RegionTreeNode smallRegion1 = new RegionTreeNode(new RectangularShape(10.0, 10.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion2 = new RegionTreeNode(new RectangularShape(50.0, 10.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion3 = new RegionTreeNode(new RectangularShape(50.0, 50.0, 10.0, 10.0), false);
		
		toplevelRegion.add(smallRegion1);
		toplevelRegion.add(smallRegion2);
		toplevelRegion.add(smallRegion3);
		
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
	}
	
	@Test
	public void regressionTestIntersectingVirtualProblemInverseGeometry() {
		// regression test 2 for #44
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		RegionTreeNode smallRegion1 = new RegionTreeNode(new RectangularShape(10.0, 10.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion2 = new RegionTreeNode(new RectangularShape(50.0, 10.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion3 = new RegionTreeNode(new RectangularShape(10.0, 50.0, 10.0, 10.0), false);
		
		toplevelRegion.add(smallRegion1);
		toplevelRegion.add(smallRegion2);
		toplevelRegion.add(smallRegion3);
		
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
	}
	
	@Test
	public void regressionTestRemoveOperationPrunesSubtreeProblem() {
		// regression test for #55
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		RegionTreeNode smallRegion1 = new RegionTreeNode(new RectangularShape(10.0, 10.0, 80.0, 80.0), false);
		RegionTreeNode smallRegion2 = new RegionTreeNode(new RectangularShape(30.0, 30.0, 20.0, 20.0), false);
		
		toplevelRegion.add(smallRegion1);
		smallRegion1.add(smallRegion2);
		assertTrue(contains(toplevelRegion, smallRegion2));
		toplevelRegion.remove(smallRegion1);
		assertTrue(contains(toplevelRegion, smallRegion2));
		assertFalse(contains(toplevelRegion, smallRegion1));
	}
	
	public static boolean contains(RegionTreeNode root, RegionTreeNode child) {
		if (root == null)
			return false;
		if (root == child)
			return true;
		return contains(root.getLeftChild(), child) || contains(root.getRightChild(), child);
	}
	
	@Test
	public void regressionTestVirtualIntersectingNewChildProblem() {
		// FIXME no ticket yet - TRAC is down
		RegionTreeNode toplevelRegion = new RegionTreeNode(new RectangularShape(0.0, 0.0, 100.0, 100.0), false);
		RegionTreeNode virtualRegion = new RegionTreeNode(new RectangularShape(10.0, 10.0, 40.0, 40.0), true);
		RegionTreeNode smallRegion1 = new RegionTreeNode(new RectangularShape(10.0, 10.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion2 = new RegionTreeNode(new RectangularShape(30.0, 10.0, 10.0, 10.0), false);
		RegionTreeNode smallRegion3 = new RegionTreeNode(new RectangularShape(20.0, 35.0, 10.0, 10.0), false);

		toplevelRegion.add(virtualRegion);
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
		
		toplevelRegion.add(smallRegion1);
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
		
		toplevelRegion.add(smallRegion2);
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
		
		toplevelRegion.add(smallRegion3);
		assertTrue(ConsistencyChecker.checkRegionTreeConsistency(toplevelRegion));
		
	}
	

}
