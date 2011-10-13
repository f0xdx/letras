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
package org.letras.tools.regionmonitor.regions.model;

import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.letras.ps.region.RegionTreeNode;

public class RegionJTreeModel implements TreeModel {

	private RegionTreeNode toplevelRegion;
	private List<TreeModelListener> listeners = new LinkedList<TreeModelListener>();

	public RegionJTreeModel(RegionTreeNode topLevelRegion) {
		this.toplevelRegion = topLevelRegion;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (index < 0 || index > 1)
			throw new IndexOutOfBoundsException();
		return index == 0 ? ((RegionTreeNode) parent).getLeftChild() : ((RegionTreeNode) parent).getRightChild();
	}

	@Override
	public int getChildCount(Object parent) {
		RegionTreeNode region = (RegionTreeNode) parent;
		if (region.getLeftChild() == null)
			return 0;
		if (region.getRightChild() == null)
			return 1;
		return 2;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		RegionTreeNode region = (RegionTreeNode) parent;
		if (region.getLeftChild() == child)
			return 0;
		if (region.getRightChild() == child)
			return 1;
		return -1;
	}

	@Override
	public Object getRoot() {
		return toplevelRegion;
	}

	@Override
	public boolean isLeaf(Object node) {
		RegionTreeNode region = (RegionTreeNode) node;
		return region.getLeftChild() == null;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		throw new IllegalArgumentException("Not supported!");
	}
	
	public void fireTreeStructureChanged() {
		for (TreeModelListener l: listeners) {
			Object[] path = {toplevelRegion};
			TreeModelEvent e = new TreeModelEvent(this, path);
			l.treeStructureChanged(e);
		}
	}

}
