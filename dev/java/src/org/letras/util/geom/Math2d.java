/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.geom;

/**
 * Selection of mathematical operations on 2 dimensional vectors.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 */
public class Math2d {

	/**
	 * Adds the first provided vector (v) to the second vector (u) storing the result
	 * in the third vector (r).
	 */
	public static IVector2d add(IVector2d v, IVector2d u, IVector2d r) {
		return add(v.getX(), v.getY(), u.getX(), u.getY(), r);
	}
	
	/**
	 * Adds the first provided vector (v) to this vector storing the result
	 * in the second provided vector (r).
	 */
	public static IVector2d add(double vx, double vy, double ux, double uy, IVector2d r) {
		r.setX(vx + ux);
		r.setY(vy + uy);
		return r;
	}

	/**
	 * Adds the first provided vector (v) to the second vector (u) storing the result
	 * in a new {@link Vector2d}.
	 */
	public static Vector2d nadd(IVector2d v, IVector2d u) {
		return nadd(v.getX(), v.getY(), u.getX(), u.getY());
	}
	
	/**
	 * Adds the first provided vector (v) to this vector storing the result
	 * in the second provided vector (r).
	 */
	public static Vector2d nadd(double vx, double vy, double ux, double uy) {
		return (Vector2d) add(vx, vy, ux, uy, new Vector2d());
	}
	
	/**
	 * Adds the first provided vector (v) to the second vector (u) storing the result
	 * in the third vector (r).
	 */
	public static IVector2d sub(IVector2d v, IVector2d u, IVector2d r) {
		return sub(v.getX(), v.getY(), u.getX(), u.getY(), r);
	}
	
	/**
	 * Adds the first provided vector (v) to this vector storing the result
	 * in the second provided vector (r).
	 */
	public static IVector2d sub(double vx, double vy, double ux, double uy, IVector2d r) {
		r.setX(vx - ux);
		r.setY(vy - uy);
		return r;
	}

	/**
	 * Adds the first provided vector (v) to the second vector (u) storing the result
	 * in a new {@link Vector2d}.
	 */
	public static Vector2d nsub(IVector2d v, IVector2d u) {
		return nsub(v.getX(), v.getY(), u.getX(), u.getY());
	}
	
	/**
	 * Adds the first provided vector (v) to this vector storing the result
	 * in the second provided vector (r).
	 */
	public static Vector2d nsub(double vx, double vy, double ux, double uy) {
		return (Vector2d) sub(vx, vy, ux, uy, new Vector2d());
	}
	
	/**
	 * Scalar product of scalar a with vector v storing the result in vector r.
	 */
	public static IVector2d scmult(double a, IVector2d v, IVector2d r) {
		return scmult(a, v.getX(), v.getY(), r);
	}
	
	/**
	 * Scalar product of scalar a with vector v storing the result in vector r.
	 */
	public static IVector2d scmult(double a, double vx, double vy, IVector2d r) {
		r.setX(a * vx);
		r.setY(a * vy);
		return r;
	}
	
	/**
	 * Scalar product of scalar a with vector v storing the result in a new vector.
	 */
	public static Vector2d nscmult(double a, IVector2d v) {
		return nscmult(a, v.getX(), v.getY());
	}
	
	/**
	 * Scalar product of scalar a with vector v storing the result in vector r.
	 */
	public static Vector2d nscmult(double a, double vx, double vy) {
		return (Vector2d) scmult(a, vx, vy, new Vector2d());
	}

	/**
	 * Returns the distance between the {@link IVector2d} v and
	 * u.
	 */
	public static double distance(IVector2d v, IVector2d u) {
		return Math.sqrt(distanceSq(v, u));
	}
	
	/**
	 * Returns the distance between the {@link Vector2d} v and
	 * u.
	 */
	public double distance(double vx, double vy, double ux, double uy) {
		return Math.sqrt(distanceSq(vx, vy, ux, uy));
	}
	
	/**
	 * Returns the squared distance between {@link Vector2d} u and
	 * v.
	 */
	public static double distanceSq(IVector2d v, IVector2d u) {
		return distanceSq(v.getX(), v.getY(), u.getX(), u.getY());
	}
	
	/**
	 * Returns the squared distance between the {@link IVector2d} v and
	 * u.
	 */
	public static double distanceSq(double vx, double vy, double ux, double uy) {
		return ((ux - vx) * (ux - vx)) + ((uy - vy) * (uy - vy));
	}
	
	/**
	 * Computes the (euclidean) norm of the {@link IVector2d}.
	 * 
	 * @return
	 */
	public static double norm(IVector2d v) {
		return Math.sqrt(normSq(v.getX(), v.getY()));
	}
	
	/**
	 * Returns the (euclidean) norm of the provided vector.
	 * @param vx
	 * @param vy
	 * @return 
	 */
	public static double norm(double vx, double vy) {
		return Math.sqrt(normSq(vx, vy));
	}

	/**
	 * Computes the squared (euclidean) norm of the {@link IVector2d}.
	 * @return
	 */
	public static double normSq(IVector2d v) {
		return (normSq(v.getX(), v.getY()));
	}		

	/**
	 * Computes the squared (euclidean) norm of the {@link IVector2d}.
	 * @return
	 */
	public static double normSq(double vx, double vy) {
		return (vx * vx) + (vy * vy);
	}

	/*
	 * Computes the euclidean (dot) product of the provided {@link IVector2d} v
	 * and u.
	 */
	public static double dot(IVector2d v, IVector2d u) {
		return (v.getX() * u.getX()) + (v.getY() * u.getY());
	}

	/*
	 * Computes the euclidean (dot) product of the {@link IVector2d} v and u.
	 */
	public static double dot(double vx, double vy, double ux, double uy) {
		return (vx * ux) + (vy * uy);
	}
}
