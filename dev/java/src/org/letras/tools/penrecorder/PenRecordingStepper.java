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
package org.letras.tools.penrecorder;

import org.letras.psi.ipen.IPen;
import org.letras.psi.ipen.IPenState;
import org.letras.psi.ipen.PenEvent;
import org.mundo.annotation.mcRemote;
import org.mundo.rt.GUID;

@mcRemote
public class PenRecordingStepper extends AbstractPenRecordingPlayer implements IPen {
	private boolean up;
	
	public PenRecordingStepper() {
		channel = "Letras Pen Stepper " + new GUID().toString();
	}
	
	@Override
	public void load(PenRecording penRecording) {
		super.load(penRecording);
	}
	
	public void step() {
		Object message = currentRecording.getNext();
		processMessage(message);
	}
	
	public void stepFigure() {
		up = false;
		while (currentRecording.hasNext() && !up) {
			processMessage(currentRecording.getNext());
		}
	}
	
	public boolean hasNext() {
		return currentRecording.hasNext();
	}
	
	@Override
	protected void processEvent(PenEvent event) {
		if (event.getNewState() == IPenState.UP)
			up = true;
		super.processEvent(event);
	}
}
