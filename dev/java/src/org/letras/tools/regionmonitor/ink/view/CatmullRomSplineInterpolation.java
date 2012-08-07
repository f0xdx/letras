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
package org.letras.tools.regionmonitor.ink.view;

import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

import org.letras.api.region.RegionSample;
import org.letras.tools.regionmonitor.ink.model.Stroke;

/**
 * This provides a Catmull-Rom cubic B-spline interpolation.
 * 
 * @author felix_h
 * @version 0.1
 */
public class CatmullRomSplineInterpolation implements IInterpolation {

	// defaults
	
	private static final double DEFAULT_TAU = 0.333;
	
	// members
	
	// To avoid creating a new curve each time the method is called,
	// only a single curve is used and adapted upon update
	private CubicCurve2D.Double curve;
	
	private Point2D.Double s1;
	private Point2D.Double p;
	private Point2D.Double q;
	private Point2D.Double s2;
	
	private double tau;

	/**
	 * @return the tau
	 */
	public double getTau() {
		return tau;
	}

	/**
	 * @param tau the tau to set
	 */
	public void setTau(double tau) {
		this.tau = tau;
	}

	// constructors

	public CatmullRomSplineInterpolation() {
		this.curve =  new CubicCurve2D.Double();
		this.p = new Point2D.Double();
		this.s1 = new Point2D.Double();
		this.s2 = new Point2D.Double();
		this.q = new Point2D.Double();
		this.tau = DEFAULT_TAU;
	}
	
	public CatmullRomSplineInterpolation(double tau) {
		this();
		this.tau = tau;
	}
	
	// methods

	/**
	 * Overwritten method from {@link IInterpolation#interpolate(Stroke, Sample, Sample, double, double)}.
	 */
	@Override
	public Shape interpolate(Stroke stroke, RegionSample s, RegionSample t, double width,
			double height) {
		assert ((stroke!=null)&&(s!=null)&&(t!=null));

		// NOTE in the following it will be assumed that s and t are adjacent in the samples
		// list, and that s has the lower index in that list
		assert (stroke.samples().indexOf(s)==stroke.samples().indexOf(t)-1);
		
		// NOTE this involves a complete iteration over the vector. If we need faster performance,
		// skip the iteration by providing the index of s
		int i = stroke.samples().indexOf(s);
		assert (i>-1) : "s must be in list";
		
		// NOTE the coordinates used for computation are given in normalized region coordinates,
		// to compute the real coordinates transform scale them (not necessarily uniformly)
		
		s1.x = s.getX(); s1.y = s.getY();
		s2.x = t.getX(); s2.y = t.getY();
		
		// formula: p = s1 + tau(s2 - s0), if no s0 available (first point in stroke is s), use s2-s1
		if (i < 1) {
			p.x = s1.x + tau * (s2.x - s1.x);
			p.y = s1.y + tau * (s2.y - s1.y);
		}
		else {
			p.x = s1.x + tau * (s2.x - stroke.samples().get(i - 1).getX());
			p.y = s1.y + tau * (s2.y - stroke.samples().get(i - 1).getY());
		}
		
		// formula: q = s2 - tau(s3 - s1), if no s3 available (last point in stroke is t), use s2-s1
		if (i > stroke.samples().size() - 3) {
			q.x = s2.x - tau * (s2.x - s1.x);
			q.y = s2.y - tau * (s2.y - s1.y);
		}
		else {
			q.x = s2.x - tau * (stroke.samples().get(i + 2).getX() - s1.x);
			q.y = s2.y - tau * (stroke.samples().get(i + 2).getY() - s1.y);
		}
		
		curve.setCurve(
				s1.x * width, s1.y * height, 
				p.x * width, p.y * height, 
				q.x * width, q.y * height, 
				s2.x * width, s2.y * height);
		
		return curve;
	}

}
