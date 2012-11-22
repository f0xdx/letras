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
package org.letras.api.pen;



/**
 * This class encapsulates all information transferred as part of a raw data sample.
 * 
 * @author niklas
 * @version 0.3
 */
public class PenSample implements IPenSample {

	// members

	/**
	 * The x-coordinate in the global Anoto Pattern Space. One integer corresponds to around 0.3mm on paper.
	 */
	public final double x;

	/**
	 * The y-coordinate in the global Anoto Pattern Space. One integer corresponds to around 0.3mm on paper.
	 */
	public final double y;

	/**
	 * The force inflicted on the pen's tip when the sample was read. Higher value means more pressure
	 */
	public final int force;

	/**
	 * Time when this sample was read or created.
	 */
	public final long timestamp;


	/**
	 * Most specific constructor for setting all members
	 * 
	 * @param xCoord the x-coordinate in global Anoto Coordinates
	 * @param yCoord the y-coordinate in global Anoto Coordinates
	 * @param force the pen pressure on the tip
	 * @param timestamp of the pen sample
	 */
	public PenSample(double xCoord, double yCoord, int force, long timestamp) {
		this.x = xCoord;
		this.y = yCoord;
		this.force = force;
		this.timestamp = timestamp;
	}

	// methods

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getForce() {
		return force;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public PenSample getPenSample() {
		return this;
	}

}
