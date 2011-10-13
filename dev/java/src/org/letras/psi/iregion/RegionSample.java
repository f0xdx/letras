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
package org.letras.psi.iregion;

import org.letras.psi.ipen.PenSample;
import org.letras.psi.iregion.shape.Bounds;
import org.letras.psi.iregion.shape.IShape;
import org.letras.util.geom.IVector2d;
import org.mundo.annotation.mcSerialize;

/**
 * After the region processing stage, the information of the position in the global
 * coordinate system given in <i>Pattern Space Coordinates</i> (PSC) alone does not
 * suffice anymore. Therefore, the x and y coordinates of a {@link RegionSample} are represented
 * in <i>Normalized Region Coordinates</i> (NRC), that is: x and y hold values ranging from 0.0 to
 * 1.0, which indicate the normalized coordinate to the interested enclosing regions bounding box.
 * The term interested here refers to a situation, where a hungry region might consume samples of
 * their children. In such a case, the x and y coordinates of the samples streamed to the
 * parent region will differ from those streamed to the children, while the PSC coordinates
 * would be the same. In order to allow an unique identification of a sample, the PSC
 * coordinates are included also.
 * 
 * @author felix_h
 * @version 0.1
 */
@mcSerialize
public class RegionSample implements RegionMessage, IVector2d {
	
	// members

	protected double pscX;
	
	protected double pscY;
	
	protected String penID;

	protected double x;

	protected double y;

	protected int force;

	protected long timestamp;
	
	// constructors

	/**
	 * No-argument constructor for serialization. Note that this constructor
	 * does not initialize the class members with meaningful values. It should
	 * be used by the serialization mechanism ONLY, use the constructor taking
	 * values for the class members instead. 
	 */
	public RegionSample() {
		super();
	}
	
	/**
	 * Constructor taking all enclosed data explicitly. Note that when using this
	 * constructor, the Caller takes responsibility, for providing the coordinates
	 * (both PSC and NRC) correctly.
	 * 
	 * @param x			x coordinate of the sample (in NRC)
	 * @param y			y coordinate of the sample (in NRC)
	 * @param pscX		x coordinate of the sample (in PSC)
	 * @param pscY		y coordinate of the sample (in PSC)
	 * @param force		force of the sample
	 * @param timestamp	time-stamp of the sample
	 */
	public RegionSample(double x, double y, double pscX, double pscY, 
			int force, long timestamp, String penId) {
		this.x = x;
		this.y = y;
		this.force = force;
		this.timestamp = timestamp;
		this.pscX = pscX;
		this.pscY = pscY;
		this.penID = penId;
	}
	
	/**
	 * Constructor creating a {@link RegionSample} out of the data represented in the provided
	 * {@link PenSample} and its enclosing regions bound (in form of an {@link Bounds}).
	 * 
	 * @param ps			the {@link PenSample} containing the data to be enclosed
	 * 						by the {@link RegionSample}
	 * @param regionBounds	the {@link Bounds} of an enclosing {@link IRegion}s {@link IShape}
	 */
	public RegionSample(PenSample ps, Bounds regionBounds, String penId) {
		this.pscX = ps.getX();
		this.pscY = ps.getY();
		this.force = ps.getForce();
		this.timestamp = ps.getTimestamp();
		this.normalizeCoordinates(regionBounds.getX(), regionBounds.getY(), 
				regionBounds.getWidth(), regionBounds.getHeight());
		this.penID = penId;
	}
	
	// methods

	/**
	 * @return the pscX
	 */
	public double getPscX() {
		return pscX;
	}

	/**
	 * @param pscX the pscX to set
	 */
	public void setPscX(double pscX) {
		this.pscX = pscX;
	}

	/**
	 * @return the pscY
	 */
	public double getPscY() {
		return pscY;
	}

	/**
	 * @param pscY the pscY to set
	 */
	public void setPscY(double pscY) {
		this.pscY = pscY;
	}
	
	/**
	 * @return the penID
	 */
	public String getPenID() {
		return penID;
	}
	
	/**
	 * @return the normalized horizontal position of this sample
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the normalized horizontal position of this sample
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the normalized vertical position of this sample
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the normalized vertical position of this sample
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the force value associated with this sample
	 */
	public int getForce() {
		return force;
	}

	/**
	 * @param force the force value associated with this sample
	 */
	public void setForce(int force) {
		this.force = force;
	}

	/**
	 * @return the timestamp of this sample
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp of this sample
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void accept(RegionMessageProcessor processor) {
		processor.process(this);
	}
	
	/**
	 * Normalize this samples x and y coordinates to the rectangular region specified
	 * (compute normalized region coordinates).
	 * <P>
	 * Precondition: 	The PSC coordinates of this sample must be set to meaningful values
	 * 					and the sample lies within the rectangle specified by the provided
	 * 					parameters<br>
	 * Postcondition: 	The x and y coordinates of this region will be initialized to the
	 * 					normalized region coordinates of the provided rectangle<br>
	 * 
	 * @param xr	absolute x position of the region (in PSC)
	 * @param yr	absolute y position of the region (in PSC)
	 * @param wr	relative width of the region (in PSC)
	 * @param hr	relative height of the region (in PSC)
	 */
	protected void normalizeCoordinates(double xr, double yr, double wr, double hr) {
		this.x = (wr == 0.0) ? 0.0 : (this.pscX - xr) / wr;
		this.y = (hr == 0.0) ? 0.0 : (this.pscY - yr) / hr;
	}
	
	@Override
	public String toString() {
		return "region_sample([" + x + "," + y
			+ "], [" + pscX + "," + pscY
			+ "], " + force + ", " + timestamp + ") from pen '" + penID + "'";
	}
}	
