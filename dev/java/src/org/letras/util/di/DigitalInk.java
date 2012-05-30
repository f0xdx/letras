/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.letras.util.geom.Rectangle2d;

/**
 * Abstract base class of the digital ink tree-like structure. This provides
 * the abstract component definition of the employed composite pattern. 
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public abstract class DigitalInk {
	
	/**
	 * Provides the time frame in which this digital ink has been recorded. The
	 * result is a 2 element vector of long values, where the first element 
	 * corresponds to the point in time, where this digital ink started and the
	 * second element to the point in time where it ended.
	 * <p>
	 * Note that for reasons of efficient object use, implementing classes may 
	 * choose to provide the same array object on each method call. Therefore the
	 * provided array should be regarded as immutable, i.e. its values must not
	 * be changed by the caller.
	 * 
	 * @return start and end point of the time frame in an immutable 2 element
	 * array
	 */
	public abstract long[] timeFrame();

	/**
	 * Provides the duration of this digital ink structure.
	 * 
	 * @return 
	 */
	public long duration() {
		long[] timeFrame = this.timeFrame();
		return timeFrame[1] - timeFrame[0];
	}

	/**
	 * Provides the bounding box of the recorded digital ink. Note that for 
	 * reasons of efficient object use, implementing classes may choose to 
	 * provide the same {@link Rectangle2d} object on each method call. 
	 * Therefore the provided object should be regarded as immutable, i.e. its 
	 * values must not be changed by the caller.
	 * 
	 * @return bounding box of the digital ink structure
	 */
	public abstract Rectangle2d boundingBox();

	/**
	 * Returns the combined length of the path of this structure.
	 * 
	 * @return combined length of the path(s) forming this structure
	 */
	public abstract double pathLength();
}
