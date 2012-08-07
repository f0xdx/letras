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
package org.letras.tools.regionmonitor.regions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.letras.api.region.shape.Bounds;
import org.letras.ps.region.RegionTreeNode;

public class RegionCanvas extends JPanel {
	private static final long serialVersionUID = 7756632067656219738L;
	protected RegionTreeNode toplevelRegion;
	private AffineTransform viewTransform;
	private AffineTransform inverseViewTransform;
	private boolean drawVirtualRegions;
	private double scale;

	public RegionCanvas() {
		setBackground(Color.white);
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		//set default Transform
		viewTransform = new AffineTransform();
		inverseViewTransform = new AffineTransform();
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				if (toplevelRegion != null && scale==0) recalculateViewTransform();
			}
		});

		drawVirtualRegions = true;

		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				final AffineTransform oldViewTransform = viewTransform;
				AffineTransform newViewTransform = AffineTransform.getTranslateInstance(e.getPoint().getX(), e.getPoint().getY());
				inverseViewTransform.concatenate(AffineTransform.getTranslateInstance(e.getPoint().getX(), e.getPoint().getY()));
				if (e.getWheelRotation() > 0) {
					newViewTransform.concatenate(AffineTransform.getScaleInstance(1.0/2, 1.0/2));
					inverseViewTransform.concatenate(AffineTransform.getScaleInstance(2, 2));
					scale/=2;
				} else {	
					newViewTransform.concatenate(AffineTransform.getScaleInstance(2.0, 2.0));
					inverseViewTransform.concatenate(AffineTransform.getScaleInstance(1.0/2, 1.0/2));
					scale*=2;
				}
				newViewTransform.concatenate(AffineTransform.getTranslateInstance(-e.getPoint().getX(), -e.getPoint().getY()));
				inverseViewTransform.concatenate(AffineTransform.getTranslateInstance(-e.getPoint().getX(), -e.getPoint().getY()));
				newViewTransform.concatenate(oldViewTransform);
				setViewTransform(newViewTransform);
				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionListener() {
			Point lastPoint;

			@Override
			public void mouseMoved(MouseEvent e) {
				lastPoint = null;
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (lastPoint != null) {
						Point newPoint = e.getPoint();
						AffineTransform oldViewTransform = viewTransform;
						AffineTransform newViewTransform = AffineTransform.getTranslateInstance(newPoint.x-lastPoint.x, newPoint.y-lastPoint.y);
						newViewTransform.concatenate(oldViewTransform);
						setViewTransform(newViewTransform);
						inverseViewTransform.concatenate(AffineTransform.getTranslateInstance(-(newPoint.x-lastPoint.x), -(newPoint.y-lastPoint.y)	));
					}
					lastPoint = e.getPoint();
					repaint();
				} else {
					lastPoint = null;
				}
			}
		});
	}
	
	protected void setViewTransform(AffineTransform newViewTransform) {
		this.viewTransform = newViewTransform;
	}
	
	protected AffineTransform getViewTransform() {
		return viewTransform;
	}

	public void setToplevelRegion(RegionTreeNode toplevelRegion) {
		this.toplevelRegion = toplevelRegion;
		recalculateViewTransform();
		repaint();
	}

	public Point2D.Double calculateAbsolutePosition(Point2D click) {
		final Point2D.Double point = new Point2D.Double();
		inverseViewTransform.transform(click, point);
		return point;
	}

	protected void recalculateViewTransform() {
		double xorigin = toplevelRegion.getShape().getBounds().getX();
		double yorigin = toplevelRegion.getShape().getBounds().getY();

		double width = getWidth();
		double height = getHeight();

		double regionWidth = toplevelRegion.getShape().getBounds().getWidth();
		double regionHeight = toplevelRegion.getShape().getBounds().getHeight();

		double scale = Math.min(width / regionWidth, height / regionHeight);

		recalculateViewTransform(xorigin, yorigin, scale);

	}

	private void recalculateViewTransform(double xorigin, double yorigin,
			double scale) {
		if (scale == 0.0) return;

		this.scale = scale;
		AffineTransform newViewTransform = AffineTransform.getScaleInstance(scale, scale);
		newViewTransform.concatenate(AffineTransform.getTranslateInstance(-xorigin, -yorigin));
		setViewTransform(newViewTransform);
		inverseViewTransform = AffineTransform.getTranslateInstance(xorigin, yorigin);
		inverseViewTransform.concatenate(AffineTransform.getScaleInstance(1/scale, 1/scale));
		this.repaint();
	}


//	public AffineTransform getInverseViewTransform() {
//		return inverseViewTransform;
//	}

//	public RegionTreeNode getToplevelRegion() {
//		return toplevelRegion;
//	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (toplevelRegion != null) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.transform(viewTransform);
			float strokeWidth = (float) (1/scale);
			if (Float.isInfinite(strokeWidth))
				strokeWidth = Float.MAX_VALUE;
			paint(toplevelRegion, g2d, strokeWidth);
		}
	}
	
	protected Color getColor(RegionTreeNode node) {
		if (node.getRegion() == null) {
			return Color.blue;
		} else {
			return Color.red;
		}
	}

	/**
	 * Draw the actual shape of regionNode. transformedGraphics is already translated so that the region
	 * is located at the origin, however, it is scaled to public coordinate space, not to normalized space.
	 * @param regionNode
	 * @param transformedGraphics
	 */
	protected void drawNode(RegionTreeNode regionNode,
			Graphics2D transformedGraphics, float strokeWidth) {
		transformedGraphics.setColor(getColor(regionNode));
		transformedGraphics.setStroke(new BasicStroke(strokeWidth));
		Rectangle2D.Double shape = new Rectangle2D.Double(0, 0, regionNode.getShape().getBounds().getWidth(),
				regionNode.getShape().getBounds().getHeight());
		transformedGraphics.draw(shape);
	}
	
	private void paint(RegionTreeNode regionNode, Graphics2D g2d, float strokeWidth) {
		if (regionNode.getRegion() != null || drawVirtualRegions) {
			Graphics2D transformedGraphics = (Graphics2D) g2d.create();
			Bounds bounds = regionNode.getShape().getBounds();
			AffineTransform regionTransform = AffineTransform.getTranslateInstance(bounds.getX(), 
					bounds.getY());
			transformedGraphics.transform(regionTransform);
			drawNode(regionNode, transformedGraphics, strokeWidth);
		}
		if (regionNode.getLeftChild() != null) {
			paint(regionNode.getLeftChild(), g2d, strokeWidth);
		}
		if (regionNode.getRightChild() != null) {
			paint(regionNode.getRightChild(), g2d, strokeWidth);
		}
	}

	/**
	 * Set whether or not virtual regions should be drawn
	 * @param visibility
	 */
	public void setVirtualRegionsVisible(boolean visibility) {
		this.drawVirtualRegions = visibility;
	}

	public int getPixelRatio() {
		return (int) Math.round(1.0/scale);
	}

	/**
	 * function to manually reposition the view
	 * @param anotoX the leftmost viewable Anoto coordinate
	 * @param anotoY the topmost viewable Anoto coordinate
	 * @param pixelRatio the amount of Anoto coordinates represented by a single pixel
	 */
	public void reposition(double anotoX, double anotoY, double pixelRatio) {	
		if (pixelRatio==0) return;
		double scale = 1.0/pixelRatio;
		recalculateViewTransform(anotoX, anotoY, scale);
	}
	
	
}
