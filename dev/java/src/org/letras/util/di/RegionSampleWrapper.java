/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.letras.psi.iregion.RegionSample;

/**
 * Sample that wraps a {@link RegionSample}. The coordinates (x and y) of this
 * sample are provided in normalized region coordinates (NRC) if the flag nrc
 * is set, otherwise pattern space coordinates (PSC) are provided. Note that the
 * NRC typically undergo a non-uniform scaling.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public class RegionSampleWrapper extends Sample {

	// members

	private RegionSample sample;
    private boolean nrc;

    // getters & setters

    public boolean isNrc() {
        return nrc;
    }

    public void setNrc(boolean nrc) {
        this.nrc = nrc;
    }

	// constructors

	public RegionSampleWrapper(RegionSample sample) {
		this.sample = sample;
        this.nrc = true;
	}

	// interface methods

	@Override
	public double getX() {
		return (this.nrc) ? this.sample.getX() : this.sample.getPscX();
	}

	@Override
	public void setX(double x) {
		if (this.nrc) this.sample.setX(x);
        else this.sample.setPscX(x);
	}

	@Override
	public double getY() {
		return (this.nrc) ? this.sample.getY() : this.sample.getPscY();
	}

	@Override
	public void setY(double y) {
		if (this.nrc) this.sample.setY(y);
        else this.sample.setPscY(y);
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

	public RegionSample getOriginalSample() {
		return this.sample;
	}
}
