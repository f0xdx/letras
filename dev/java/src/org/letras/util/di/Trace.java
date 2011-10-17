/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import java.util.ArrayList;
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

	private ArrayList<Sample> samples;
	private Rectangle2d bb;
	private long[] tf;

	// getters & setters

	public ArrayList<Sample> getSamples() {
		return samples;
	}

	public void setSamples(ArrayList<Sample> samples) {
		this.samples = samples;
	}

	// constructors
	
	public Trace() {
		this.samples = new ArrayList<Sample>(DEFAULT_CAPACITY);
	}

	// interface methods

	@Override
	public long[] timeFrame() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Rectangle2d boundingBox() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	// methods

	/**
	 * Returns the size of this trace in samples.
	 * 
	 * @return 
	 */
	public int size() {
		return samples.size();
	}

	/**
	 * Remove a sample from this trace.
	 * 
	 * @param s 
	 * @return 
	 */
	public boolean remove(Sample s) {
		return samples.remove(s);

		// TODO adapt the bounding box and time frame
	}

	public boolean add(Sample e) {
		return samples.add(e);

		// TODO adapt the bounding box and time frame
	}

}