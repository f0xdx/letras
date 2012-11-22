package org.letras.psi.ipen;

import org.letras.api.pen.IPenSample;
import org.letras.api.pen.PenSample;
import org.mundo.annotation.mcSerialize;

@mcSerialize
public class MundoPenSample implements IPenSample {

	protected double x;
	protected double y;
	protected int force;
	protected long timestamp;

	public MundoPenSample() {

	}

	public MundoPenSample(double x, double y, int force, long timestamp) {
		this.x = x;
		this.y = y;
		this.force = force;
		this.timestamp = timestamp;
	}

	@Override
	public PenSample getPenSample() {
		return new PenSample(x, y, force, timestamp);
	}

	@Override
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	@Override
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public int getForce() {
		return force;
	}

	public void setForce(int force) {
		this.force = force;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
