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

import org.letras.ps.region.RegionTreeNode;

/**
 * A bounding box for any {@link IShape} in the {@link RegionTreeNode} hierarchy.
 * @author Jannik Jochem
 *
 */
public class Bounds {
	
	private double x;
	private double y;
	private double width;
	private double height;
	
	/**
	 * Creates a new bounding box.
	 * 
	 * @param x the leftmost edge of the bounding box
	 * @param y the topmost edge of the bounding box
	 * @param width the width of the bounding box
	 * @param height the height of the bounding box
	 */
	public Bounds(double x, double y, double width, double height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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

	/**
	 * @param x2
	 * @param y2
	 * @return true iff the point (x2,y2) is (weakly) contained in this Bounds.
	 */
	public boolean contains(double x2, double y2) {
		return x <= x2 && x2 <= x + width && y <= y2 && y2 <= y2 + height;
	}

	/**
	 * @param bounds
	 * @return true iff bounds is entirely contained (weakly) in this Bounds.
	 */
	public boolean contains(Bounds bounds) {
		return x <= bounds.x && bounds.x + bounds.width <= x + width
			&& y <= bounds.y && bounds.y + bounds.height <= y + height;
	}
	
	/**
	 * @param bounds
	 * @return true iff bounds (strongly) intersects this Bounds.
	 */
	public boolean intersects(Bounds bounds) {
		return !(x >= bounds.x + bounds.width || x + width <= bounds.x ||
				y >= bounds.y + bounds.height || y + height <= bounds.y);

	}
	
	/**
	 * @param bounds
	 * @return true iff bounds intersects this bounds and neither is (weakly) contained in the other.
	 */
	public boolean strictIntersects(Bounds bounds) {
		return intersects(bounds) && !contains(bounds) &&!bounds.contains(this);
	}

	/**
	 * @param bounds1
	 * @param bounds2
	 * @return the smallest Bounds that (weakly) contains both bounds1 and bounds2
	 */
	public static Bounds union(Bounds bounds1, Bounds bounds2) {
		double left = Math.min(bounds1.x, bounds2.x);
		double top = Math.min(bounds1.y, bounds2.y);
		double right = Math.max(bounds1.x + bounds1.width, bounds2.x + bounds2.width);
		double bottom = Math.max(bounds1.y + bounds1.height, bounds2.y + bounds2.height);
		return new Bounds(left, top, right - left, bottom - top);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Bounds))
			return false;
		Bounds other = (Bounds) obj;
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
