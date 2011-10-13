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

import org.letras.psi.ipen.DoIPen;
import org.letras.psi.ipen.IPenState;
import org.letras.psi.ipen.PenEvent;
import org.letras.psi.ipen.PenSample;
import org.mundo.rt.GUID;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;

/**
 * The Pen Information class holds the DoObject and handles samples send from the pen.
 * 
 * @author niklas
 */
public class PenInformation extends Observable implements IReceiver{

	private DoIPen doPen;
	
	private String nodeId;
	
	private String penId = "";
	private int penState = 0;
	private int lastPenState = 0;
	
	private int currentSampleDelay = 0;
	private PenSample currentSample; 
	
	/**
	 * Default Constructor
	 * @param doPen the DoObject for the pen
	 */
	public PenInformation(DoIPen doPen, GUID nodeID) {
		this.doPen = doPen;
		this.nodeId = nodeID.toString();
	}
	
	/**
	 * get the penId. the penId will be cached.
	 * @return the penId 
	 */
	public String getPenID() {
		if (penId.equals("")) {
			penId = doPen.penId();
		}
		return penId;
		
	}
	
	/**
	 * get the nodeId from the node to which the pen is connected
	 * @return
	 */
	public String getNodeId() {
		return this.nodeId;
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
		switch (doPen.penState()) {
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

	/**
	 * Method from interface {@link org.mundo.rt.IReceiver}
	 * <br> 
	 * Only messages containing {@link org.letras.psi.ipen.PenSample}s
	 * or {@link org.letras.psi.ipen.PenEvent}s will be handled
	 */
	@Override
	public void received(Message arg0, MessageContext arg1) {
		Object obj = arg0.getObject();
		if (obj instanceof PenSample) {
			currentSample = (PenSample) obj;
			currentSampleDelay = (int) (System.currentTimeMillis() - currentSample.getTimestamp());
		} else if (obj instanceof PenEvent) {
			PenEvent event = (PenEvent) obj;
			penState = event.getNewState();
			lastPenState = event.getOldState();
		}
		
		//notify observers
		if (currentSample != null) {
			setChanged();
			notifyObservers();
		}
	}
}
