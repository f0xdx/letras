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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.letras.ps.region.RegionTreeNode;
import org.letras.psi.iregion.IDigitalInkConsumer;
import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.RegionEvent;
import org.letras.psi.iregion.RegionSample;
import org.letras.psi.iregion.shape.Bounds;
import org.letras.tools.regionmonitor.ink.model.Stroke;
import org.letras.tools.regionmonitor.ink.view.DigitalInkRenderer;
import org.letras.tools.regionmonitor.ink.view.LineStroke;
import org.letras.tools.regionmonitor.ink.view.DigitalInkRenderer.IUpdateCallback;
import org.letras.tools.regionmonitor.regions.view.RegionCanvas;

public class InkRegionCanvas extends RegionCanvas implements IDigitalInkConsumer, IUpdateCallback {
	
	private static final long serialVersionUID = 8989307192630548856L;
	private static final float LINEWIDTH_INCREMENT = 3.0f;
	private static final float BASE_LINEWIDTH = 1.0f;
	private List<IRegion> regions = new ArrayList<IRegion>();
	private Map<IRegion, DigitalInkRenderer> renderers = new HashMap<IRegion, DigitalInkRenderer>();
	private Map<IRegion, Stroke> regionToStrokes = new HashMap<IRegion, Stroke>();
	private Map<Stroke, DigitalInkRenderer> strokeRenderers = new HashMap<Stroke, DigitalInkRenderer>();
	private Map<IRegion, BufferedImage> regionBuffers = new HashMap<IRegion, BufferedImage>();
	private Map<IRegion, Integer> regionLevels = new HashMap<IRegion, Integer>();
	private List<Stroke> completedStrokes = new ArrayList<Stroke>();
	
	private Map<IRegion, Color> regionColors = new HashMap<IRegion, Color>();
	private int maxLevel;
	
	public InkRegionCanvas() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				initializeDoubleBuffers();
				redrawOldStrokes();
			}

		});
		setVirtualRegionsVisible(false);
	}
	
	private void initializeDoubleBuffers() {
		for (IRegion region: regions) {
			BufferedImage strokeBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = strokeBuffer.getGraphics();
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
			g.fillRect(0, 0, getWidth(), getHeight());
			regionBuffers.put(region, strokeBuffer);
		}
		for (IRegion region: renderers.keySet()) {
			DigitalInkRenderer renderer = renderers.get(region);
			Graphics2D g2d = regionBuffers.get(region).createGraphics();
			g2d.transform(getViewTransform());
			renderer.setGraphics(g2d);
		}
	}
	
	@Override
	protected void setViewTransform(AffineTransform newViewTransform) {
		super.setViewTransform(newViewTransform);
		initializeDoubleBuffers();
	}

	@Override
	public void setToplevelRegion(RegionTreeNode toplevelRegion) {
		cleanup();
		if (toplevelRegion != null) {
			this.toplevelRegion = toplevelRegion;
			recalculateViewTransform();
			handleRegion(toplevelRegion, 0);
			Collections.sort(regions, new Comparator<IRegion>() {
				@Override
				public int compare(IRegion o1, IRegion o2) {
					return regionLevels.get(o1).compareTo(regionLevels.get(o2));
				}
			});
			assignRegionColors();
			initializeDoubleBuffers();
			repaint();
		}
	}
	
	private void cleanup() {
		regions.clear();
		for (DigitalInkRenderer renderer: renderers.values()) {
			renderer.flush();
		}
		renderers.clear();
		regionToStrokes.clear();
		strokeRenderers.clear();
		completedStrokes.clear();
		regionBuffers.clear();
	}

	private void assignRegionColors() {
		regionColors = new HashMap<IRegion, Color>();
		float increment = 1.0f / (regions.size() + 1);
		for (int i=0; i < regions.size(); i++) {
			Color color = Color.getHSBColor(i * increment, 1.0f, 1.0f);
			regionColors.put(regions.get(i), color);
		}
	}

	private void handleRegion(RegionTreeNode node, int level) {
		if (node == null)
			return;
		IRegion region = node.getRegion();
		if (region != null) {
			level++;
			maxLevel = Math.max(level, maxLevel);
			regionLevels.put(region, level);
			regions.add(node.getRegion());
		}
		handleRegion(node.getLeftChild(), level);
		handleRegion(node.getRightChild(), level);
	}

	private DigitalInkRenderer getRenderer(IRegion region) {
		DigitalInkRenderer renderer = renderers.get(region);
		if (renderer == null) {
			Rectangle position = new Rectangle(0, 0,
					(int) region.shape().getBounds().getWidth(),
					(int) region.shape().getBounds().getHeight());
			AffineTransform regionTransform = calculateRegionTransform(region);
			float linewidth = BASE_LINEWIDTH + (maxLevel -  regionLevels.get(region)) * LINEWIDTH_INCREMENT;
			LineStroke lineStroke = LineStroke.createLineStroke(linewidth, regionColors.get(region));
			renderer = new DigitalInkRenderer(position, regionTransform, lineStroke);
			Graphics2D g2d = regionBuffers.get(region).createGraphics();
			g2d.transform(getViewTransform());
			renderer.setGraphics(g2d);
			renderers.put(region, renderer);
		}
		return renderer;
	}

	private AffineTransform calculateRegionTransform(IRegion region) {
		Bounds bounds = region.shape().getBounds();
		double deltaX = bounds.getX();
		double deltaY = bounds.getY();
		return AffineTransform.getTranslateInstance(deltaX,
				deltaY);
	}
	
	private void redrawOldStrokes() {
		for (Stroke stroke: completedStrokes) {
			strokeRenderers.get(stroke).renderStroke(stroke, this);
		}
	}

	@Override
	public void consume(IRegion source, RegionSample regionSample) {
		System.out.println("received sample on " + source.channel());
		getRenderer(source);
		Stroke stroke = regionToStrokes.get(source);
		if (stroke == null)
			System.out.println("Error: trying to add sample without existing stroke");
		else
			stroke.addSample(regionSample);
	}

	@Override
	public void consume(IRegion source, RegionEvent regionEvent) {
		if (regionEvent.traceStart()) {
			Stroke stroke = new Stroke();
			DigitalInkRenderer renderer = getRenderer(source);
			renderer.renderStroke(stroke, this);
			strokeRenderers.put(stroke, renderer);
			regionToStrokes.put(source, stroke);
		} else if (regionEvent.traceEnd()) {
			Stroke stroke = regionToStrokes.get(source);
			stroke.finished();
			completedStrokes.add(stroke);
		}
	}
	
	@Override
	protected Color getColor(RegionTreeNode node) {
		Color color = regionColors.get(node.getRegion());
		return color != null ? color : Color.black;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.create();
		for (IRegion region: regions) {
			g.drawImage(regionBuffers.get(region), 0, 0, this.getWidth(), this.getHeight(), null);
		}
	}
	
	@Override
	public void update(Rectangle2D r) {
		repaint();
	}
	
}
