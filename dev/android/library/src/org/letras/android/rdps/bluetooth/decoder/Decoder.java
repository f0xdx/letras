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
package org.letras.android.rdps.bluetooth.decoder;

import java.io.InputStream;

import org.letras.ps.rawdata.IPenAdapter;
import org.letras.psi.ipen.IPenState;
import org.letras.psi.ipen.PenSample;	

/**
 * A <code>Decoder</code> handles the task of converting the input stream from the
 * pen coming from the Bluetooth serial port emulation into PenSamples.
 * 
 * The samples in conjunction with pen-up and pen-down events are forwarded to an
 * implementation of IPenAdapter.
 * 
 * @author niklas
 */
public abstract class Decoder {

	/**
	 * PenAdapter to which PenSamples and state changes are send
	 */
	IPenAdapter penAdapter;
	
	/**
	 * start decoding the a stream into samples and events. The <code>Decoder</code> assumes
	 * that the stream is at the beginning and no bytes have been read from it before.<br>
	 * A call to this function is blocking, you must not call this function
	 * from the main run loop.
	 * @param sppRawStream the stream to decode
	 * @param listener the IPenAdapter to send events and samples to
	 */
	public void beginDecoding(InputStream sppRawStream, IPenAdapter listener) {
		this.penAdapter = listener;
		penAdapter.penState(IPenState.ON);
		decode(sppRawStream);
		penAdapter.penState(IPenState.OFF);
	}

	/**
	 * this method contains the actual decoding logic.<br>
	 * A subclass must implement this method as blocking and only return
	 * when the stream is closed.
	 * @param sppRawStream the stream to decode
	 */
	protected abstract void decode(InputStream sppRawStream);
	
	/**
	 * set the pen into Pen-Up state. 
	 * This is a callback function for concrete decoder subclasses
	 */
	protected void penUp() {
		penAdapter.penState(IPenState.UP);
	};
	
	/**
	 * set the pen into Pen-Down state.
	 * This is a callback function for concrete decoder subclasses
	 */
	protected void penDown() {
		penAdapter.penState(IPenState.DOWN);
	};

	/**
	 * sends the given sample to the <code>IPenAdapter</code> 
	 * @param sample the sample which is ready to be send
	 */
	protected void receivedSample(PenSample sample) {
		penAdapter.publishSample(sample);
	};
}
