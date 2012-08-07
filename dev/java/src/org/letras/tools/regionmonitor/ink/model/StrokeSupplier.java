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
package org.letras.tools.regionmonitor.ink.model;

import java.util.HashMap;

import org.letras.api.region.RegionEvent;
import org.letras.api.region.RegionSample;
import org.letras.psi.iregion.IRegion;
import org.mundo.rt.GUID;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Service;
import org.mundo.rt.Subscriber;

public class StrokeSupplier extends Service implements IReceiver{
	
	private IRegion remoteRegion;
	
	private InkRegion strokeReceiver;
	
	private Subscriber subscriber;
	
	private HashMap<GUID, Stroke> currentStrokes;
	
	private HashMap<String, Stroke> penToStroke;
	
	public StrokeSupplier(IRegion remoteRegion, InkRegion strokeReceiver) {
		this.remoteRegion = remoteRegion;
		this.strokeReceiver = strokeReceiver;
		currentStrokes = new HashMap<GUID, Stroke>();
		penToStroke = new HashMap<String, Stroke>();
	}
	
	public void connect(String zone) {
		subscriber = this.getSession().subscribe(zone, remoteRegion.channel(), this);
	}
	
	public void disconnect() {
		subscriber.unsubscribe();
	}

	@Override
	public void received(Message msg, MessageContext ctx) {
		Object obj = msg.getObject();
		if (obj instanceof RegionSample) {
			handle((RegionSample) obj);
		} else if (obj instanceof RegionEvent) {
			handle((RegionEvent) obj);
		}
	}
	
	private void handle(RegionEvent event) {
		if (event.traceStart()) {
			if (currentStrokes.containsKey(event.getGuid())) {
				endStroke(currentStrokes.get(event.getGuid()));
			}
			final Stroke stroke = new Stroke();
			currentStrokes.put(event.getGuid(), stroke);
			penToStroke.put(event.getPenID(), stroke);
			strokeReceiver.addStroke(stroke);
		}
		else if (event.traceEnd()) {
			if (currentStrokes.containsKey(event.getGuid())) {
				endStroke(currentStrokes.get(event.getGuid()));
			}
		}
	}
	
	private void handle(RegionSample sample) {
		if (penToStroke.containsKey(sample.getPenID())) {
			penToStroke.get(sample.getPenID()).addSample(sample);
		}
	}
	
	private void endStroke(Stroke currentStroke) {
		currentStroke.finished();
	}
	
	

	
	
	
}
