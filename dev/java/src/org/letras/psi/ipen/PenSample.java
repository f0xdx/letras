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
package org.letras.psi.ipen;

import org.mundo.annotation.mcSerialize;

/**
 * This class encapsulates all information transferred as part of a
 * raw data sample. Such samples are streamed on a Mundo channel for
 * each active pen.
 * 
 * @author felix_h
 * @version 0.0.1
 */
@mcSerialize
public class PenSample {

	// members
	
	protected double x;
	
	protected double y;
	
	protected int force;
	
	protected long timestamp;
	
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

	public int getForce() {
		return force;
	}

	public void setForce(int force) {
		this.force = force;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	// constructors
	
	/**
	 * No-argument constructor for serialization. Note that this constructor
	 * does not initialize the class members with meaningful values. It should
	 * be used by the serialization mechanism ONLY, use the constructor taking
	 * values for the class members instead. 
	 */
	public PenSample() {
		this.x = 0.0d;
		this.y = 0.0d;
		this.force = 0;
		this.timestamp = 0l; // EPOCH ?
	}
	
	public PenSample(double xCoord, double yCoord, int force, long timestamp) {
		this.x = xCoord;
		this.y = yCoord;
		this.force = force;
		this.timestamp = timestamp;
	}

	// methods
}
