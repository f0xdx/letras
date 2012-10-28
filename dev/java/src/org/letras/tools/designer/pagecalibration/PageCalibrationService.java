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
package org.letras.tools.designer.pagecalibration;

import org.letras.api.pen.IPenState;
import org.letras.api.pen.PenEvent;
import org.letras.api.pen.PenSample;
import org.letras.psi.ipen.DoIPen;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Logger;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Subscriber;
import org.mundo.service.ServiceInfo;

public class PageCalibrationService extends Service implements IReceiver {
	private static Logger log = Logger.getLogger(PageCalibrationService.class);

	private final ServiceInfo penServiceInfo;
	private final IPageCalibrationListener listener;
	boolean down = false;
	boolean firstCornerIdentified = false;
	private double xMin = Double.POSITIVE_INFINITY;
	private double yMin = Double.POSITIVE_INFINITY;
	private double xMax = Double.NEGATIVE_INFINITY;
	private double yMax = Double.NEGATIVE_INFINITY;
	private Subscriber subscriber;

	public PageCalibrationService(ServiceInfo penServiceInfo, IPageCalibrationListener listener) {
		this.penServiceInfo = penServiceInfo;
		this.listener = listener;
		Mundo.registerService(this);
	}

	@Override
	public void init() {
		super.init();
		final DoIPen doIPen = new DoIPen(penServiceInfo.doService);
		subscriber = getSession().subscribe(penServiceInfo.zone, doIPen.channel(), this);
	}

	public void close() {
		subscriber.unsubscribe();
		Mundo.unregisterService(this);
	}

	@Override
	public void received(Message message, MessageContext messageContext) {
		final Object obj = message.getObject();
		if (obj instanceof PenEvent) {
			final PenEvent event = (PenEvent) obj;
			if (event.state == IPenState.DOWN) {
				down = true;
			} else if (down && event.state == IPenState.UP) {
				down = false;
				if (!firstCornerIdentified) {
					log.info("first corner of page identified");
					firstCornerIdentified = true;
					listener.firstCornerIdentified();
				} else {
					log.info("page calibrated: (" + xMin + "," + yMin + "), " + "(" + xMax + "," + yMax + ")");
					listener.documentCalibrated(xMin, yMin, xMax - xMin, yMax - yMin);
					subscriber.unsubscribe();
					Mundo.unregisterService(this);
				}
			}
		} else if (down && obj instanceof PenSample) {
			final PenSample currentSample = (PenSample) obj;
			xMin = Math.min(xMin, currentSample.getX());
			yMin = Math.min(yMin, currentSample.getY());
			xMax = Math.max(xMax, currentSample.getX());
			yMax = Math.max(yMax, currentSample.getY());
		}
	}

}
