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
package org.letras.tools.designer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.letras.ps.region.RegionTreeNode;
import org.letras.tools.regionmonitor.regions.view.RegionCanvas;

public class DesignerRegionCanvas extends RegionCanvas implements ItemSelectable {
	private static final long serialVersionUID = 6987908282552171738L;
	private static final float HANDLE_SIZE = 6;
	private RegionTreeNode selectedItem = null;
	private List<ItemListener> listeners = new LinkedList<ItemListener>();
	
	public DesignerRegionCanvas() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point2D.Double p = calculateAbsolutePosition(e.getPoint());
				if (toplevelRegion.getShape().contains(p.getX(), p.getY())) {
					setSelectedItem(toplevelRegion.getIntersectingRegion(p.getX(), p.getY()));
				} else {
					setSelectedItem(null);
				}
			}
		});
	}
	
	public void setSelectedItem(RegionTreeNode newSelectedItem) {
		this.selectedItem = newSelectedItem;
		repaint();
		fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, newSelectedItem, (newSelectedItem == null) ? ItemEvent.DESELECTED : ItemEvent.SELECTED));
	}
	
	public RegionTreeNode getSelectedItem() {
		return selectedItem;
	}
	
	@Override
	protected void drawNode(RegionTreeNode regionNode,
			Graphics2D transformedGraphics, float strokeWidth) {
		double width = regionNode.getShape().getBounds().getWidth();
		double height = regionNode.getShape().getBounds().getHeight();
		Rectangle2D.Double shape = new Rectangle2D.Double(0, 0, width,
				height);
		if (selectedItem == regionNode) {
			transformedGraphics.setColor(Color.darkGray);
			transformedGraphics.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, 
					BasicStroke.JOIN_MITER, 10.0f, new float[] {2*strokeWidth, 1*strokeWidth}, 0.0f));transformedGraphics.draw(shape);
			double handleSize = strokeWidth * HANDLE_SIZE;
			for (int i=0; i < 3; i++) {
				for (int j=0; j < 3; j++) {
					if (i == 1 && j == 1)
						continue;
					Rectangle2D.Double rect = new Rectangle2D.Double(i * width / 2 - handleSize/2,
							j * height / 2 - handleSize/2, handleSize, handleSize);
					transformedGraphics.setColor(Color.black);
					transformedGraphics.setStroke(new BasicStroke(strokeWidth));
					transformedGraphics.draw(rect);
					transformedGraphics.fill(rect);
				}
			}
		} else {
			transformedGraphics.setColor(getColor(regionNode));
			transformedGraphics.setStroke(new BasicStroke(strokeWidth));
			
			transformedGraphics.draw(shape);
		}
	}
	
	@Override
	protected Color getColor(RegionTreeNode node) {
		return Color.black;
	}

	@Override
	public void addItemListener(ItemListener l) {
		listeners.add(l);
	}

	@Override
	public Object[] getSelectedObjects() {
		if (selectedItem == null)
			return null;
		else
			return new Object[] { selectedItem };
	}
	
	@Override
	public void removeItemListener(ItemListener l) {
		listeners.remove(l);
	}
	
	protected void fireItemStateChanged(ItemEvent e) {
		for (ItemListener l: listeners) {
			l.itemStateChanged(e);
		}
	}
	
}
