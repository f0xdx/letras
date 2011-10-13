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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.shape.Bounds;
import org.letras.psi.iregion.shape.IShape;
import org.letras.psi.iregion.shape.RectangularShape;

/**
 * A Region in the hierarchy.
 * 
 * A Region models a part of the Anoto coordinate space defined by an {@link IShape}. 
 * Regions can contain sub-regions that are contained within the bounds of their 
 * defining {@link IShape}. This way a containment hierarchy of bounding boxes is 
 * formed.
 * 
 * A point intersection test can be performed on any Region in the hierarchy. The test
 * yields the {@link RegionTreeNode} with the lowest height (i.e. that is nearest to a leaf or is a
 * leaf itself) that contains the point. This is required for dispatching pen samples
 * to the appropriate {@link RegionTreeNode}.
 * 
 * For the structure and the geometry of the hierarchy, the following assumptions hold:
 * <ul>
 * <li>Any {@link IShape} contains (geometrically, not structurally) itself (weak containment is employed).</li>
 * <li>Nodes entirely contain any of their children.</li>
 * <li>Nodes have at most two children (the hierarchy is a binary tree).</li>
 * <li>Siblings do not intersect.</li> 
 * <li>The bounding boxes of siblings do not intersect.</li>
 * <li>When adding {@link RegionTreeNode}s to the tree, additional <em>virtual</em> {@link RegionTreeNode}s may be created.</li> 
 * <li>Virtual {@link RegionTreeNode}s may be destroyed any time.
 * <li>When adding subtrees, their structure may not be preserved.
 * </ul>
 * 
 * A Region has a property defined by a generic type parameter. This can be used
 * to store information relevant to performing the actual dispatch without introducing
 * a dependency from the model to the component doing the dispatching.
 * 
 * @author Jannik Jochem
 *
 */
public class RegionTreeNode {
	
	private static final Logger logger = 
		Logger.getLogger("org.letras.region.model");
	
	private IShape shape;
	
	private RegionTreeNode leftChild;
	private RegionTreeNode rightChild;
	
	private RegionTreeNode parent;

	private boolean virtual;

	private IRegion region;
	
	/**
	 * Create a new Region from shape. The region is non-virtual (it cannot be destroyed or resized).
	 * @param region the Region to create the tree node from
	 */
	public RegionTreeNode(IRegion region) {
		this.region = region;
		this.shape = region.shape();
		this.virtual = false;
	}
	
	/**
	 * Creates a new Region from shape. The region is virtual iff virtual is true. This means that it may be destroyed or resized when rebalancing the hierarchy.
	 * @param shape
	 * @param virtual
	 */
	public RegionTreeNode(IShape shape, boolean virtual) {
		this.shape = shape;
		this.virtual = virtual;
	}

	/**
	 * Determines the lowest Region in the tree that (weakly) contains the point (x,y).
	 * If the point is not contained in this region, the request is recursively passed up
	 * to the parent region until a region is reached that contains the point.
	 * 
	 * @param x 
	 * @param y
	 */
	public RegionTreeNode getIntersectingRegion(double x, double y) {
		if (getShape().getBounds().contains(x, y) && getShape().contains(x, y)) {
			return getIntersectingRegionRecursive(x, y);
		} else {
			if (getParent() == null)
				throw new IllegalArgumentException("Intersection point (" + x + "," + y + ") is outside the toplevel region " + this + ".");
			return getParent().getIntersectingRegion(x, y);
		}
	}
	
	/**
	 * Determines the lowest Region in the tree that contains the point (x,y).
	 * Precondition: this Region contains the point (x,y).
	 * @param x
	 * @param y
	 */
	protected RegionTreeNode getIntersectingRegionRecursive(double x, double y) {
		RegionTreeNode result = null;
		if (leftChild != null) {
			result = leftChild.getIntersectingRegionInternal(x, y);
		}
		if (result == null && rightChild != null) {
			result = rightChild.getIntersectingRegionInternal(x, y);
		}
		if (result == null)
			return this;
		else
			return result;
	}

	/**
	 * Determines the lowest Region in the tree that contains the point (x,y) and is a (transitive) child of this Region.
	 * If this Region does not contain the point (x,y), null is returned.
	 * @param x
	 * @param y
	 * @return the lowest Region in the tree that contains the point (x,y) and is a child of this Region, null if the point is not contained in any child of this Region.
	 */
	protected RegionTreeNode getIntersectingRegionInternal(double x, double y) {
		if (getShape().getBounds().contains(x, y) && getShape().contains(x, y)) {
			return getIntersectingRegionRecursive(x, y);
		}
		return null;
	}
	
