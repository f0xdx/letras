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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.CubicCurve2D.Double;
import java.util.Hashtable;
import java.util.ListIterator;

import org.letras.psi.iregion.RegionSample;
import org.letras.tools.regionmonitor.ink.model.IStrokeObserver;
import org.letras.tools.regionmonitor.ink.model.Stroke;

/**
 * The {@link DigitalInkRenderer} performs the actual rendering of digital ink data
 * on a provided buffer. It can use various interpolations to do so. Note that the
 * graphics must be set in order to achieve any true rendering. Whenever updates or
 * similar stuff happens, the renderer should be flushed (via a call to <code>flush()</code>).
 * Note that the contract foresees that the entity using the digital ink renderer
 * <ul>
 *   <li> makes sure that the appropriate graphics object is set
 *   <li> takes care of disposing etc. of this object
 * </ul>
 * 
 * @author felix_h
 * @version 0.1
 */
public class DigitalInkRenderer implements IStrokeObserver {

	// TODO line stroke is currently not used, enable its usage
	
	/**
	 * This interface defines the call-back method indicating that a stroke
	 * has now been rendered and the visualizing component should update.
	 */
	public interface IUpdateCallback {
		/**
		 * Calling this method serves as mechanism to notify interested parties
		 * that a stroke now has been rendered. Whether it has been completed
		 * is not specified, only the dirty-rectangle is passed indicating
		 * where the update has been performed (rectangle has normalized region
		 * coordinates respective to the interactive region the rendered samples
		 * lie on).
		 *
		 * @param r		the dirty-rect
		 */
		public void update(Rectangle2D r);
	}

	// members

	// This is the transform describing the regions upper left corner position
	// in relation to the ink panel (should be only rotation and translation)
	private AffineTransform regionTransform;
	
	/**
	 * @return the regionTransform
	 */
	public AffineTransform getRegionTransform() {
		return regionTransform;
	}

	/**
	 * Set the region transform. Note that if this is called with an existing
	 * transform, the graphics need to be reset also (dispose, create, set)!!!
	 * 
	 * @param regionTransform the regionTransform to set
	 */
	public void setRegionTransform(AffineTransform regionTransform) {
		this.regionTransform = regionTransform;
		if ((regionTransform != null)&&(this.graphics != null)) 
			this.graphics.transform(regionTransform);
	}

	// This is the rectangle describing the interactive region boundaries relative
	// to the coordinate system specified by the regionTransform, typically its
	// x and y values should be zero, otherwise it will introduce an additional
	// translation to the region transform
	// NOTE this rectangle should have the same aspect ratio as the interactive
	// region the samples are recorded on, otherwise this will result in a
	// distortion effect
	private Rectangle regionPosition;
	
	/**
	 * @return the regionPosition
	 */
	public Rectangle getRegionPosition() {
		return regionPosition;
	}

	/**
	 * @param regionPosition the regionPosition to set
	 */
	public void setRegionPosition(Rectangle regionPosition) {
		this.regionPosition = regionPosition;
	}

	// This provides the stroke (line width, color etc.) which should be used for
	// rendering digital ink.. Don't confuse it with our strokes ;)
	private LineStroke lineStroke;
	
	/**
	 * @return the lineStroke
	 */
	public LineStroke getLineStroke() {
		return lineStroke;
	}

	/**
	 * @param lineStroke the lineStroke to set
	 */
	public void setLineStroke(LineStroke lineStroke) {
		this.lineStroke = lineStroke;
		if ((this.lineStroke != null)&&(this.graphics != null)) {
			this.graphics.setStroke(this.lineStroke);
			this.graphics.setColor(this.lineStroke.getStrokeColor());
		}
	}

	private boolean antialiasing;

	public boolean isAntialiasing() {
		return antialiasing;
	}

