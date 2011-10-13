/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.geom;

/**
 * Interface for 2-dimensional vectors. This can be used in order to perform some
 * platform independent geometry computation.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 */
public interface IVector2d {

	/**
	 * Returns the x component of this vector.
	 * 
	 * @return 
	 */
	public double getX();

	/**
	 * Returns the y component of this vector.
	 * 
	 * @return 
	 */
	public double getY();


	/**
	 * Sets the x component of this vector.
	 * @param x 
	 */
	public void setX(double x);

	/**
	 * Sets the y component of this vector.
	 * 
	 * @param y 
	 */
	public void setY(double y);
}
