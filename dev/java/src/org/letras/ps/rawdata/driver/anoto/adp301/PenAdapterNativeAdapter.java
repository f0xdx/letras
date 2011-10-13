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
package org.letras.ps.rawdata.driver.anoto.adp301;

import org.letras.ps.rawdata.IPenAdapter;
import org.letras.psi.ipen.IPenState;
import org.letras.psi.ipen.PenSample;

/**
 * The PenAdapterNativeAdapter is an adapter to ease communication between
 * the native driver part and the IPenAdapter. The adapter hides the IPenState
 * and the PenSample class from the native code.
 * 
 * @author niklas
 *
 */
public class PenAdapterNativeAdapter {
	
	/**
	 * the IPenAdapter to encapsulate
	 */
	private IPenAdapter penAdapter;
	
	/**
	 * constructor needs the penAdapter that should be encapsulated
	 * @param penAdapter
	 */
	public PenAdapterNativeAdapter(IPenAdapter penAdapter) {
		this.penAdapter = penAdapter;
	}
	
	/**
	 * switch to pen-down state sending the pen-down event if necessary. This 
	 * should be called before calling sendSample().
	 */
	public void penDown() {
		penAdapter.penState(IPenState.DOWN);
	}
	/**
	 * switch to pen-up state sending the pen-up event if necessary
	 */
	public void penUp() {
		penAdapter.penState(IPenState.UP);
	}
	
	/**
	 * change to the pen-disconnected state. This does not however shutdown
	 * the PenService.
	 */
	public void penDisconnected() {
		penAdapter.penState(IPenState.OFF);
	}
	
	/**
	 * change to the pen-connected state. This should be called prior to sending
	 * pen-down or samples.
	 */
	public void penConnected() {
		penAdapter.penState(IPenState.ON);
	}
	
	/**
	 * send a sample with the given coordinates and pen pressure. The current time will be
	 * used as timestamp when creating the PenSample.
	 * @param x the Anoto x-coordinate
	 * @param y the Anoto y-coordinate
	 * @param force the pen pressure
	 */
	public void sendSample(double x, double y, int force) {
		final PenSample sample = new PenSample(x, y, force, System.currentTimeMillis());
		penAdapter.publishSample(sample);
	}
}
