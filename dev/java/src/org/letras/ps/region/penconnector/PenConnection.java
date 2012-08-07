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
package org.letras.ps.region.penconnector;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.api.pen.IPenState;
import org.letras.api.pen.PenEvent;
import org.letras.api.pen.PenSample;
import org.letras.psi.ipen.DoIPen;
import org.letras.psi.ipen.IPen;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Session;
import org.mundo.rt.Subscriber;
import org.mundo.service.ServiceInfo;

/**
 * PenConnection implements all the basic functionality for receiving and delegating 
 * received samples as well as caching the state of the pen. Before a pen connection 
 * does this you have to activate it with a call to activatePen.
 * @author niklas
 */
public class PenConnection implements IPenConnection, IReceiver{

	//logger
	
	private static Logger logger = Logger.getLogger("org.letras.ps.region.penconnector");
	
	//static methods
	
	/**
	 * create a PenConnection by dissecting an instance of <code>ServiceInfo</code>
	 * @return the created PenConnection
	 */
	public static PenConnection createPenConnectionFromServiceInfo(ServiceInfo serviceInfo) {
		return new PenConnection((IPen) new DoIPen(serviceInfo.doService), serviceInfo.zone);
	}
	
	
	//members
	
	private IPen pen;
	
	private String zone;
	
	private String penId;
	
	private String channel;
	
	private int state;
	
	private Subscriber subscriber;
	
	private ISampleProcessor sampleDelegate;
	
	//setter and getter
	
	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * @return the penId
	 */
	public String getPenId() {
		return penId;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * @return the pen service
	 */
	public IPen getPen() {
		return pen;
	}
	
	/**
	 * sets the delegate to which received samples will be forwarded
	 * @param delegate
	 */
	public void setSampleDelegate(ISampleProcessor delegate) {
		this.sampleDelegate = delegate;
	}

	@Override
	public boolean isActive() {
		return subscriber != null;
	}

	//constructor
	
	public PenConnection(IPen pen, String zone) {
		this.pen = pen;
		this.zone = zone;
		this.penId = pen.penId();
		this.channel = pen.channel();
		this.state = pen.penState();
	}
	
	/**
	 * establish the connection to the appropriate Mundo channel, which 
	 * allows the Pen Connection to receive samples from the pen
	 * @param currentSession a session on which we will subscribe
	 * @param iSampleProcessor 
	 */
	void activatePen(Session currentSession, ISampleProcessor iSampleProcessor) {
		if (subscriber == null) {
			subscriber = currentSession.subscribe(zone, channel, this);
			sampleDelegate = iSampleProcessor;
		} else logger.logp(Level.WARNING, "PenConnection", "activatePen", "pen was already active");
	}
	
	/**
	 * tear down the connection to the Mundo channel, which stops the receiving 
	 * of samples. The Pen Connection simulates a Pen up event at this point.
	 */
	void deactivatePen() {
		if (subscriber != null) {
			subscriber.unsubscribe();
			//we simulate a penUp event here
			sampleDelegate.penUp();
			subscriber = null;
		}
	}

	@Override
	public void received(Message msg, MessageContext ctx) {
		Object obj = msg.getObject();
		if (obj instanceof PenSample) {
			sampleDelegate.handleSample((PenSample) obj);
		} else if (obj instanceof PenEvent) {
			PenEvent event = (PenEvent) obj;
			//save the state
			state = event.getNewState();
			//we treat every other event then IPenState.DOWN as IPenState.UP
			if (state == IPenState.DOWN) {
				sampleDelegate.penDown();
			} else {
				sampleDelegate.penUp();
			}
		}
	}


}
