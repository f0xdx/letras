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

import org.letras.ps.region.RegionTreeNode;

public class ConsistencyChecker {
	public static boolean checkRegionTreeConsistency(RegionTreeNode root) {
		RegionTreeNode leftChild = root.getLeftChild();
		RegionTreeNode rightChild = root.getRightChild();
		if (leftChild == root) {
			System.err.println("node " + root + " contains itself as left child.");
			return false;
		}
		if (rightChild == root) {
			System.err.println("node " + root + " contains itself as right child.");
			return false;
		}
		if (leftChild != null && rightChild != null) {
			if (leftChild.getShape().getBounds().intersects(rightChild.getShape().getBounds())) {
				System.err.println("Model is inconsistent: leftChild a = " + leftChild + " and rightChild b = " + rightChild + " intersect.");
				if (leftChild.getShape().getBounds().contains(rightChild.getShape().getBounds()))
					System.err.println("b should instead be contained within a. The error seems to be the addInternal() code.");
				else if (rightChild.getShape().getBounds().contains(leftChild.getShape().getBounds()))
					System.err.println("a should instead be contained within b. The error seems to be the addInternal() code.");
				else {
					System.err.println("Neither a nor b is entirely contained in the other. If one of them is virtual, the error is in the addInternal() code. Otherwise, the input is invalid.");
				}
				return false;
			}
		}
		if (leftChild == null && rightChild != null) {
			System.err.println("Model is inconsistent: " + root + "'s children are not pushed to the left.");
		}
		if (leftChild != null) {
			if (!root.getShape().getBounds().contains(leftChild.getShape().getBounds())) {
				System.err.println("Model is inconsistent: leftChild " + leftChild + " not contained in parent " + root + ".");
				return false;
			}
			if (!checkRegionTreeConsistency(leftChild))
				return false;
		}
		if (rightChild != null) {
			if (!root.getShape().getBounds().contains(rightChild.getShape().getBounds())) {
				System.err.println("Model is inconsistent: rightChild " + rightChild + " not contained in parent " + root + ".");
				return false;
			}
			if (!checkRegionTreeConsistency(rightChild))
				return false;
		}
		return true;
	}

}
