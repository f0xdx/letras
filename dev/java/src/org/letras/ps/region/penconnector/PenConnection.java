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

import java.util.HashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.api.pen.IPenState;
import org.letras.api.pen.PenEvent;
import org.letras.api.pen.PenSample;
import org.letras.psi.ipen.DoIPen;
import org.letras.psi.ipen.IPen;
import org.letras.psi.ipen.MundoPenEvent;
import org.letras.psi.ipen.MundoPenSample;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Subscriber;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceManager;

/**
 * Implements all the basic functionality for receiving and delegating received samples as well as caching the state of
 * a pen.
 * 
 * @version 0.3
 * @author Niklas Lochschmidt <nlochschmidt@gmail.com>
 */
public class PenConnection implements org.letras.api.pen.IPen, IReceiver {

	//logger

	private static Logger logger = Logger.getLogger("org.letras.api.pen");

	//static methods

	/**
	 * create a Pen instance by dissecting an instance of <code>ServiceInfo</code>
	 * 
	 * @return pen
	 */
	public static PenConnection createPenConnectionFromServiceInfo(ServiceInfo serviceInfo) {
		return new PenConnection(new DoIPen(serviceInfo.doService), serviceInfo.zone);
	}

	// static members

	public static Executor threadPool = Executors.newCachedThreadPool(new ThreadFactory() {
		@Override
		public Thread newThread(Runnable arg0) {
			final Thread thread = new Thread(arg0);
			thread.setDaemon(true);
			return thread;
		}
	});


	//members

	private final IPen pen;

	private final String zone;

	private final String penId;

	private final String channel;

	private int state;

	private Subscriber subscriber;

	private final HashSet<IPenListener> listeners = new HashSet<IPenListener>();

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
	@Override
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
	@Override
	public int getPenState() {
		return state;
	}

	/**
	 * @return the pen service
	 */
	public IPen getPen() {
		return pen;
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
	 */
	private void activatePen() {
		if (subscriber == null) {
			subscriber = ServiceManager.getInstance().getSession().subscribe(zone, channel, this);
		} else logger.logp(Level.WARNING, "PenConnection", "activatePen", "pen was already active");
	}

	/**
	 * tear down the connection to the Mundo channel, which stops the receiving
	 * of samples. The Pen Connection simulates a Pen up event at this point.
	 */
	private void deactivatePen() {
		if (subscriber != null) {
			subscriber.unsubscribe();
			//we simulate a penUp event here
			synchronized (listeners) {
				for (final IPenListener listener : this.listeners) {
					listener.receivePenEvent(new PenEvent(state, state = IPenState.UP));
				}
			}
			subscriber = null;
		}
	}

	@Override
	public void received(Message msg, MessageContext ctx) {
		final Object obj = msg.getObject();
		if (obj instanceof MundoPenSample) {
			final MundoPenSample mundoPenSample = (MundoPenSample) obj;
			synchronized (listeners) {
				final PenSample penSample = mundoPenSample.getPenSample();
				for (final IPenListener listener : listeners) {
					listener.receivePenSample(penSample);
				}
			}
		} else if (obj instanceof MundoPenEvent) {
			final MundoPenEvent mundoPenEvent = (MundoPenEvent) obj;
			//save the state
			state = mundoPenEvent.state;
			synchronized (listeners) {
				final PenEvent penEvent = mundoPenEvent.getPenEvent();
				for (final IPenListener listener : listeners) {
					listener.receivePenEvent(penEvent);
				}
			}
		}
	}

	@Override
	public void registerPenListener(IPenListener penListener) {
		synchronized (listeners) {
			listeners.add(penListener);
			if (subscriber == null)
				activatePen();
		}
	}

	@Override
	public void unregisterPenListener(IPenListener penListener) {
		synchronized (listeners) {
			listeners.remove(listeners);
			if (listeners.isEmpty())
				deactivatePen();
		}
	}
}
