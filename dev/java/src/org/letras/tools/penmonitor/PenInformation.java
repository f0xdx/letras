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
package org.letras.tools.penmonitor;

import java.util.Observable;

import org.letras.api.pen.IPen.IPenListener;
import org.letras.api.pen.IPenState;
import org.letras.api.pen.PenEvent;
import org.letras.api.pen.PenSample;

/**
 * The Pen Information class holds the DoObject and handles samples send from the pen.
 * 
 * @author niklas
 */
public class PenInformation extends Observable implements IPenListener {

	private String penId = "";
	private int penState = 0;
	private int lastPenState = 0;

	private int currentSampleDelay = 0;
	private PenSample currentSample = new PenSample(0.0, 0.0, 0, 0);

	public PenInformation(String penId) {
		this.penId = penId;
	}

	/**
	 * get the penId. the penId will be cached.
	 * @return the penId
	 */
	public String getPenID() {
		return penId;
	}

	/**
	 * get the pens state in human readable form
	 * @return pen state
	 */
	public String getPenState() {
		return penStateAsString(penState);
	}

	/**
	 * get the pens last state in human readable form
	 * @return last pen state
	 */
	public String getLastPenState() {
		return penStateAsString(lastPenState);
	}

	/**
	 * get the x-coordinate of the pen based on the last received data sample.
	 * @return x-coordinat in anoto pattern space
	 */
	public double getCurrentXPosition() {
		if (penState == IPenState.DOWN)
			return currentSample.getX();
		else return 0;
	}
	/**
	 * get the y-coordinate of the pen based on the last received data sample
	 * @return y-coordinate in anoto pattern space
	 */
	public double getCurrentYPosition() {
		if (penState == IPenState.DOWN)
			return currentSample.getY();
		else return 0;
	}

	/**
	 * get the tip force that's applied to the pen based on the last received data sample.
	 * <br>
	 * A value of 0 represents nearly zero force while higher values represent more pressure
	 * @return tip force
	 */
	public int getCurrentForce() {
		if (penState == IPenState.DOWN)
			return currentSample.getForce();
		else return 0;
	}

	/**
	 * get the delay between the creation of the sample in the RDPS and the reception
	 * @return delay in milliseconds
	 */
	public int getCurrentDelay() {
		if (penState == IPenState.DOWN)
			return currentSampleDelay;
		else return 0;
	}

	/**
	 * convert the pen state id to a string
	 * @param penState probably retrived from a <code>PenEvent</code>
	 * @return state description
	 */
	private String penStateAsString(int penState) {
		switch (penState) {
		case IPenState.OFF:
			return "off";
		case IPenState.ON:
			return "on";
		case IPenState.DOWN:
			return "down";
		case IPenState.OUT_OF_REACH:
			return "out of reach";
		case IPenState.EXCEPTION:
			return "off (exception)";
		}
		return "unknown";
	}

	@Override
	public void receivePenEvent(PenEvent penEvent) {
		penState = penEvent.state;
		lastPenState = penEvent.oldState;
		setChanged();
		notifyObservers();
	}

	@Override
	public void receivePenSample(PenSample penSample) {
		currentSample = penSample;
		currentSampleDelay = (int) (System.currentTimeMillis() - currentSample.getTimestamp());
		setChanged();
		notifyObservers();
	};
}
