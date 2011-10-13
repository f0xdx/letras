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
package org.letras.ps.rawdata.penmanager;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.psi.ipen.IPen;
import org.letras.psi.ipen.IPenState;
import org.letras.psi.ipen.PenEvent;
import org.letras.psi.ipen.PenSample;
import org.mundo.rt.Message;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;

/**
 * 
 * @author felix_h
 * @version 0.0.1
 */
public class PenService extends Service implements IPen {
	
	// logger

	private static final Logger logger = Logger
			.getLogger("org.letras.ps.rawdata.penmanager");
	
	// defaults
	
	private static final String CHN_SCHEME = "%s.chn";
	
	// members

	private String penId;
	
	private int state;
	
	private String channel;
	
	private Publisher pub;
	
	// constructors

	public PenService(String penId) {
		this.penId = penId;
		
		// set the pen id as user friendly id for the service
		this.setServiceInstanceName(penId);
		
		// now generate the channel name
		this.channel = String.format(CHN_SCHEME, this.penId);
		
		// set the state to off
		this.state = IPenState.OFF;
	}
	
	// methods

	/**
	 * Sets the state of the pen to <code>state</code>. State takes values as defined
	 * in {@link org.letras.psi.ipen.IPenState}.
	 * 
	 * @param state		the new pen state
	 */
	public void setPenState(int state) {
		
		PenEvent evt = new PenEvent(this.state, state);
		
		this.state = state;
		
		// publish the event on our channel
		pub.send(Message.fromObject(evt));
	}
	
	/**
	 * Publish a sample on the channel associated with this pen. Note: for sake
	 * of efficiency no check for null samples is done here. The caller must make sure
	 * that no null samples will be published here.
	 * 
	 * @param sample	the <code>PenSample</code> to publish
	 */
	public void publishSample(PenSample sample) {
		pub.send(Message.fromObject(sample));
	}
	
	/**
	 * Interface method from {@link org.letras.psi.ipen.IPen}.
	 */
	@Override
	public String channel() {
		return this.channel;
	}

	/**
	 * Interface method from {@link org.letras.psi.ipen.IPen}.
	 */
	@Override
	public String penId() {
		return this.penId;
	}

	/**
	 * Interface method from {@link org.letras.psi.ipen.IPen}.
	 */
	@Override
	public int penState() {
		return this.state;
	}

	@Override
	public void init() {
		logger.logp(Level.FINE, "PenService", "init", 
				String.format("pen service started (%s)", this.penId));
		super.init();
		
		// set up the publisher
		this.pub = this.getSession().publish(this.getServiceZone(), this.channel);
	}

	@Override
	public void shutdown() {
		logger.logp(Level.FINE, "PenService", "shutdown", 
				String.format("pen service shut down (%s)", this.penId));
		super.shutdown();
	}
}
