package org.letras.api.pen;

public interface IPenSample {

	public double getX();

	public double getY();

	public int getForce();

	public long getTimestamp();

	public PenSample getPenSample();
}