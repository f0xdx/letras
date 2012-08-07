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
import java.util.concurrent.CopyOnWriteArrayList;

import org.letras.api.region.RegionSample;

/**
 * This represents a stroke consisting of several pen samples. Strokes
 * are part of the model classes. Pen samples of strokes contain their
 * coordinates in a normalized form respective to the enclosing region.
 * Strokes are attached to regions (where the stroke was created) and typically
 * created using a <code>StrokeAggregator</code>. A stroke has the following logical
 * states
 * <ol>
 *  <li> DRAWING
 *  <li> DRAWN
 * </ol>
 * When a new stroke is created it starts in the DRAWING mode. In this mode,
 * new samples can be added using the <code>addSample()</code> method. If this
 * is finished, the stroke will be set to DRAWN mode, which means that no new
 * samples will be recorded.
 * <p>
 * NOTE In the current version the <code>transfer</code> operation is not yet 
 * implemented. This means, that no easy way to transfer a stroke into another
 * region exists.
 *  
 * @author felix_h
 * @version 0.0.1
 */
public class Stroke extends DigitalInkStructure {

	// defaults
	public static final int DRAWING_MODE = 0;
	public static final int DRAWN_MODE = 1;
	// members
	private int mode;
	private Stroke successor;
	private Stroke predecessor;
	private Vector<RegionSample> samples;
	private CopyOnWriteArrayList<IStrokeObserver> observers;

	// constructors
	public Stroke() {
		super();
		this.samples = new Vector<RegionSample>();
		this.observers = new CopyOnWriteArrayList<IStrokeObserver>();
		this.mode = DRAWING_MODE;
	}

	// methods
	/**
	 * @return true if this stroke has a successor, false otherwise
	 */
	public boolean hasSuccessor() {
		return (this.successor == null);
	}

	/**
	 * @return true if this stroke has a predecessor, false otherwise
	 */
	public boolean hasPredecessor() {
		return (this.predecessor == null);
	}

	/**
	 * @return the successor
	 */
	public Stroke getSuccessor() {
		return successor;
	}

	/**
	 * @param successor the successor to set
	 */
	public void setSuccessor(Stroke successor) {
		this.successor = successor;
	}

	/**
	 * @return the predecessor
	 */
	public Stroke getPredecessor() {
		return predecessor;
	}

	/**
	 * @param predecessor the predecessor to set
	 */
	public void setPredecessor(Stroke predecessor) {
		this.predecessor = predecessor;
	}

	/**
	 * Adds an <code>IStrokeObserver</code> to the list of registered
	 * observers.
	 * 
	 * @param observer		the new observer
	 */
	public void addStrokeObserver(IStrokeObserver observer) {
		if (observer != null) {
			this.observers.add(observer);
		}
	}

	/**
	 * Removes an <code>IStrokeObserver</code> from the list of registered
	 * observers.
	 * 
	 * @param observer		the observer to be removed
	 */
	public void removeStrokeObserver(IStrokeObserver observer) {
		if (observer != null) {
			this.observers.remove(observer);
		}
	}

	/**
	 * Returns the current mode of this <code>Stroke</code>.
	 * 
	 * @return 		the current mode of this <code>Stroke</code>
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Sets the current mode of this stroke to the specified mode.
	 * 
	 * @param mode		mode of this <code>Stroke</code>, might be one of
	 * 					the modes specified in the <code>Storke</code> class
	 */
	public void setMode(int mode) {
		if ((mode == DRAWING_MODE) || (mode == DRAWN_MODE)) {
			int oldMode = this.mode;
			this.mode = mode;
			// notify the observers
			// NOTE: this method might provoke
			// observers to unregister.. BANG!!! ConcurrentModificationException,
			// Solution: create a copy of the list.. This is ugly though.
			// Alternative solution: use the not fail-fast behavior of the enumeration
			// returned by observers.elements().. This is discouraged though by
			// the java guys. What should we do?
			// we try to use a scheduled set of observers: removes will be handled
			// after the iteration has succeeded
			// ATTENTION: this should be solved using the CopyOnWriteArrayList,
			// which is what Sun suggests for the problem
			if (oldMode != mode) {
				for (IStrokeObserver observer : this.observers) {
					observer.strokeModeChanged(this, oldMode, mode);
				}
			}
		}
	}

	/**
	 * This method returns a vector of all the samples in this stroke. Note
	 * that this is meant for READ-ONLY access. In order to increase performance
	 * no copy of the samples vector is returned here, the return value will be
	 * the original samples vector instead. DO NOT EDIT, or the behavior will be
	 * undefined. Note that the contained samples will change as long as the mode
	 * of this stroke is not yet set to DRAWN.
	 *  
	 * @return a <code>Vector</code> containing all the samples in this stroke
	 */
	public synchronized Vector<RegionSample> samples() {
		return this.samples;
	}

	/**
	 * This method adds a new sample to the list of samples making up this stroke.
	 * It is synchronized with other methods accessing the samples. The contract of
	 * calling this method is that the provided samples coordinates are already 
	 * normalized to the region of this stroke (Region should be passed in the 
	 * constructor of the sample, see {@link dgl.ppi.model.Sample} for details).
	 * 
	 * @param sample		the new <code>Sample</code> to add
	 * @return true if the sample could be added, false otherwise
	 */
	public synchronized boolean addSample(RegionSample sample) {
		if (this.mode == DRAWING_MODE) {
			// NOTE this method keeps also the bounding box and the
			// lower and upper temporal bounds of this stroke correct
			// synchronize the access to the internal list of samples
			// check for the different procedure of the first entry
			if (this.samples.isEmpty()) {
				this.samples.add(sample);
				// the bounding box needs to set its upper lest corner
				// to this sample
				this.boundingBox.x = sample.getX();
				this.boundingBox.y = sample.getY();
				this.lowerTBound = sample.getTimestamp();
				this.upperTBound = sample.getTimestamp();
			} else {
				this.samples.add(sample);
				// recompute the temporal bounds
				this.lowerTBound = ((sample.getTimestamp()) < this.lowerTBound)
						? sample.getTimestamp() : this.lowerTBound;
				this.upperTBound = ((sample.getTimestamp()) > this.upperTBound)
						? sample.getTimestamp() : this.upperTBound;
			}

			// recompute the bounding box
			this.boundingBox.add(sample.getX(), sample.getY());

			// notify the observers
			for (IStrokeObserver observer : this.observers) {
				observer.sampleAdded(this, sample);
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Used to set this stroke in finished mode (after all its samples have been
	 * collected). This is a convenience method for <code>setMode(Stroke.DRAWN_MODE)</code>.
	 */
	public void finished() {
		this.setMode(DRAWN_MODE);
	}

	// static methods
	/**
	 * Transfers the provided stroke's samples to the provided region. This
	 * basically means that all of the stroke's samples are normalized to coordinates
	 * within this region. 
	 */
//	public static void transfer(Stroke stroke, Region region) {
//		// TODO implement if the stroke transfer should be supported
//		// if this is implemented, change NOTE in the class docu
//	}
}