	/**
	 * Adds a new Region below this Region. If the child's bounds exceed the bounds of this {@link RegionTreeNode}, an IllegalArgumentException is thrown.
	 * 
	 * @param child
	 * @throws IllegalArgumentException if the child's bounds exceed the bounds of this {@link RegionTreeNode}
	 */
	public void add(RegionTreeNode child) {
		if (child == this)
			throw new IllegalArgumentException("Trying to add " + child + " under itself.");
		if (getShape().getBounds().contains(child.getShape().getBounds()))
			addInternal(child);
		else
			throw new IllegalArgumentException("Trying to add child " + child + " under a smaller parent " + this);
	}

	/**
	 * Adds a new Region below this Region. The added child's bounds must be entirely within the bounds of this Region, otherwise
	 * unspecified Behavior may occur.
	 * 
	 * @param child
	 */
	protected void addInternal(RegionTreeNode child) {
		if (isLeaf()) {
			// no children - simplest case
			setLeftChild(child);
		}
		else if (leftChild.getShape().getBounds().contains(child.getShape().getBounds())) {
			// entirely contained in left child - add it there
			leftChild.add(child);
		} else if (child.getShape().getBounds().contains(leftChild.getShape().getBounds())) {
			// new child contains the left child (or both) - swap the new child in as left child
			RegionTreeNode oldLeftChild = leftChild;
			setLeftChild(child);
			leftChild.add(oldLeftChild);
			if (rightChild != null && child.getShape().getBounds().contains(rightChild.getShape().getBounds())) {
				// the right child is also contained in the new child - add it under the new child 
				// in order to maintain geometry constraints of the tree
				child.add(rightChild);
				setRightChild(null);
			}
		} else if (leftChild.isVirtual() && child.getShape().getBounds().strictIntersects(leftChild.getShape().getBounds())) {
			// The leftChild is virtual and intersects the new child without containing it entirely.
			// This means we need to make the leftChild virtual region bigger.
			Bounds newLeftChildBounds = Bounds.union(leftChild.getShape().getBounds(), child.getShape().getBounds());
			boolean addRightChild = false;
			if (rightChild != null && newLeftChildBounds.intersects(rightChild.getShape().getBounds())) {
				newLeftChildBounds = Bounds.union(newLeftChildBounds, rightChild.getShape().getBounds());
				addRightChild = true;
			}
			RegionTreeNode newLeftChild = new RegionTreeNode(new RectangularShape(newLeftChildBounds), true);
			newLeftChild.setLeftChild(leftChild.getLeftChild());
			newLeftChild.setRightChild(leftChild.getRightChild());
			setLeftChild(newLeftChild);
			newLeftChild.add(child);
			if (addRightChild) {
				newLeftChild.add(rightChild);
				setRightChild(null);
			}
		} else if (rightChild == null) {
			// no right child and not contained in left child - add as right child
			setRightChild(child);
		} else if (rightChild.getShape().getBounds().contains(child.getShape().getBounds())) {
			// entirely contained in right child - add it there
			rightChild.add(child);
		} else if (child.getShape().getBounds().contains(rightChild.getShape().getBounds())) {
			// new child contains the right child, but not the left child - swap the new child in as right child
			RegionTreeNode oldRightChild = rightChild;
			setRightChild(child);
			rightChild.add(oldRightChild);
		} else if (rightChild.isVirtual() && child.getShape().getBounds().strictIntersects(rightChild.getShape().getBounds())) {
			// The rightChild is virtual and intersects the new child without containing it entirely.
			// This means we need to make the rightChild virtual region bigger.
			Bounds newRightChildBounds = Bounds.union(rightChild.getShape().getBounds(), child.getShape().getBounds());
			boolean addLeftChild = false;
			if (newRightChildBounds.intersects(rightChild.getShape().getBounds())) {
				newRightChildBounds = Bounds.union(newRightChildBounds, leftChild.getShape().getBounds());
				addLeftChild = true;
			}
			RegionTreeNode newRightChild = new RegionTreeNode(new RectangularShape(newRightChildBounds), true);
			newRightChild.setLeftChild(rightChild.getLeftChild());
			newRightChild.setRightChild(rightChild.getRightChild());
			setRightChild(newRightChild);
			newRightChild.add(child);
			if (addLeftChild) {
				newRightChild.add(leftChild);
				setLeftChild(null);
			} 
			pushLeft();
		} else {
			// the new child is disjoint from the left and right child - create a new virtual region and add
			// the new child and one of the current children under it
			RegionTreeNode oldLeftChild = leftChild;
			Bounds newVirtualBounds = Bounds.union(oldLeftChild.getShape().getBounds(), child.getShape().getBounds());
			if (rightChild.getShape().getBounds().intersects(newVirtualBounds)) {
				// the new virtual region intersects the right child - this means we need to construct 
				// the virtual region from the new child and the right child instead of with the left child
				// in order to maintain consistency of the geometry constraints
				RegionTreeNode oldRightChild = rightChild;
				newVirtualBounds = Bounds.union(oldRightChild.getShape().getBounds(), child.getShape().getBounds());
				if (newVirtualBounds.intersects(leftChild.getShape().getBounds())) {
					// The new virtual region intersects the left child - this means we need to add the old left
					// and right children to a new virtual region and add the new child separately
					newVirtualBounds = Bounds.union(oldLeftChild.getShape().getBounds(), oldRightChild.getShape().getBounds());
					if (newVirtualBounds.intersects(child.getShape().getBounds()))
						throw new IllegalArgumentException("Cannot accomodate the regions into a binary containment hierarchy. This situation really should not arise with the kind of geometry we are using. Please contact the Letras team!");
					RegionTreeNode newVirtualRegion = new RegionTreeNode(new RectangularShape(newVirtualBounds), true);
					setLeftChild(newVirtualRegion);
					leftChild.setLeftChild(oldLeftChild);
					leftChild.setRightChild(oldRightChild);
					setRightChild(child);
				} else {
					RegionTreeNode newVirtualRegion = new RegionTreeNode(new RectangularShape(newVirtualBounds), true);
					setRightChild(newVirtualRegion);
					rightChild.add(child);
					rightChild.add(oldRightChild);
				}
			} else {
				// the new virtual region does not intersect its future sibling - we can create it and add it to the tree
				RegionTreeNode newVirtualRegion = new RegionTreeNode(new RectangularShape(newVirtualBounds), true);
				setLeftChild(newVirtualRegion);
				leftChild.add(child);
				leftChild.add(oldLeftChild);
			}
		}
	}
		
