/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import java.util.ArrayList;
import org.letras.util.geom.Math2d;
import org.letras.util.geom.Rectangle2d;

/**
 * Represents a  trace in the tree-like digital ink data structure. This is
 * one level above the leaves and encompasses a series of successively streamed
 * {@link Sample Samples}. A trace contains the following basic information
 * <ul>
 *  <li> bounding box ({@link Rectangle2d})
 *  <li> time frame (2 element long[]: start time, end time)
 *  <li> samples ({@link ArrayList} of type {@link Sample})
 * </ul>
 * Traces are typically started when the pen is set onto a surface and end either
 * when the pen crosses an interactive region boundary, or when it is lifted
 * from the surface.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public class Trace extends DigitalInk {

	// defaults

	private static final int DEFAULT_CAPACITY = 16;

	//members

	private final Object lock = new Object();

	private ArrayList<Sample> samples;
	private Rectangle2d bb;
	private long[] tf;
	private double pathLength;

	// getters & setters

	/**
	 * Get the samples of this trace. The returned {@link ArrayList} is considered
	 * to be immutable, i.e. it must not be changed by the caller. 
	 * 
	 * @return list of sample forming this trace
	 */
	public ArrayList<Sample> getSamples() {
		return samples;
	}

	public void setSamples(ArrayList<Sample> samples) {
		this.samples = samples;
		this.computeBoundingBox();
		this.computeTimeFrame();
		this.computePathLength();
	}
	
	// constructors
	
	public Trace() {
		this.samples = new ArrayList<Sample>(DEFAULT_CAPACITY);
		this.tf = new long[2];
		this.bb = new Rectangle2d();
		this.pathLength = 0.0;
	}

	// interface methods

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

	// methods

	/**
	 * Method to recompute the bounding box. This will cause a traversal over
	 * all samples of this trace. As a side effect of calling this method, the
	 * bounding box {@link Rectangle2d} will be changed.
	 * 
	 * @return the bounding box of this trace
	 */
	protected Rectangle2d computeBoundingBox() {
		synchronized (lock) {
			if (this.samples.isEmpty()) {
				this.bb.init(0.0, 0.0, 0.0, 0.0);
			}
			else {
				double xmin = this.samples.get(0).getX();
				double ymin = this.samples.get(0).getY();
				double xmax = this.samples.get(0).getX();
				double ymax = this.samples.get(0).getY();

				// NOTE for syntax here is optimized to relieve the garbage
				// collector on some virtual machines (e.g. Android / Dalvik)

				int size = this.samples.size();
				for (int i = 1; i < size; i++) {
					Sample s = this.samples.get(i);
					xmin = (s.getX() < xmin) ? s.getX() : xmin;
					ymin = (s.getY() < ymin) ? s.getY() : ymin;
					xmax = (s.getX() > xmax) ? s.getX() : xmax;
					ymax = (s.getY() > ymax) ? s.getY() : ymax;
				}
				this.bb.init(xmin, ymin, xmax - xmin, ymax - ymin);
			}
			return this.bb;
		}
	}

	/**
	 * Method to recompute the time frame. This will cause a traversal over
	 * all samples of this trace. As a side effect of calling this method the
	 * time frame array will be changed.
	 * 
	 * @return a 2-element array containing the start and end point of the time
	 * frame
	 */
	protected long[] computeTimeFrame() {
		synchronized (lock) {
			if (this.samples.isEmpty()) {
				this.tf[0] = 0l;
				this.tf[1] = 0l;
			}
			else {
				long tmin = this.samples.get(0).getTimestamp();
				long tmax = this.samples.get(0).getTimestamp();

				// NOTE for syntax here is optimized to relieve the garbage
				// collector on some virtual machines (e.g. Android / Dalvik)

				int size = this.samples.size();
				for (int i = 1; i < size; i++) {
					Sample s = this.samples.get(i);
					tmin = (s.getTimestamp() < tmin) ? s.getTimestamp() : tmin;
					tmax = (s.getTimestamp() > tmax) ? s.getTimestamp() : tmax;
				}
				this.tf[0] = tmin;
				this.tf[1] = tmax;
			}
			return this.tf;
		}
	}

	/**
	 * Method to recompute the path length. This will cause a traversal over
	 * all samples of this trace. As a side effect of calling this method the
	 * path length of this trace will be changed.
	 * 
	 * @return a double representing the path length obtained by linear 
	 * interpolation between samples
	 */
	protected double computePathLength() {
		synchronized (lock) {
			if (this.samples.isEmpty()) {
				this.pathLength = 0.0;
			}
			else {
				this.pathLength = 0.0;

				// NOTE for syntax here is optimized to relieve the garbage
				// collector on some virtual machines (e.g. Android / Dalvik)
				int size = this.samples.size() - 1;
				for (int i = 0; i < size; i++) {
					Sample s = this.samples.get(i);
					Sample t = this.samples.get(i + 1);
					this.pathLength += Math2d.distance(s, t);
				}
			}
			return this.pathLength;
		}
	}
	
	/**
	 * Returns the size of this trace in samples.
	 * 
	 * @return 
	 */
	public int size() {
		return samples.size();
	}

	/**
	 * Add a new sample to this trace. This will adapt the bounding box, the
	 * time frame and the path length on the fly.
	 * 
	 * @param s
	 * @return 
	 */
	public boolean add(Sample s) {
		synchronized (lock) {
			boolean success = samples.add(s);
			if (success) 
				if (this.samples.size() == 1) {
					this.bb.init(s.getX(), s.getY(), 0.0, 0.0);
					this.tf[0] = this.tf[1] = s.getTimestamp();
					this.pathLength = 0.0;
				}
				else {
					this.bb.insert(s);
					this.tf[0] = (s.getTimestamp() < this.tf[0]) ? 
							s.getTimestamp() : this.tf[0];
					this.pathLength += 
							Math2d.distance(this.samples.get(this.samples.size() - 2), s);
				}
			return success;
		}
	}
}