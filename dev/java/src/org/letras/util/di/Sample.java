/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.letras.psi.ipen.PenSample;
import org.letras.psi.iregion.RegionSample;
import org.letras.util.geom.IVector2d;

/**
 * Represents a sample in the tree-like digital ink structure. Samples are
 * streamed by digital pens and contain at least the following information
 * <ul>
 *  <li> x,y position (double)
 *  <li> timestamp (long value)
 *  <li> force at the pen tip (byte)
 * </ul>
 * Samples are mainly wrappers around {@link PenSample} or {@link RegionSample}, 
 * for which concrete implementations can be found in the subclasses. However, 
 * {@link Sample} adds the {@link IVector2d} interface as decorator. 
 * <p>
 * The coordinate values stored in the x and y coordinates depend on the wrapped
 * sample class. In case of {@link RegionSample}, coordinates are given in
 * normalized region coordinates (NRC), in case of {@link PenSample} in pattern
 * space coordinates (PSC).
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public abstract class Sample implements IVector2d {

	/**
	 * Get the x coordinate of the sample.
	 * 
	 * @return x coordinate
	 */
	public abstract double getX();

	/**
	 * Set the x coordinate of the sample.
	 * 
	 * @param x coordinate
	 */
	public abstract void setX(double x);

	/**
	 * Get the y coordinate of the sample.
	 * 
	 * @return y coordinate
	 */
	public abstract double getY();

	/**
	 * Set the y coordinate of the sample.
	 * 
	 * @param y coordinate
	 */
	public abstract void setY(double y);

	/**
	 * Get the force of the sample.
	 * 
	 * @return force
	 */
	public abstract int getForce();

	/**
	 * Get the timestamp of the sample.
	 * 
	 * @return 
	 */
	public abstract long getTimestamp();
	
	/**
	 * Returns the string name of the wrapped sample type.
	 * 
	 * @return canonical name of the wrapped sample type
	 */
	public abstract String sampleType();
}
