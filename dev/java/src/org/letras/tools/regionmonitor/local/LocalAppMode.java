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
package org.letras.tools.regionmonitor.local;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;

import org.letras.ps.region.RegionTreeNode;
import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.shape.IShape;
import org.letras.psi.iregion.shape.RectangularShape;
import org.letras.tools.regionmonitor.ApplicationMode;
import org.letras.tools.regionmonitor.regions.model.RegionJTreeModel;


public class LocalAppMode extends ApplicationMode {

	@Override
	public String getNameForMenuBar() {
		return "switch to local mode";
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
	
	
	
	
	protected List<? extends JComponent> getTools() {
		LinkedList<JToggleButton> tools = new LinkedList<JToggleButton>();
		
		JToggleButton drawRectangle = new JToggleButton("Draw Region", new ImageIcon("ressources/rectangle.png"));
		drawRectangle.addActionListener(new ActionListener() {
			MouseAdapter regionDrawingCanvasListener = new MouseAdapter() {
				Point2D startPoint;
				private Rectangle2D selection;
				 
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						startPoint = e.getPoint();
					}
				};
				
				private void clear(Rectangle2D area) {
					Graphics2D graphics = (Graphics2D) canvas.getGraphics();
					graphics.setColor(Color.white);
					graphics.fill(new Rectangle2D.Double(area.getX() - 5, area.getY() - 5,
							area.getWidth() + 10, area.getHeight() + 10));
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						Point currentPoint = e.getPoint();
						Graphics2D graphics = (Graphics2D) canvas.getGraphics();
						if (selection != null) {
							clear(selection);
						}
						graphics.setColor(Color.black);
						selection = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), 0, 0);
						selection.add(currentPoint);
						graphics.draw(selection);
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						Point2D.Double endPoint = canvas.calculateAbsolutePosition(e.getPoint());
						Point2D.Double startPoint = canvas.calculateAbsolutePosition(this.startPoint);
						selection = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), 0, 0);
						selection.add(endPoint);
						clear(selection);
						makeRegion(selection);
					}
				}

				private void makeRegion(final Rectangle2D selection2) {
					try {
					((RegionTreeNode) regionTreeViewer.getModel().getRoot()).add(new RegionTreeNode(new IRegion() {
						IShape shape = new RectangularShape(selection2.getX(), selection2.getY(), selection2.getWidth(), selection2.getHeight());
						@Override
						public IShape shape() {
							return shape;
						}
						
						@Override
						public boolean hungry() {
							// TODO Auto-generated method stub
							return false;
						}
						
						@Override
						public String channel() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String uri() {
							// TODO Auto-generated method stub
							return null;
						}
					}));
					} catch (IllegalArgumentException iae) {
						JOptionPane.showMessageDialog(getPanel(), "Don't draw outside the Anoto coordinate space", "Can't create region", JOptionPane.ERROR_MESSAGE);
					}
					((RegionJTreeModel) regionTreeViewer.getModel()).fireTreeStructureChanged();
				}
			};
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JToggleButton) e.getSource()).isSelected()) {
					this.activate();
				} else {
					this.deactivate();
				}
				
			}
			
			private void deactivate() {
				canvas.removeMouseListener(regionDrawingCanvasListener);
				canvas.removeMouseMotionListener(regionDrawingCanvasListener);
			}

			private void activate() {
				canvas.addMouseListener(regionDrawingCanvasListener);
				canvas.addMouseMotionListener(regionDrawingCanvasListener);
			}
		});
		
		tools.add(drawRectangle);
		return tools;

		
	}

	@Override
	protected RegionTreeNode getTopLevelRegionTreeNode() {
		return new RegionTreeNode(new RectangularShape(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE), false);
	}

}
