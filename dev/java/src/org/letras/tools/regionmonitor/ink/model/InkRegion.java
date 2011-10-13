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
package org.letras.tools.regionmonitor.ink.model;

import java.util.Vector;

import org.letras.psi.iregion.IRegion;



/**
 * Regions are rectangular shapes inside the Anoto pattern space. Such
 * regions are part of the model for pen stuff. They provide a subject-observer
 * implementation for change and modification of attached strokes.
 * <p>
 * An important property for the implementation of regions is the speed
 * for checking whether a given sample lies within a certain region. To do this,
 * certain strategies should be used. A quad- or kd-tree like organization of
 * the checked regions is one such strategy. In the current implementation 
 * (version 0.0.1) this is neglected, as first a working pipeline will be needed.
 * Future versions of the region class, however, should incorporate such
 * approaches.
 * <p>
 * The current implementation is based on the following assumptions
 * <ol>
 *   <li> there is a set of sub-regions for each region that is not a leaf
 *   <li> these sub-regions do not overlap, this means that a sample could exist
 *   only within one sub-region (and its sub-regions) not in several
 *   <li> a highly efficient organization of sub-regions (c.f. kd-tree etc.) is not
 *   needed as much as the implementation as a while
 *   <li> regions are constructed from larger to smaller regions: if there are already
 *   smaller regions registered and a larger region (e.g. an enclosing) region should 
 *   be registered, this is not possible. So construct the regions from the outermost
 *   regions towards the innermost regions
 *   <li> regions do not change after strokes are drawn on them: if they however do change,
 *   the strokes present on one region might need transfer to another region and further 
 *   splitting into smaller strokes
 * </ol>
 * Future versions should change the limitations and assumptions stated here towards
 * something more meaningful.
 * 
 * @author felix_h
 * @version 0.0.1
 */
public class InkRegion {

	private static final long serialVersionUID = 1L;
	
	// members

	private IRegion localRegion;
	
	private Vector<Stroke> strokes;
	
	private Vector<IInkRegionObserver> observers;

	/**
	 * Adds the specified <code>IInkRegionObserver</code> to the list of registered
	 * observers.
	 *
	 * @param observer			the new observer
	 */
	public void addRegionObserver(IInkRegionObserver observer) {
		if (observer != null)
			this.observers.add(observer);
	}

	/**
	 * Removes the specified <code>IInkRegionObserver</code> from the list of registered
	 * observers,
	 *
	 * @param observer			the observer to be removed
	 */
	public void removeRegionObserver(IInkRegionObserver observer) {
		if (observer != null)
			this.observers.remove(observer);
	}
	
	// constructors

	/**
	 * Standard constructor.
	 */
	public InkRegion(IRegion augmentedRegion) {
		this.observers = new Vector<IInkRegionObserver>();
		this.strokes = new Vector<Stroke>();
		this.localRegion = augmentedRegion;
	}

	// methods
	
	/**
	 * Computes and returns the aspect ratio of this region.
	 * 
	 * @return the aspect ratio of this region: (width / height) for 
	 * heigth != 0.0, 0.0 otherwise
	 */
	public double aspectRatio() {
		return (getHeight() != 0.0) ? getWidth() / getHeight() : 0.0;
	}

	private double getHeight() {
		return this.localRegion.shape().getBounds().getHeight();
	}
	
	private double getWidth() {
		return this.localRegion.shape().getBounds().getWidth();
	}

	/**
	 * Adds another stroke to the strokes of this region. As region checking should be performed
	 * on sample basis, the contract for calling this method is that the provided stroke (although
	 * it might still be in DRAWING mode, which means there will be some more samples attached to
	 * the stroke) will only contain samples within this region. For sake of efficiency this is
	 * not checked inside this method. A consistent model however requires this to be the case.
	 * Additionally the check for null pointers is omitted here, so be careful on what to pass and
	 * what not.
	 * 
	 * @param stroke		a <code>Stroke</code> consisting of samples within this region
	 */
	public void addStroke(Stroke stroke) {
		synchronized (this) {
			this.strokes.add(stroke);
		}
		// now notify all observers
		for (IInkRegionObserver observer : this.observers) observer.strokeAdded(this, stroke);
	}
	
	/**
	 * Removes a stroke from this region.
	 * 
	 * @param stroke		a <code>Stroke</code>
	 */
	public void removeStroke(Stroke stroke) {
		synchronized (this) {
			this.strokes.remove(stroke);
		}
		// now notify all observers
		for (IInkRegionObserver observer : this.observers) observer.strokeRemoved(this, stroke);
	}

	/**
	 * Getter for retrieval of the strokes vector. The returned vector should not
	 * be edited, any changes might violate the assumed stroke organization. 
	 * @return
	 */
	public Vector<Stroke> getStrokes() {
		return strokes;
	}
	
	@Override
	public String toString() {
		return String.format("[%s | %s]", this.localRegion.toString(), super.toString());
	}
}
