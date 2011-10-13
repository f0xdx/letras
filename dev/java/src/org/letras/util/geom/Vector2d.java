package org.letras.util.geom;

import org.letras.psi.ipen.PenSample;
import org.letras.psi.iregion.RegionSample;

/**
 * A simple vector class not based on the AWT geom stuff.
 * 
 * @author felix_h
 *
 */
public class Vector2d implements IVector2d {
	
	// MEMBERS

	public double x;
	public double y;

	
	// CONSTRUCTORS 

	public Vector2d() {
		this.x = 0.0;
		this.y = 0.0;
	}
	
	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor to allow creating a vector out of a {@link PenSample}. 
	 * 
	 * @param s 
	 */
	public Vector2d(PenSample s) {
		this.x = s.getX();
		this.y = s.getY();
	}

	/**
	 * Constructor to allow creating a vector out of a {@link RegionSample}. This
	 * will use the normalized region coordinates as values of this vector.
	 * 
	 * @param s 
	 */
	public Vector2d(RegionSample s) {
		this.x = s.getX();
		this.y = s.getY();
	}
	
	
	// METHODS

	/**
	 * Initializes this vector to the values provided.
	 *
	 * @param x
	 * @param y
	 */
	public void init(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Initializes this vector to the values contained within the provided
	 * one.
	 *
	 * @param v
	 */
	public void init(Vector2d v) {
		this.init(v.x, v.y);
	}

	/**
	 * Adds the provided vector to this vector. Note that
	 * this will change the x and y values of this vector
	 * as a result.
	 */
	public Vector2d add(Vector2d v) {
		return this.add(v.x, v.y);
	}
	
	/**
	 * Adds the provided vector to this vector. Note that
	 * this will change the x and y values of this vector
	 * as a result.
	 */
	public Vector2d add(double vx, double vy) {
		this.x += vx;
		this.y += vy;
		return this;
	}
	
	/**
	 * Adds the first provided vector (v) to this vector storing the result
	 * in the second provided vector (r).
	 */
	public Vector2d add(Vector2d v, Vector2d r) {
		return this.add(v.x, v.y, r);
	}
	
	/**
	 * Adds the first provided vector (v) to this vector storing the result
	 * in the second provided vector (r).
	 */
	public Vector2d add(double vx, double vy, Vector2d r) {
		r.x = this.x + vx;
		r.y = this.y + vy;
		return r;
	}

	/**
	 * Subtracts the provided vector from this vector. Note that
	 * this will change the x and y values of this vector
	 * as a result.
	 */
	public Vector2d sub(Vector2d v) {
		return this.sub(v.x, v.y);
	}
	
	/**
	 * Subtracts the provided vector form this vector. Note that
	 * this will change the x and y values of this vector
	 * as a result.
	 */
	public Vector2d sub(double vx, double vy) {
		this.x -= vx;
		this.y -= vy;
		return this;
	}
	
	/**
	 * Subtracts the first provided vector (v) from this vector storing the
	 * result in the second provided vector (r).
	 */
	public Vector2d sub(Vector2d v, Vector2d r) {
		return this.sub(v.x, v.y, r);
	}
	
	/**
	 * Subtracts the first provided vector (v) from this vector storing the
	 * result in the second provided vector (r).
	 */
	public Vector2d sub(double vx, double vy, Vector2d r) {
		r.x = this.x - vx;
		r.y = this.y - vy;
		return r;
	}

	/**
	 * Scalar product. Note that
	 * this will change the x and y values of this vector
	 * as a result.
	 */
	public Vector2d scmult(double a) {
		this.x *= a;
		this.y *= a;
		return this;
	}
	
	/**
	 * Scalar product storing the result in the provided vector.
	 */
	public Vector2d scmult(double a, Vector2d r) {
		r.x = this.x * a;
		r.y = this.y * a;
		return r;
	}

	/**
	 * Returns the distance between this {@link Vector2d} and
	 * the one provided.
	 */
	public double distance(Vector2d v) {
		return Math.sqrt(this.distanceSq(v.x, v.y));
	}
	
	/**
	 * Returns the distance between this {@link Vector2d} and
	 * the one provided.
	 */
	public double distance(double vx, double vy) {
		return Math.sqrt(this.distanceSq(vx, vy));
	}
	
	/**
	 * Returns the squared distance between this {@link Vector2d} and
	 * the one provided.
	 */
	public double distanceSq(Vector2d v) {
		return this.distanceSq(v.x, v.y);
	}
	
	/**
	 * Returns the squared distance between this {@link Vector2d} and
	 * the one provided.
	 */
	public double distanceSq(double vx, double vy) {
		return ((vx - this.x) * (vx - this.x)) + ((vy - this.y) * (vy - this.y));
	}
	
	/**
	 * Computes the norm of this vector.
	 * 
	 * @return
	 */
	public double norm() {
		return Math.sqrt(this.normSq());
	}
	
	/**
	 * Computes the squared norm of this vector.
	 * @return
	 */
	public double normSq() {
		return (this.x * this.x) + (this.y * this.y);
	}

	/*
	 * Computes the euclidean (dot) product of this {@link Vector} and the one
	 * provided.
	 */
	public double dot(Vector2d v) {
		return (this.x * v.x) + (this.y * v.y);
	}

	/*
	 * Computes the euclidean (dot) product of this {@link Vector} and the one
	 * provided.
	 */
	public double dot(double vx, double vy) {
		return (this.x * vx) + (this.y * vy);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public static Vector2d fromString(String s) {
		assert (s!=null);
		Vector2d result = new Vector2d();

		// remove leading and trailing whitespaces
		s.trim();
	
		// test if the string conforms to the format
		if (!(s.startsWith("(") && s.endsWith(")"))) throw
				new IllegalArgumentException("wrong Vector2d string format");

		// remove leading and trailing parentheses
		s = s.substring(1, s.length()- 1);

		// extract the values
		String[] doubleValues = s.split(",");
		if (doubleValues.length != 2) throw
				new IllegalArgumentException("wrong Vector2d string format");
		try {
			result.x = Double.parseDouble(doubleValues[0].trim());
			result.y = Double.parseDouble(doubleValues[1].trim());
		}
		catch (Exception e) {
			throw new IllegalArgumentException("wrong Vector2d string format");
		}

		return result;
	}


	// INTERFACE METHODS

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

}
