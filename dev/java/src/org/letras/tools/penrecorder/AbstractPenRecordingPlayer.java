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
import org.letras.psi.ipen.PenEvent;
import org.letras.psi.ipen.PenSample;
import org.mundo.rt.Message;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;

public class AbstractPenRecordingPlayer extends Service implements IPen {

	protected PenRecording currentRecording;
	protected int state = 0;
	protected Publisher publisher;
	protected long currentTime = -1;
	protected String channel;

	@Override
	public void init() {
		super.init();
		publisher = getSession().publish("lan", channel());
	}

	@Override
	public String channel() {
		return channel;
	}

	@Override
	public String penId() {
		return channel;
	}

	@Override
	public synchronized int penState() {
		return state;
	}

	protected void load(PenRecording penRecording) {
		this.currentRecording = penRecording;
	}

	protected void processMessage(Object message) {
		if (message instanceof PenEvent) {
			PenEvent event = (PenEvent) message;
			processEvent(event);
		}
		else if (message instanceof PenSample) {
			PenSample sample = (PenSample) message;
			processSample(sample);
		}
	}

	protected void processEvent(PenEvent event) {
		synchronized(this) {
			state = event.getNewState();
		}
		publisher.send(Message.fromObject(event));
	}
	
	protected void processSample(PenSample sample) {
		publisher.send(Message.fromObject(sample));
	}

}
