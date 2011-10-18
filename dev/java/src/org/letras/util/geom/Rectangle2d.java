/*
 * Build by TU Darmstadt 2010, all rights reserved.
 */

package org.letras.util.geom;

/**
 * A simple rectangle geometry class that does not depend on the AWT classes.
 *
 * @author felix_h
 */
public class Rectangle2d {

	public double x;
	public double y;
	public double w;
	public double h;

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	// constructors

	public Rectangle2d(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public Rectangle2d() {
		this.x = 0.0;
		this.y = 0.0;
		this.w = 0.0;
		this.h = 0.0;
	}

	// methods

	/**
	 * Method used to init the this rectangle to the specified rectangle.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void init(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
	}

	/**
	 * Method used to init this rectangle to the values contained within the
	 * other rectangle.
	 * 
	 * @param r
	 */
	public void init(Rectangle2d r) {
		this.init(r.x, r.y, r.w, r.h);
	}

	/**
	 * Returns <code>true</code> iff the provided point is inside the rectangle
	 * or lying on its borders.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(double x, double y) {
		return (((this.x <= x)&&(x <= (this.x + w))) &&
				((this.y <= y)&&(y <= (this.y + h))));
	}

	/**
	 * Returns <code>true</code> iff the provided point is inside the rectangle
	 * or lying on its borders.
	 *
	 * @param v
	 * @return
	 */
	public boolean contains(IVector2d v) {
		assert (v!=null);
		return contains(v.getX(), v.getY());
	}

	/**
	 * This computes the center point of the rectangle 2d. The result will
	 * be stored in a new {@link Vector2d}.
	 *
	 * @return a {@link Vector2d} describing the center point
	 */
	public Vector2d ncenter() {
		return new Vector2d(this.x + (this.w / 2.0), this.y + (this.h / 2.0));
	}

	/**
	 * This computes the center point of the rectangle 2d. The result will
	 * be stored in the provided {@link Vector2d}.
	 *
	 * @return a {@link Vector2d} describing the center point
	 */
	public IVector2d center(IVector2d v) {
		v.setX(this.x + (this.w / 2.0));
		v.setY(this.y + (this.h / 2.0));
		return v;
	}
	
	/**
	 * Inserts a new point into this rectangle, such that the resulting
	 * rectangle will provide the smallest axis aligned bounding box enclosing
	 * this rectangle and the new point.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Rectangle2d insert(double x, double y) {
		double xmin = (x < this.x) ? x : this.x;
		double ymin = (y < this.y) ? y : this.y;
		double xmax = (x > this.x + w) ? x : this.x + w;
		double ymax = (y > this.y + h) ? y : this.y + h;
		this.x = xmin;
		this.y = ymin;
		this.w = xmax - xmin;
		this.h = ymax - ymin;
		return this;
	}

	/**
	 * Inserts a new point into this rectangle, such that the resulting
	 * rectangle will provide the smallest axis aligned bounding box enclosing
	 * this rectangle and the new point. The result is a new rectangle.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Rectangle2d ninsert(double x, double y) {
		double xmin = (x < this.x) ? x : this.x;
		double ymin = (y < this.y) ? y : this.y;
		double xmax = (x > this.x + w) ? x : this.x + w;
		double ymax = (y > this.y + h) ? y : this.y + h;
		return new Rectangle2d(xmin, ymin, xmax - xmin, ymax - ymin);
	}

	/**
	 * Inserts a new point into this rectangle, such that the resulting
	 * rectangle will provide the smallest axis aligned bounding box enclosing
	 * this rectangle and the new point.
	 *
	 * @param v
	 * @return
	 */
	public Rectangle2d insert(IVector2d v) {
		return this.insert(v.getX(), v.getY());
	}

	/**
	 * Inserts a new point into this rectangle, such that the resulting
	 * rectangle will provide the smallest axis aligned bounding box enclosing
	 * this rectangle and the new point. The result is a new rectangle.
	 *
	 * @param v
	 * @return
	 */
	public Rectangle2d ninsert(IVector2d v) {
		return this.ninsert(v.getX(), v.getY());
	}

	/**
	 * Joins this rectangle and the passed rectangle into the smallest axis
	 * aligned rectangle enclosing both rectangles, storing the result in this
	 * rectangle.
	 *
	 * @param r
	 * @return
	 */
	public Rectangle2d join(Rectangle2d r) {
		assert (r!=null);
		double xmin = (r.x < this.x) ? r.x : this.x;
		double ymin = (r.y < this.y) ? r.y : this.y;
		double xmax = (this.x + this.w < r.x + r.w ) ? r.x + r.w : this.x + this.w;
		double ymax = (this.y + this.h < r.y + r.h ) ? r.y + r.h : this.y + this.h;
		this.x = xmin;
		this.y = ymin;
		this.w = xmax - xmin;
		this.h = ymax - ymin;
		return this;
	}

	/**
	 * Joins this rectangle and the passed rectangle into the smallest axis
	 * aligned rectangle enclosing both rectangles, storing the result into a
	 * new rectangle.
	 *
	 * @param r
	 * @return
	 */
	public Rectangle2d njoin(Rectangle2d r) {
		assert (r!=null);
		double xmin = (r.x < this.x) ? r.x : this.x;
		double ymin = (r.y < this.y) ? r.y : this.y;
		double xmax = (this.x + this.w < r.x + r.w ) ? r.x + r.w : this.x + this.w;
		double ymax = (this.y + this.h < r.y + r.h ) ? r.y + r.h : this.y + this.h;
		return new Rectangle2d(xmin, ymin, xmax - xmin, ymax - ymin);
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null)||!(obj instanceof Rectangle2d)) return false;
		else {
			Rectangle2d r = (Rectangle2d) obj;
			return (this.x == r.x) && (this.y == r.y) &&
					(this.w == r.w) && (this.h == r.h);
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 73 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 73 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		hash = 73 * hash + (int) (Double.doubleToLongBits(this.w) ^ (Double.doubleToLongBits(this.w) >>> 32));
		hash = 73 * hash + (int) (Double.doubleToLongBits(this.h) ^ (Double.doubleToLongBits(this.h) >>> 32));
		return hash;
	}
}
