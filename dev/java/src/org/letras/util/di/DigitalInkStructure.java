/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import java.util.ArrayList;
import java.util.Iterator;
import org.letras.util.geom.Rectangle2d;

/**
 * Composite of the tree-like digital ink data structure. The {@link DigitalInkStructure}
 * consists of several digital ink sub structures. It represents the composite
 * in the employed composite pattern.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public class DigitalInkStructure extends DigitalInk {

	// DEFAULTS

	private static final int CHILD_CAPACITY = 12;

	// MEMBERS

	private final Object lock = new Object();

	private Rectangle2d bb;
	private long[] tf;
	private double pathLength;
	private ArrayList<DigitalInk> children;

	/**
	 * Returns the children of this structure. The returned {@link ArrayList}
	 * is immutable, i.e. the caller must not alter its structure.
	 * 
	 * @return 
	 */
	public ArrayList<DigitalInk> getChildren() {
		return children;
	}

	/**
	 * Sets the children of this structure. This will trigger recomputation of
	 * bounding box, time frame and path length.
	 * 
	 * @param children 
	 */
	public void setChildren(ArrayList<DigitalInk> children) {
		this.children = children;
		this.computeBoundingBox();
		this.computeTimeFrame();
		this.computePathLength();
	}

	// CONSTRUCTORS

	public DigitalInkStructure() {
		this.children = new ArrayList<DigitalInk>(CHILD_CAPACITY);
		this.bb = new Rectangle2d();
		this.tf = new long[2];
		this.pathLength = 0.0;
	}

	// METHODS

	/**
	 * Method to recompute the bounding box. This will cause a traversal over
	 * all children of this structure. As a side effect of calling this method, the
	 * bounding box {@link Rectangle2d} will be changed.
	 * 
	 * @return the bounding box of this trace
	 */
	protected Rectangle2d computeBoundingBox() {
		synchronized (lock) {
			if (this.children.isEmpty()) {
				this.bb.init(0.0, 0.0, 0.0, 0.0);
			}
			else {
				this.bb.init(this.children.get(0).boundingBox());
				// NOTE for syntax here is optimized to relieve the garbage
				// collector on some virtual machines (e.g. Android / Dalvik)
				int size = this.children.size();
				for (int i = 1; i < size; i++) 
					this.bb.join(this.children.get(i).boundingBox());
			}
			return this.bb;
		}
	}

	/**
	 * Method to recompute the time frame. This will cause a traversal over
	 * all children of this structure. As a side effect of calling this method the
	 * time frame array will be changed.
	 * 
	 * @return a 2-element array containing the start and end point of the time
	 * frame
	 */
	protected long[] computeTimeFrame() {
		synchronized (lock) {
			if (this.children.isEmpty()) {
				this.tf[0] = this.tf [1] = 0l;
			}
			else {
				this.tf[0] = this.children.get(0).timeFrame()[0];
				this.tf[1] = this.children.get(0).timeFrame()[1];
				// NOTE for syntax here is optimized to relieve the garbage
				// collector on some virtual machines (e.g. Android / Dalvik)
				int size = this.children.size();
				for (int i = 0; i < size; i++) {
					DigitalInk child = this.children.get(i);
					// this requires that the assumption 
					// child.timeFrame()[0] <= child.timeFrame()[1] holds, i.e.
					// children need proper time frames
					this.tf[0] = (child.timeFrame()[0] < this.tf[0]) ?
							child.timeFrame()[0] : this.tf[0];
					this.tf[1] = (child.timeFrame()[1] > this.tf[1]) ?
							child.timeFrame()[1] : this.tf[1];
				}
			}
			return this.tf;
		}
	}

	/**
	 * Method to recompute the path length. This will cause a traversal over
	 * all children of this structure. As a side effect of calling this method the
	 * path length of this trace will be changed.
	 * 
	 * @return a double representing the combined path length of all children
	 */
	protected double computePathLength() {
		synchronized (lock) {
			if (this.children.isEmpty()) {
				this.pathLength = 0.0;
			}
			else {
				this.pathLength = 0.0;

				// NOTE for syntax here is optimized to relieve the garbage
				// collector on some virtual machines (e.g. Android / Dalvik)
				int size = this.children.size();
				for (int i = 0; i < size; i++) 
					this.pathLength += this.children.get(i).pathLength();
			}
			return this.pathLength;
		}
	}
	
	/**
	 * Remove a child from this structure. This will traverse the structure
	 * several times, so it should be only used if absolutely necessary.
	 * 
	 * @param d the digital ink to remove
	 * @return 
	 */
	public void remove(DigitalInk d) {
		synchronized (lock) {
			children.remove(children.indexOf(d));
		}
		this.computeBoundingBox();
		this.computeTimeFrame();
		this.computePathLength();
	}

	/**
	 * Add a child to this structure.
	 * 
	 * @param d the digital ink to remove
	 * @return 
	 */
	public boolean add(DigitalInk d) {
		synchronized (lock) {
			boolean success = children.add(d);
			if (success) 
				if (this.children.size() == 1) {
					this.bb.init(d.boundingBox());
					this.tf[0] = d.timeFrame()[0];
					this.tf[1] = d.timeFrame()[1];
					this.pathLength = d.pathLength();
				}
				else {
					this.bb.join(d.boundingBox());
					this.tf[0] = (d.timeFrame()[0] < this.tf[0]) ?
						d.timeFrame()[0] : this.tf[0];
					this.tf[1] = (d.timeFrame()[1] > this.tf[1]) ?
						d.timeFrame()[1] : this.tf[1];
					this.pathLength += d.pathLength();
				}
			return success;
		}
	}
	
	// INTERFACE METHODS

	@Override
	public long[] timeFrame() {
		return this.tf;
	}

	@Override
	public Rectangle2d boundingBox() {
		return this.bb;
	}

	@Override
	public double pathLength() {
		return this.pathLength;
	}
}