	private void setLeftChild(RegionTreeNode child) {
		leftChild = child;
		if (child != null)
			child.parent = this;
	}
	
	private void setRightChild(RegionTreeNode child) {
		rightChild = child;
		if (child != null)
			child.parent = this;
	}

	/**
	 * Removes a node that is somewhere below this node from the containment hierarchy.
	 * @param child
	 */
	public void remove(RegionTreeNode child) {
		if (leftChild == child) {
			setLeftChild(child.generateSubtree());
			pushLeft();
		} else if (rightChild == child) {
			setRightChild(child.generateSubtree());
		} else if (leftChild != null && leftChild.getShape().getBounds().contains(child.getShape().getBounds())) {
			leftChild.remove(child);
		} else if (rightChild != null && rightChild.getShape().getBounds().contains(child.getShape().getBounds())) {
			rightChild.remove(child);
		} else {
			logger.log(Level.WARNING, "Could not find " + child + " to remove inside " + this + ".");
		}
	}
	
	/**
	 * Forms the entire subtree below this node into a new subtree, i.e. merges leftChild
	 * and rightChild into a new subtree. This is needed when this node is removed.
	 * @return a node that contains the entire subtree below this node, null iff {@link #isLeaf()} returns true.
	 */
	protected RegionTreeNode generateSubtree() {
		if (rightChild == null) {
			return leftChild;
		} else {
			Bounds newSubtreeRootBounds = Bounds.union(leftChild.getShape().getBounds(), rightChild.getShape().getBounds());
			RegionTreeNode newSubtreeRoot = new RegionTreeNode(new RectangularShape(newSubtreeRootBounds), true);
			newSubtreeRoot.setLeftChild(leftChild);
			newSubtreeRoot.setRightChild(rightChild);
			return newSubtreeRoot;
		}
	}
	
	/**
	 * @return true iff this node is a leaf node
	 */
	public boolean isLeaf() {
		return leftChild == null;
	}
	
	/**
	 * @return true iff this is node is a virtual node
	 */
	public boolean isVirtual() {
		return virtual;
	}
	
	/**
	 * Ensures that the children of this node are left-aligned.
	 */
	protected void pushLeft() {
		if (leftChild == null && rightChild != null) {
			setLeftChild(rightChild);
			setRightChild(null);
		}
	}

	/**
	 * @return the {@link IShape} that defines this {@link RegionTreeNode}
	 */
	public IShape getShape() {
		return shape;
	}
	
	/**
	 * @return the {@link RegionTreeNode} this {@link RegionTreeNode} is contained in
	 */
	public RegionTreeNode getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return "region(" + shape.toString() + ", " + virtual + ")";
	}

	public RegionTreeNode getLeftChild() {
		return leftChild;
	}
	
	public RegionTreeNode getRightChild() {
		return rightChild;
	}

	/**
	 * @return the {@link IRegion} for this node. May be null.
	 */
	public IRegion getRegion() {
		return region;
	}
}
