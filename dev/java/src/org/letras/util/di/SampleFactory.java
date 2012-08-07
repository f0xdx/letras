/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.letras.api.pen.PenSample;
import org.letras.api.region.RegionSample;

/**
 * Factory method to construct the appropriate type of sample out of some object.
 * The object thereby can be either a {@link PenSample Pen-} or a 
 * {@link RegionSample}.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public class SampleFactory {
	
	/**
	 * Factory method that constructs a sample out of the proided object. The
	 * object can thereby consist of either a {@link PenSample} or a 
	 * {@link RegionSample}. All other objects cannot be transformed in a sample
	 * and thus will result in an {@link UnsupportedOperationException}.
	 * 
	 * @param o an object to uses as a generic sample
	 * @return appropriate {@link Sample} implementation for the provided object
	 */
	public static Sample createSample(Object o) 
			throws UnsupportedOperationException {
		if (o instanceof PenSample) 
			return new PenSampleWrapper((PenSample) o);
		else if (o instanceof RegionSample) 
			return new RegionSampleWrapper((RegionSample) o);
		else
			throw new UnsupportedOperationException("no wrapper for this type available");
	}
}
