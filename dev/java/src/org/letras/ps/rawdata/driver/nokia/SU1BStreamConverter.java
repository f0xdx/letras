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
import org.letras.ps.rawdata.driver.nokia.ByteStreamConverter;
import org.letras.psi.ipen.PenSample;

/**
 * The SU1BStreamConverter class converts the byte stream coming from 
 * the Bluetooth Serial Port Profile into RawDataSamples by assuming that 
 * the source is a Nokia SU-1B digital pen.
 * <p>
 * TODO: This class includes code from the PenStreamingConnection class 
 * included in the PaperToolkit from Stanford, license should be added!?
 * 
 * @author niklas
 * @version 0.0.1
 *
 */
class SU1BStreamConverter extends ByteStreamConverter{

	//SU-1B protocol specific values
	
	/**
	 * Different fields in the serial protocol.
	 */
	private static enum StreamingField {
		FORCE, HEADER, X, X_FRACTION, Y, Y_FRACTION
	}
	
	/**
	 * PenUP Event Identifier
	 */
	private static final byte ID_PEN_UP = 0x01;

	/**
	 * SimpleCoord Event Identifier
	 */
	private static final byte ID_SIMPLE_COORD = 0x00;

	/**
	 * Length of the PenUP Packet (packet without payload)
	 */
	private static final byte LENGTH_PEN_UP = 0x00;

	/**
	 * Length of the Simple Coordinate Packet
	 */
	private static final byte LENGTH_SIMPLE_COORD = 0x0B;

	//members
	
	/**
	 * Bytes to store the header in
	 */
	private int bCurrent, bLast, bLastLast;

	/**
	 * Byte array to store bytes for later conversion into coordinates
	 */
	private int[] coordinateBuffer = new int[4];
	
	/**
	 * Temporary variables to hold the sample data
	 */
	private double x,y;
	private int force; 

	/**
	 * Segment which is currently being transmitted
	 */
	private StreamingField nextUpStreamSegment = StreamingField.HEADER;

	/**
	 * Counter for the received number of bytes for the coordinate in transmission
	 */
	private int numBytesCoord;

	/**
	 * State of the pen. This is needed because the pen doesn't issue an explicit PenDown event.
	 */
	private boolean penIsUp = true;

	
	/**
	 * Standard constructor
	 * @param adapter to which the extracted RawDataSamples will be relayed
	 */
	public SU1BStreamConverter(IPenAdapter adapter) {
		super(adapter);
		nextUpStreamSegment = StreamingField.HEADER;
	}

	@Override
	public void handleByte(int currentByte) {
		
		// looking for the header portion of the data
		if (nextUpStreamSegment == StreamingField.HEADER) {
			
			// we got a new byte, so we push the others back
			bLastLast = bLast;
			bLast = bCurrent;
			bCurrent = currentByte;
			
			//check what type of event we are receiving
			if (bCurrent == LENGTH_SIMPLE_COORD && bLast == 0x00 && bLastLast == ID_SIMPLE_COORD) {
				// we are now in the sample event mode
				// we should read the next 0x0B bytes as coordinates and force
				nextUpStreamSegment = StreamingField.X;
				numBytesCoord = 0;
			} 
			else if (bCurrent == LENGTH_PEN_UP && bLast == 0x00 && bLastLast == ID_PEN_UP) {
				penIsUp = true;
				penUp();
			}
		} 
		else if (nextUpStreamSegment == StreamingField.X || nextUpStreamSegment == StreamingField.Y) { 
			//store 4 bytes for each coordinate
			coordinateBuffer[numBytesCoord] = currentByte;
			numBytesCoord++;
			
			if (numBytesCoord == 4) {
				// after four loops, all bytes for the coordinate have arrived so we can convert it
				if (nextUpStreamSegment == StreamingField.X) {
					x = convertBufferToCoordinate(coordinateBuffer);
					nextUpStreamSegment = StreamingField.Y;
				} else {
					y = convertBufferToCoordinate(coordinateBuffer);
					nextUpStreamSegment = StreamingField.X_FRACTION;
				}
				numBytesCoord = 0;
			}
		} 
		else if (nextUpStreamSegment == StreamingField.X_FRACTION) {
			// convert and save the value
			x += convertByteToFraction(currentByte);
			nextUpStreamSegment = StreamingField.Y_FRACTION;
		} 
		else if (nextUpStreamSegment == StreamingField.Y_FRACTION) {
			// convert and save the value
			y += convertByteToFraction(currentByte);
			nextUpStreamSegment = StreamingField.FORCE;
		} 
		else if (nextUpStreamSegment == StreamingField.FORCE) {
			// convert and save the value
			// original calculation from PaperToolkit
			// force = 128 - (((int) bCurrent) & 0xFF)
			
			force = 255 - (currentByte)*2;
			if (force < 0) {
				force = 0;
			}

			// IMPLEMENTATION NOTE:
			// type 'float' is NOT long enough to hold the orignial X/Y and
			// their
			// fraction part simutaneously, since the original X/Y is too big
			// so we have to append the fraction part after conversion.

			// done with the whole streaming sample, so output it!

			//check if the pen was lifted before this sample
			if (penIsUp) {
				penIsUp = false;
				penDown();
			}

			final PenSample finishedSample = new PenSample();
			finishedSample.setX(x);
			finishedSample.setY(y);
			finishedSample.setForce(force);
			finishedSample.setTimestamp(System.currentTimeMillis());
			
			sendSample(finishedSample);
		
			// reset the temp. variables
			x = 0.0;
			y = 0.0;
			force = 0;

			// look for the header of the next sample
			nextUpStreamSegment = StreamingField.HEADER;
		}
	}

	 
	/**
	 * 
	 */
	private float convertByteToFraction(int fractionByte) {
		 // use only the leftmost three bits of the byte
		return ((fractionByte >> 5) & 0x7) * 0.125f;
	}

	/**
	 * 
	 * @param coordinateBuffer
	 * @return
	 */
	private long convertBufferToCoordinate(int[] coordinateBuffer) {
		
		return (  (long) coordinateBuffer[0] << 24 
				| ((long) coordinateBuffer[1] << 16)
				| ((long) coordinateBuffer[2] << 8)
				| (long) coordinateBuffer[3]) & 0x00000000FFFFFFFF;
	}
}
