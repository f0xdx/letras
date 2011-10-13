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
package org.letras.tools.regionmonitor.ink.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.letras.tools.regionmonitor.ink.model.IInkRegionObserver;
import org.letras.tools.regionmonitor.ink.model.InkRegion;
import org.letras.tools.regionmonitor.ink.model.Stroke;

/**
 * Panel class used to display the eInk data of a given region.
 * 
 * @author felix_h
 *
 */
public class DigitalInkPanel extends JPanel implements IInkRegionObserver {

	private static final long serialVersionUID = 1L;
	
	// defaults
	protected static final int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB_PRE;
	
	// members

	private BufferedImage backBuffer;
	
	private InkRegion displayRegion;
	
	private LineStroke lineStroke;

	private IInterpolation interpolationMethod;

	/**
	 * @return the interpolationMethod
	 */
	public IInterpolation getInterpolationMethod() {
		return interpolationMethod;
	}

	/**
	 * @param interpolationMethod the interpolationMethod to set
	 */
	public void setInterpolationMethod(IInterpolation interpolationMethod) {
		this.interpolationMethod = interpolationMethod;
		if (interpolationMethod != null)
			this.renderingDelegate.setInterpolationMethod(interpolationMethod);
	}

	public LineStroke getLineStroke() {
		return lineStroke;
	}

	public void setLineStroke(LineStroke lineStroke) {
		this.lineStroke = lineStroke;

		// set the used stroke in the rendering delegate
		if ((this.lineStroke != null)&&(this.renderingDelegate != null))
			this.renderingDelegate.setLineStroke(this.lineStroke);
	}

	private ComponentListener preparationControl = new ComponentListener() {

		@Override
		public void componentHidden(ComponentEvent arg0) {
			// unused
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			// unused
			
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			if (!isPrepared()) {
				prepare();
				renderComplete();
			}
			repaint();
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			if (!isPrepared()) {
				prepare();
				renderComplete();
			}
			repaint();
		}	
	};
	
	private DigitalInkRenderer.IUpdateCallback updateControl = 
			new DigitalInkRenderer.IUpdateCallback() {

		public void update(Rectangle2D r) {
			// TODO use the appropriate method here
			// renderUpdate(r);
			// TEST
			repaint();
		}
	};

	private DigitalInkRenderer renderingDelegate;

	/**
	 * @return the displayRegion
	 */
	public InkRegion getDisplayRegion() {
		return displayRegion;
	}

	/**
	 * @param displayRegion the displayRegion to set
	 */
	public void setDisplayRegion(InkRegion displayRegion) {
		assert (displayRegion != null) : "display region cannot be set to null";

		// first remove this from the old region if it has been initialized
		if (this.displayRegion != null) this.displayRegion.removeRegionObserver(this);
		// now set the new region and add this as listener to the regions events
		this.displayRegion = displayRegion;
		this.displayRegion.addRegionObserver(this);
		
		// now prepare (if this already initialized)
		if (this.isDisplayable()) this.prepare();
	}

	private BufferedImage backgroundImage;
	
	/**
	 * @return the backgroundImage
	 */
	public BufferedImage getBackgroundImage() {
		return backgroundImage; 
	}

