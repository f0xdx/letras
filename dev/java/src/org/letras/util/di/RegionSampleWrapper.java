/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.letras.psi.iregion.RegionSample;

/**
 * Sample that wraps a {@link RegionSample}. The coordinates (x and y) of this
 * sample are provided in normalized region coordinates (NRC).
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public class RegionSampleWrapper extends Sample {

	// members

	private RegionSample sample;

	// constructors

	public RegionSampleWrapper(RegionSample sample) {
		this.sample = sample;
	}

	// interface methods

	@Override
	public double getX() {
		return this.sample.getX();
	}

	@Override
	public void setX(double x) {
		this.sample.setX(x);
	}

	@Override
	public double getY() {
		return this.sample.getY();
	}

	@Override
	public void setY(double y) {
		this.sample.setY(y);
	}

	@Override
	public int getForce() {
		return this.sample.getForce();
	}

	@Override
	public long getTimestamp() {
		return this.sample.getTimestamp();
	}

	@Override
	public String sampleType() {
		return this.sample.getClass().getName();
	}
}
