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
package org.letras.tools.regionmonitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.letras.ps.region.RegionTreeNode;
import org.letras.psi.iregion.shape.RectangularShape;
import org.letras.tools.regionmonitor.regions.control.IViewportPositionDelegate;
import org.letras.tools.regionmonitor.regions.control.ViewportPositionControllerBar;
import org.letras.tools.regionmonitor.regions.model.RegionJTreeModel;
import org.letras.tools.regionmonitor.regions.view.RegionCanvas;

/**
 * Application Mode is an abstraction from the modes in which the RegionMonitor can operate in.
 * The responsibility for the abstract class is in initializing the set of common panels. In edition
 * there are hooks for the concrete subclasses to implement mode specific tools for a tool panel.
 * 
 * @author niklas
 *
 */
public abstract class ApplicationMode implements IApplicationMode {

	protected RegionCanvas canvas;
	JPanel mainPanel;
	private JPanel sidePanel;
	protected JTree regionTreeViewer;

	/* (non-Javadoc)
	 * @see org.letras.tools.regionmonitor.IApplicationMode#getNameForMenuBar()
	 */
	public abstract String getNameForMenuBar();
	protected abstract List<? extends JComponent> getTools();

	/* (non-Javadoc)
	 * @see org.letras.tools.regionmonitor.IApplicationMode#activate()
	 */
	public void activate() {
		regionTreeViewer.setModel(new RegionJTreeModel(getTopLevelRegionTreeNode()));
		regionTreeViewer.getModel().addTreeModelListener(new TreeModelListener() {
			
			@Override
			public void treeStructureChanged(TreeModelEvent e) {
				canvas.repaint();
			}
			
			@Override
			public void treeNodesRemoved(TreeModelEvent e) {}
			
			@Override
			public void treeNodesInserted(TreeModelEvent e) {}
			
			@Override
			public void treeNodesChanged(TreeModelEvent e) {}
		});
		canvas.setToplevelRegion((RegionTreeNode) regionTreeViewer.getModel().getRoot());
	}

	/* (non-Javadoc)
	 * @see org.letras.tools.regionmonitor.IApplicationMode#deactivate()
	 */
	public void deactivate() {
		regionTreeViewer.setModel(null);
	}

	public JComponent getPanel() {
		return mainPanel;
	}

	public ApplicationMode() {
		initializeMainPanel();
		initializeCanvas();
		initializeSidebar();
		initializePositionBar();
	}

	
	private void initializeSidebar() {

		sidePanel = new JPanel();
		sidePanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		sidePanel.setLayout(new BorderLayout());
		
		regionTreeViewer = new JTree(new RegionJTreeModel(new RegionTreeNode(new RectangularShape(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE), false)));
//		regionTreeViewer = new JTree(new RegionJTreeModel(null));
		
		regionTreeViewer.setBorder(new BevelBorder(BevelBorder.LOWERED));
		regionTreeViewer.setCellRenderer(new TreeCellRenderer() {

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				final JLabel nodeLabel = new JLabel(String.valueOf(((RegionTreeNode) value).hashCode()));
				if (selected) {
					nodeLabel.setBackground(Color.decode("#b5defc"));
					nodeLabel.setOpaque(true);
				}
				return nodeLabel;
			}
		});
		
		regionTreeViewer.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		regionTreeViewer.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getNewLeadSelectionPath();
				if (path != null)
					canvas.setToplevelRegion((RegionTreeNode) path.getLastPathComponent());
			}
		});

		sidePanel.add(regionTreeViewer, BorderLayout.CENTER);
		JPanel toolBar = new JPanel(new GridLayout(0, 1));
		for (JComponent comp : getTools()) {
			toolBar.add(comp,BorderLayout.PAGE_START);
		}
		sidePanel.add(toolBar, BorderLayout.PAGE_START);
		mainPanel.add(sidePanel, BorderLayout.EAST);
	}

	private void initializeMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
	}

	private void initializeCanvas() {
		canvas = new RegionCanvas();
		mainPanel.add(canvas, BorderLayout.CENTER);
	}
	
	private void initializePositionBar() {
		ViewportPositionControllerBar statusBar = new ViewportPositionControllerBar();
		statusBar.setPositionChangeListener(new IViewportPositionDelegate() {

			@Override
			public void positionDidChange(double x, double y, int ratio) {
				canvas.reposition(x, y, ratio);
			}

			@Override
			public Double calculateAbsoluteCoordinatesFor(Point2D canvasPoint) {
				return canvas.calculateAbsolutePosition(canvasPoint);
			}

			@Override
			public int getCurrentAnotoCoordinatesPerPixel() {
				return canvas.getPixelRatio();
			}
			
		});
		canvas.addMouseMotionListener((MouseMotionListener) statusBar);
		mainPanel.add(statusBar, BorderLayout.SOUTH);
	}
	
	/* (non-Javadoc)
	 * @see org.letras.tools.regionmonitor.IApplicationMode#getMenuBarItem()
	 */
	public JMenu getMenuBarItem() {
		return null;
	}
	
	protected abstract RegionTreeNode getTopLevelRegionTreeNode();

}
