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

import org.letras.api.pen.IPenEvent;
import org.letras.api.pen.IPenSample;
import org.letras.psi.ipen.DoIPen;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Service;
import org.mundo.rt.Subscriber;
import org.mundo.service.ServiceInfo;

public class PenRecordingSession extends Service implements IReceiver {

	private final ServiceInfo penServiceInfo;
	private PenRecording penRecording;
	private Subscriber subscriber;

	public PenRecordingSession(ServiceInfo penServiceInfo) {
		this.penServiceInfo = penServiceInfo;
	}

	@Override
	public void received(Message msg, MessageContext ctx) {
		final Object o = msg.getObject();
		if (o instanceof IPenSample) {
			penRecording.record((IPenSample) o);
		}
 else if (o instanceof IPenEvent) {
			penRecording.record((IPenEvent) o);
		}
	}

	public PenRecording getRecording() {
		return penRecording;
	}

	public void record() {
		penRecording = new PenRecording();
		final DoIPen pen = new DoIPen(penServiceInfo.doService);
		subscriber = getSession().subscribe(penServiceInfo.zone, pen.channel(), this);
	}

	public void stop() {
		subscriber.unsubscribe();
	}

}
