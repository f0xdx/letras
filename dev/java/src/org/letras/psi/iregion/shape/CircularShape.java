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
package org.letras.psi.iregion.shape;




/**
 * A circle shape.
 * 
 * @author Jannik Jochem
 *
 */
public class CircularShape implements IShape {
	
	// mundo requires at least protected visibility for serialization
	protected double x;
	protected double y;
	protected double radius;
	
	// constructors
	
	/**
	 * No-argument constructor for serialization. Note that this constructor
	 * does not initialize the class members with meaningful values. It should
	 * be used by the serialization mechanism ONLY, use the constructor taking
	 * values for the class members instead. 
	 */
	public CircularShape() {
		this.x = 0.0;
		this.y = 0.0;
		this.radius = 0.0;
	}
	
	/**
	 * Creates a new circle.
	 * @param x the horizontal coordinate of the circle's center
	 * @param y the vertical coordinate of the circle's center
	 * @param radius the radius of the circle
	 */
	public CircularShape(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	@Override
	public boolean contains(double x, double y) {
		return Math.sqrt((x-this.x)*(x-this.x) + (y - this.y)*(y - this.y)) <= radius;
	}

	@Override
	public Bounds getBounds() {
		return new Bounds(x - radius, y - radius, 2 * radius, 2 * radius);
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getRadius() {
		return radius;
	}

	@Override
	public String toString() {
		return "circle([" + x + "," + y + "]," + radius + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(radius);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CircularShape other = (CircularShape) obj;
		if (Double.doubleToLongBits(radius) != Double
				.doubleToLongBits(other.radius))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
}
