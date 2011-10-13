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
package org.letras.ps.rawdata.driver.nokia;

import org.letras.ps.rawdata.IPenAdapter;
import org.letras.psi.ipen.IPenState;
import org.letras.psi.ipen.PenSample;
/**
 * The byte stream converter defines the interface for the pen model specific
 * stream converters and provides basic functions and functionality that every
 * stream converter uses.
 * <p>
 * All stream converters should extend this class.
 * 
 * @author niklas
 * @version 0.0.1
 */
abstract class ByteStreamConverter {

	//members

	private IPenAdapter penAdapter;

	//constructors 

	/**
	 * constructor to be used by the subclasses only
	 * @param adapter
	 */
	protected ByteStreamConverter(IPenAdapter adapter) {
		this.penAdapter = adapter;
	}

	//methods

	/**
	 * receives the byteStream one byte at a time and converts them into RawDataSamples
	 * @param currentByte
	 */
	abstract void handleByte(int currentByte);

	/**
	 * Call this method to signal the ByteStreamConverter that the pen has been disconnected
	 */
	void penDisconnected() {
		penAdapter.penState(IPenState.OFF);
	}
	
	/**
	 * Call this method to signal the ByteStreamConverter that the pen has been lifted. This
	 * also implies that the pen is ON.
	 */
	void penUp() {
		penAdapter.penState(IPenState.UP);
	}
	
	
	/**
	 * Call this method to signal the ByteStreamConverter that the pen is  pressed on the paper
	 */
	void penDown() {
		penAdapter.penState(IPenState.DOWN);
	}
	
	/**
	 * Call this method to signal the ByteStreamConverter that the connector has 
	 * detected a pen-side error.
	 */
	void penError() {
		penAdapter.penState(IPenState.EXCEPTION);
	}
	
	/**
	 * sends the sample to the pen adapter
	 * @param sample to be relayed 
	 */
	protected void sendSample(PenSample sample) {
		penAdapter.publishSample(sample);
	}

	/**
	 * Call this method to signal the ByteStreamConverter that a pen has connected
	 */
	protected void penConnected() {
		penAdapter.penState(IPenState.ON);
	}
	
	
}