	/**
	 * @param backgroundImage the backgroundImage to set
	 */
	public void setBackgroundImage(BufferedImage backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	// constructors

	/**
	 * No argument constructor for bean serialization. Do not use this
	 * constructor, use the other one instead.
	 */
	public DigitalInkPanel() {
		super();
		this.setBorder(null);
		this.addComponentListener(this.preparationControl);
		this.renderingDelegate = new DigitalInkRenderer();
	}
	
	/**
	 * Constructor for the <code>DigitalInkPanel</code>.
	 * 
	 * @param region
	 * @param image
	 */
	public DigitalInkPanel(InkRegion region, BufferedImage image) {
		this();
		assert (region != null) && (image!= null) : 
			"region/image must not be null";
		this.setDisplayRegion(region);
		this.setBackgroundImage(image);
	}
	
	// methods
	
	/**
	 * Helper method to initialize the rendering delegate.
	 */
	private void prepareRenderingDelegate() {
		
		this.renderingDelegate.flush();
		
		// TODO check for the region transform and the region position
		// TODO define the region where the digital ink should be displayed
		// through its AffineTransform (coordinate system anchor in relation to this)
		// and its bounding box
		// this probably needs to be configured externally, since these are inherent
		// properties of the design of the document
		
		this.renderingDelegate.setRegionPosition(
				new Rectangle(0,0,(int) this.getSize().getWidth(), (int) this.getSize().getHeight())
				);
		if (this.renderingDelegate.getGraphics() != null)
			this.renderingDelegate.getGraphics().dispose();
		this.renderingDelegate.setGraphics(this.backBuffer.createGraphics());
	}
	
	/**
	 * Called to prepare the panel for rendering. This must be called
	 * whenever the size of the panel has been changed, including at the
	 * beginning, to forge a fitting backbuffer.
	 */
	protected void prepare() {
		if ((this.getSize().getWidth() > 0)&& (this.getSize().getHeight() > 0)) {
			this.backBuffer = new BufferedImage(this.getSize().width,
				this.getSize().height, IMAGE_TYPE);
			// check that the graphics object in the rendering delegate remains up-to-date
			this.prepareRenderingDelegate();
		}
	}
	
	/**
	 * Helper method to determine whether the current backbuffer is prepared
	 * to handle the drawing.
	 *  
	 * @return
	 */
	protected boolean isPrepared() {
		return ((this.backBuffer != null) &&
				((this.getSize().width == this.backBuffer.getWidth()) &&
				(this.getSize().height == this.backBuffer.getHeight())));
	}
	
	/**
	 * Renders the background image and the ink data of the associated region.
	 */
	protected synchronized void renderComplete() {
		assert (this.backBuffer != null) : "back buffer uninitialized";
		// first flush all ongoing rendering processes in the
		// rendering delegate
		this.renderingDelegate.flush();

		// paint the background Image
		Graphics2D g = this.backBuffer.createGraphics();
		if (this.backgroundImage != null) {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);
		}
		g.dispose();

		// NOTE if there is some inconsistent behavior in the rendering process
		// re-check the concurrency model employed here. At this point, we draw
		// each finished stroke w/o call-backs and set up update call-backs for
		// the unfinished strokes. Since this method blocks the update mechanism
		// from being executed until it is finished, the final repaint call should
		// reflect all strokes that could be rendered. Then the queued update
		// calls will be executed, resulting in a small performance penalty
		// (unless of course, the repaint dispatching stuff handles these succeeding
		// repaint requests intelligently). However, there should be no "missed"
		// samples.. If this is not the case: re-check ;)
		
		if (this.displayRegion != null) {
			// now render all ink data again
			for (Stroke s : this.displayRegion.getStrokes()) 
				if (s.getMode() == Stroke.DRAWN_MODE) 
					this.renderingDelegate.renderStroke(s, null);
				else
					this.renderingDelegate.renderStroke(s, this.updateControl);
		}

		super.repaint();
	}
	
	/**
	 * Renders only the update in the specified rectangular region.
	 */
	protected synchronized void renderUpdate(Rectangle dirtyRect) {
		assert this.backBuffer != null : "back buffer uninitialized";
		// TODO check whether this works that way... Might be, that we have
		// to somehow blit the rectangle contents of the backbuffer onto the
		// graphics of this component (via overriding the repaint(rect) method)
		// NOTE keep in mind, that the provided rect can only be in normalized
		// coordinates, or relative to the region rectangle set in the 
		// DigitalInkRenderer --> adjust implementation of the renderer, use
		// the AffineTransform in the latter case to convert coordinates
		
		//super.repaint(dirtyRect);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		// we can draw the complete image: the cliping rect will specify which region
		// of it can be drawn anyway
		//g.drawImage(this.backBuffer, 0, 0, null);
		g.drawImage(this.backBuffer, 0, 0, null);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	/* (non-Javadoc)
	 * @see dgl.ppi.model.IRegionObserver#strokeAdded
	 */
	public synchronized void strokeAdded(InkRegion region, Stroke stroke) {
		if ((region != null)&&(region.equals(this.displayRegion)))
			this.renderingDelegate.renderStroke(stroke, this.updateControl);
	}

	/* (non-Javadoc)
	 * @see dgl.ppi.model.IRegionObserver#strokeRemoved
	 */
	public synchronized void strokeRemoved(InkRegion region, Stroke stroke) {
		if ((region != null)&&(region.equals(this.displayRegion))) this.renderComplete();
	}
}