	public void setAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
	}

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
	}

	private Hashtable<Stroke, IUpdateCallback> callbackMap;
	private boolean flushed;

	private final Object lock;

	private Graphics2D graphics;
	
	/**
	 * @return the graphics
	 */
	public Graphics2D getGraphics() {
		return graphics;
	}

	/**
	 * @param graphics the graphics to set
	 */
	public void setGraphics(Graphics2D graphics) {
		this.graphics = graphics;
		// switch antialiasing on if required
		if (this.antialiasing)
			this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		// improve the rendering of line strokes
		this.graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
				RenderingHints.VALUE_STROKE_NORMALIZE);
		if ((this.regionTransform != null)&&(this.graphics != null)) 
			this.graphics.transform(regionTransform);
		if ((this.lineStroke != null)&&(this.graphics != null)) {
			this.graphics.setStroke(this.lineStroke);
			this.graphics.setColor(this.lineStroke.getStrokeColor());
		}
	}
	
	// constructors

	public DigitalInkRenderer() {
		this.callbackMap = new Hashtable<Stroke, IUpdateCallback>();
		this.lock = new Object();
		this.flushed = true;
		this.antialiasing = true;
		this.interpolationMethod = InterpolationFactory.createInterpolation();
	}
	
	/**
	 * This constructor should be used to instantiated renderers.
	 * 
	 * @param regionPosition 	the aspect ratio of the interactive region provided in form of
	 * 							a rectangle (must not be null)
	 * @param regionTransform	the affine transform describing the regions position in relation
	 * 							the set graphics object (may be null)
	 * @param lineStroke		the stroke to use for rendering (may be null)
	 */
	public DigitalInkRenderer(Rectangle regionPosition, AffineTransform regionTransform, 
			LineStroke lineStroke) {
		this();
		this.setRegionPosition(regionPosition);
		this.setRegionTransform(regionTransform);
		this.setLineStroke(lineStroke);
	}
	
	/**
	 * This constructor should be used to instantiated renderers.
	 * 
	 * @param regionPosition 	the aspect ratio of the interactive region provided in form of
	 * 							a rectangle (must not be null)
	 * @param interpolationMethod method used for interpolation (must not be null)
	 * @param regionTransform	the affine transform describing the regions position in relation
	 * 							the set graphics object (may be null)
	 * @param lineStroke		the stroke to use for rendering (may be null)
	 */
	public DigitalInkRenderer(Rectangle regionPosition, IInterpolation interpolationMethod,
			AffineTransform regionTransform, LineStroke lineStroke) {
		this(regionPosition, regionTransform, lineStroke);
		this.interpolationMethod = interpolationMethod;
	}

	// methods

	/**
	 * This is called in order to render a given stroke. The provided call-back
	 * will be used to initialize update calls to the rendering method. A null
	 * call-back indicates, that the caller will handle redraw requests itself.
	 * In this case, only completed (i.e. <code>DRAWN</code>) strokes will be
	 * rendered. Strokes which still are drawing need to provide a call-back
	 * (what makes sense, considering the fact that we do not know when the next
	 * sample arrives).
	 * <p>
	 * The concurrency behavior is as follows: If the stroke is already finished,
	 * the renderer will render the stroke onto its graphics, then notify the
	 * call-back (if available) and return. Otherwise it will register itself
	 * as stroke listener to the drawing stroke, render the stroke as far as it
	 * is completed already notify the call-back and return. Whenever another
	 * sample is added to the stroke, this will be rendered and the call-back will
	 * be notified, until the stroke is finished.
	 *
	 * @param stroke		the stroke to render
	 * @param callback		the callback to notify of changes to the buffer
	 *						(may be null)
	 */
	public void renderStroke(Stroke stroke, IUpdateCallback callback) {
		assert (stroke != null) : "stroke must not be null";
		if (stroke.getMode() == Stroke.DRAWING_MODE) {
			// handle situation if the stroke is still drawing
			synchronized (lock) {
				// first register this as a stroke observer
				this.flushed = false;
				if (callback != null)
					this.callbackMap.put(stroke, callback);
				stroke.addStrokeObserver(this);
				
				// now render the stroke as far as it is already available
				this.renderSamples(stroke);
				
				// and notify the callback
				if (callback != null) callback.update(stroke.getBoundingBox());
			}			
		}
		else {
			// handle situation if the stroke has already been drawn
			this.renderSamples(stroke);
			if (callback != null) callback.update(stroke.getBoundingBox());
		}
	}

	/**
	 * Helper method to render all currently available samples of this stroke.
	 * 
	 * @param stroke
	 */
	private void renderSamples(Stroke stroke) {
		assert (this.graphics != null) : "graphics must be set to use the digital ink renderer";
		assert (this.regionPosition != null) : "region position must be set";
		assert (this.interpolationMethod != null) : "interpolation method must be set";
		assert (stroke.samples() != null) : "stroke's samples must not be null";
		// TODO clarify whether we need to render single sample strokes..
		// we still need at least two samples to render a stroke (unless we render single
		// sample strokes also)

		// NOTE here the devil lies in the detail: as the smaples vector in a
		// stroke will be filled in a separate thread, the iteration over all
		// samples might result in a ConcurrentModificationException. To overcome
		// this, the following options are available:
		//  * copy the list prior to iterating over it: this introduces some
		//    performance penalty on each call to this method, otherwise it could
		//    be safe, iff there is no modification while copying... ;), basically
		//    this does not solve the problem
		//  * use a synchronized version of the sample list and acquire the lock
		//    before starting to iterate over the elements: well, this solves the
		//    problem of concurrent modification. However, it means that no other
		//    entity can iterate over the samples in between (e.g. the interpolation
		//    method cannot).. Bad!!
		// TODO check whether the last statement is true. If synchronized only
		// prevents the access by other threads, and leaves the current thread alone,
		// this might be what we want after all
		//  * use one of the special concurrent access data structures, e.g. the
		//    ConcurrentSkipListSet: Not all these structures are equally well
		//    suited. For example the CopyOnWriteArrayList does not have the
		//    performance criteria we want, since adding list entries is to
		//    expensive. ConcurrentSkipListSet seems promising however, although
		//    this requires the interface to change.
		// DECISION: I try synchronizing the calls.. Hope for the best!! Upon
		// deadlock revisit this secion :).
		if (stroke.samples().size() < 2) return;
		ListIterator<RegionSample> it = stroke.samples().listIterator();
		
		RegionSample lastSample, currentSample;
		lastSample = it.next();
		while (it.hasNext()) {
			currentSample = it.next();
			Shape interpolatedSample = this.interpolationMethod.interpolate(stroke, lastSample, 
					currentSample, this.regionPosition.getWidth(), this.regionPosition.getHeight());
			this.graphics.draw(interpolatedSample);
			lastSample = currentSample;
		}
	}
	
	public void printShapeInfo(Shape s) {
		if (s instanceof CubicCurve2D.Double) {
			CubicCurve2D.Double cc = (Double) s;
			System.out.println("Shape = CubicCurve([" + cc.x1 + "," + cc.y1 + "], [" + cc.x2 + "," + cc.y2 + "], [" + cc.ctrlx1 + "," + cc.ctrly1 + "], [" + cc.ctrlx2 + "," + cc.ctrly2 + "])");
		}
	}
	/**
	 * This cancels all current drawing operations.
	 */
	public void flush() {
		synchronized (lock) {
			// remove this as listener to all ongoing strokes and clear the
			// stroke map afterwards
			for (Stroke s : this.callbackMap.keySet())
				s.removeStrokeObserver(this);
			this.callbackMap.clear();
			this.flushed = true;
		}
	}

	/* (non-Javadoc)
	 * @see dgl.ppi.model.IStrokeObserver#sampleAdded(dgl.ppi.model.Stroke, dgl.ppi.model.Sample)
	 */
	@Override
	public void sampleAdded(Stroke stroke, RegionSample sample) {
		// interpolate between this sample and the previous one
		
		// first check whether a flush is required (we need to cancel our 
		// subscription to the stroke then)
		synchronized (lock) {
			if (this.flushed) return;
			
			// interpolate between this sample and the previous one
			ListIterator<RegionSample> it = stroke.samples().listIterator(stroke.samples().indexOf(sample));
			
			RegionSample previousSample = it.hasPrevious() ? it.previous() : null;
			if (previousSample == null) return;
			Shape interpolatedSample = this.interpolationMethod.interpolate(stroke, previousSample, 
					sample, this.regionPosition.getWidth(), this.regionPosition.getHeight());
			this.graphics.draw(interpolatedSample);
			
			// issue the call-back to notify of updates (if available)
			if (this.callbackMap.containsKey(stroke)) 
				this.callbackMap.get(stroke).update(stroke.getBoundingBox());
		}
	}

	/* (non-Javadoc)
	 * @see dgl.ppi.model.IStrokeObserver#strokeModeChanged(dgl.ppi.model.Stroke, int, int)
	 */
	@Override
	public void strokeModeChanged(Stroke stroke, int oldMode, int newMode) {
		// check whether this stroke has been finished
		synchronized (lock) {
			if (this.flushed) return;
			if (newMode == Stroke.DRAWN_MODE)
				stroke.removeStrokeObserver(this);
		}
	}
}
