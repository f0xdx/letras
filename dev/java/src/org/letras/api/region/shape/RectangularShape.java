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
package org.letras.api.region.shape;

import java.util.Locale;

import org.mundo.annotation.mcSerialize;

/**
 * A rectangle.
 * 
 * @author Jannik Jochem
 *
 */
@mcSerialize
public class RectangularShape implements IShape {
	
	// mundo requires at least protected visibility for serialization
	protected double x;
	protected double y;
	protected double width;
	protected double height;
	
	// constructors
	
	/**
	 * No-argument constructor for serialization. Note that this constructor
	 * does not initialize the class members with meaningful values. It should
	 * be used by the serialization mechanism ONLY, use the constructor taking
	 * values for the class members instead. 
	 */
	public RectangularShape() {
		this.x = 0.0;
		this.y = 0.0;
		this.width = 0.0;
		this.height = 0.0;
	}
	
	/**
	 * Creates a new rectangle that fits perfectly in the bounding box bounds.
	 * @param bounds
	 */
	public RectangularShape(Bounds bounds) {
		x = bounds.getX();
		y = bounds.getY();
		width = bounds.getWidth();
		height = bounds.getHeight();
	}
	
	/**
	 * Creates a new rectangle.
	 * @param x the horizontal coordinate of the top left corner of the rectangle
	 * @param y the vertical coordinate of the top left corner of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 */
	public RectangularShape(double x, double y, double width, double height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean contains(double x, double y) {
		return 
			x >= this.x && 
			x <= (this.x + this.width) && 
			y >= this.y && 
			y <= (this.y + this.height);
	}

	@Override
	public Bounds getBounds() {
		return new Bounds(x, y, width, height);
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "rectangle((%.0f,%.0f), %.0f x %.0f)", x, y, width, height);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(height);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(width);
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
		RectangularShape other = (RectangularShape) obj;
		if (Double.doubleToLongBits(height) != Double
				.doubleToLongBits(other.height))
			return false;
		if (Double.doubleToLongBits(width) != Double
				.doubleToLongBits(other.width))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

}
