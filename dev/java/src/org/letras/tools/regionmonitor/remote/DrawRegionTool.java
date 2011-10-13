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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class DrawRegionTool {
	
	private IToolDependencies dependencies;
	
	private MouseAdapter regionDrawingCanvasListener;
	
	
	public DrawRegionTool(IToolDependencies dependencies) {
		this.dependencies = dependencies;
		createMouseAdapter();
	}
	
	public MouseAdapter getMouseAdapter() {
		return regionDrawingCanvasListener;
	}
	
	private void createMouseAdapter() {
		regionDrawingCanvasListener = new MouseAdapter() {
			Point2D startPoint;
			private Rectangle2D selection;
			 
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					startPoint = e.getPoint();
				}
			};
			
			private void clear(Rectangle2D area) {
				Graphics2D graphics = (Graphics2D) dependencies.getRegionCanvas().getGraphics();
				graphics.setColor(Color.white);
				graphics.fill(new Rectangle2D.Double(area.getX() - 5, area.getY() - 5,
						area.getWidth() + 10, area.getHeight() + 10));
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					Point currentPoint = e.getPoint();
					Graphics2D graphics = (Graphics2D) dependencies.getRegionCanvas().getGraphics();
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
					Point2D.Double endPoint = dependencies.getRegionCanvas().calculateAbsolutePosition(e.getPoint());
					Point2D.Double startPoint = dependencies.getRegionCanvas().calculateAbsolutePosition(this.startPoint);
					clear(selection);
					selection = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), 0, 0);
					selection.add(endPoint);
					makeRegion(selection);
				}
			}

			private void makeRegion(final Rectangle2D selection2) {
				dependencies.getApplicationController().createNewRegion(selection2);
			}
		};
	}


	public JToggleButton getToogleButton() {
		return new JToggleButton("Draw Region", new ImageIcon("ressources/rectangle.png"));
	}
}
