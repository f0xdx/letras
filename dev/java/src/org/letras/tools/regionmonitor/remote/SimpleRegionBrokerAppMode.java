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
package org.letras.tools.regionmonitor.remote;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.tree.TreePath;

import org.letras.ps.region.RegionTreeNode;
import org.letras.tools.regionmonitor.ApplicationMode;
import org.letras.tools.regionmonitor.ink.RegionInkInspector;
import org.letras.tools.regionmonitor.regions.model.RegionJTreeModel;
import org.letras.tools.regionmonitor.regions.view.RegionCanvas;

public class SimpleRegionBrokerAppMode extends ApplicationMode {

	private ApplicationController controller;
	
	private MouseAdapter currentActiveToolsMouseAdapter;
	
	@Override
	public String getNameForMenuBar() {
		return "use SimpleRegionBroker";
	}

	@Override
	protected List<? extends JComponent> getTools() {
		LinkedList<JComponent> tools = new LinkedList<JComponent>(); 
		ButtonGroup group = new ButtonGroup();
		final DrawRegionTool drawRegionTool = new DrawRegionTool(new IToolDependencies() {
			@Override
			public RegionCanvas getRegionCanvas() {
				return canvas;
			}
			
			@Override
			public ApplicationController getApplicationController() {
				return controller;
			}
		});
		JToggleButton drawRegionButton = drawRegionTool.getToogleButton();
		drawRegionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentActiveToolsMouseAdapter != null) {
					canvas.removeMouseListener(currentActiveToolsMouseAdapter);
					canvas.removeMouseMotionListener(currentActiveToolsMouseAdapter);
				}
				canvas.addMouseListener(drawRegionTool.getMouseAdapter());
				canvas.addMouseMotionListener(drawRegionTool.getMouseAdapter());
				currentActiveToolsMouseAdapter = drawRegionTool.getMouseAdapter();
			}
		});
		tools.add(drawRegionButton);
		group.add(drawRegionButton);
		
		
		JToggleButton discoverRegion = new JToggleButton("Discover Region", new ImageIcon("ressources/rectangle.png"));
		discoverRegion.addActionListener(new ActionListener() {
			MouseAdapter regionDiscoveryListener = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					controller.discoverRegionsAt(canvas.calculateAbsolutePosition(e.getPoint()));
				};
			};
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentActiveToolsMouseAdapter != null) {
					canvas.removeMouseListener(currentActiveToolsMouseAdapter);
					canvas.removeMouseMotionListener(currentActiveToolsMouseAdapter);
				}
				canvas.addMouseListener(regionDiscoveryListener);
				canvas.addMouseMotionListener(regionDiscoveryListener);
				currentActiveToolsMouseAdapter = regionDiscoveryListener;
			}
		});
		

		tools.add(discoverRegion);
		group.add(discoverRegion);
		
		JToggleButton drawInk = new JToggleButton("Draw Ink");
		drawInk.addActionListener(new ActionListener() {
			MouseAdapter inkDrawListener = new MouseAdapter() {
				public void mousePressed(MouseEvent e) {controller.penDown();};
				public void mouseDragged(MouseEvent e) {controller.penAtPosition(canvas.calculateAbsolutePosition(e.getPoint()));};
				public void mouseReleased(MouseEvent e) {controller.penUp();};
			};
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentActiveToolsMouseAdapter != null) {
					canvas.removeMouseListener(currentActiveToolsMouseAdapter);
					canvas.removeMouseMotionListener(currentActiveToolsMouseAdapter);
				}
				canvas.addMouseListener(inkDrawListener);
				canvas.addMouseMotionListener(inkDrawListener);
				currentActiveToolsMouseAdapter = inkDrawListener;
			}
		});
		

		tools.add(drawInk);
		group.add(drawInk);
		
		
		
		return tools;
	}
	
	@Override
	public void activate() {
		controller = new ApplicationController(this);
		controller.activate();
		super.activate();

		regionTreeViewer.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int selRow = regionTreeViewer.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = regionTreeViewer.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1) {
					if(e.getClickCount() == 2) {
						final RegionTreeNode selNode = (RegionTreeNode) selPath.getLastPathComponent();
						if (selNode.getRegion() != null)
							new RegionInkInspector(selNode).setVisible(true);
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}
			
			
		});

	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		controller.deactivate();
		controller = null;
	}

	@Override
	protected RegionTreeNode getTopLevelRegionTreeNode() {
		return controller.getTopLevelRegionTreeNode();
	}
	
	void updateView() {
		((RegionJTreeModel) this.regionTreeViewer.getModel()).fireTreeStructureChanged();
	}
	
	@Override
	public JMenu getMenuBarItem() {
		JMenu optionsMenu = new JMenu("Options");
		
		JRadioButtonMenuItem virtualNodeVisibility = 
			new JRadioButtonMenuItem(new AbstractAction("show virtual nodes") {
			
			private static final long serialVersionUID = 87768176345152634L;

			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.setVirtualRegionsVisible(((JRadioButtonMenuItem) e.getSource()).isSelected());
				canvas.repaint();
			}
		});
		
		virtualNodeVisibility.setSelected(true);
		
		optionsMenu.add(virtualNodeVisibility);
		
		return optionsMenu;
		
	}

}
