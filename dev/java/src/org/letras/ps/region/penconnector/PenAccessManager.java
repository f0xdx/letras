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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.Letras;
import org.letras.api.pen.IPen;
import org.letras.api.pen.IPen.IPenListener;
import org.letras.api.pen.IPenDiscovery;
import org.letras.ps.region.sampleprocessor.ISampleProcessorFactory;
import org.mundo.rt.TypedMap;

/**
 * The PenAccessManager is the top-level class of the PenConnectorService-Module.
 * The Setup should be done in three steps
 * 
 * <ol>
 * 	<li>Create a new PenAccessManager using the constructor</li>
 * 	<li>Set the factory for SampleProcessors with <code>setSampleProcessorFactory</code></li>
 * 	<li>Start the discovery service with a call to <code>init</code></li>
 * </ol>
 * 
 * @author niklas
 *
 */
public class PenAccessManager implements IPenAccessConfiguration {

	//logger

	private static Logger logger = Logger.getLogger("org.letras.ps.region.penconnector");

	//members

	/**
	 * used access configuration
	 */
	private final PenAccessConfiguration penAccessConfiguration;

	/**
	 * a mapping from penId to PenConnection including all connected pens
	 */
	private final HashMap<String, PenEntry> connectedPens;

	/**
	 * reference to the discoveryCallback in case the shutdown is invoked
	 */
	private final IPenDiscovery penDiscoveryCallback;

	/**
	 * factory responsible for creating the right instances of ISampleProcessor
	 */
	private ISampleProcessorFactory factory;


	//constructor

	/**
	 * Nullary constructor.
	 * Will also set up the environment for the PenAccessManager
	 */
	public PenAccessManager() {
		penAccessConfiguration = new PenAccessConfiguration();
		connectedPens = new HashMap<String, PenEntry>();
		penDiscoveryCallback = new IPenDiscovery() {

			@Override
			public void penConnected(IPen pen) {

			}

			@Override
			public void penDisconnected(IPen pen) {

			}
		};
	}

	// methods

	/**
	 * Starts the discovery service for remote pens. Make sure <code>Mundo.init()</code> has been called beforehand.
	 */
	public void init() {
		Letras.getInstance().registerPenDiscovery(penDiscoveryCallback);
	}

	/**
	 * Set the factory for creating instances of ISampleProcessor for incoming pen connections
	 * @param factory
	 */
	public void setSampleProcessorFactory(ISampleProcessorFactory factory) {
		this.factory = factory;
	}

	/**
	 * Shutdown the discovery service and disconnect all pens
	 */
	public void shutdown() {
		Letras.getInstance().unregisterPenDiscovery(penDiscoveryCallback);
		logger.logp(Level.FINE, "PenAccessManager", "shutdown", "disconnecting the pens");
		for (final PenEntry penEntry : connectedPens.values()) {
			penEntry.pen.unregisterPenListener(penEntry.penListener);
		}
	}

	/**
	 * call-back method for when a new pen has been discovered
	 * @param serviceInfo information about the discovered pen
	 */
	void penDiscovered(PenConnection pen) {
		logger.logp(Level.FINEST, "PenDiscoveryService", "penDiscovered", "New pen tries to connect to the system");

		final String penId = pen.getPenId();
		if (connectedPens.containsKey(penId)) {
			logger.logp(Level.WARNING, "PenDiscoveryService", "penDiscovered", String.format("Pen with PenID %s was already connected", penId));
		} else {
			final SampleProcessorAdapter penListener = new SampleProcessorAdapter(factory.createSampleProcessor(pen));
			connectedPens.put(penId, new PenEntry(pen, penListener));

			if (penAccessConfiguration.hasAccess(penId, pen.getZone())) {
				pen.registerPenListener(penListener);
				logger.logp(Level.FINEST, "PenDiscoveryService", "penDiscovered", String.format("Pen with PenID %s has been granted access", penId));
			} else {
				logger.logp(Level.WARNING, "PenDiscoveryService", "penDiscovered", String.format("Pen with PenID %s has been denied access", penId));
			}
		}
	}

	/**
	 * call-back method for when a pen is lost
	 * @param serviceInfo the information about the lost pen
	 */
	void penLost(PenConnection penConnection) {
		logger.logp(Level.FINEST, "PenDiscoveryService", "penLost", "Pen is disconnecting from the system");

		final String penId = penConnection.getPenId();

		final PenEntry penEntry = connectedPens.remove(penId);
		if (penEntry != null) {
			penEntry.pen.unregisterPenListener(penEntry.penListener);
		}
	}

	/**
	 * recheck a specific pen against the access policy. activate or deactivate the pen if needed
	 * @param penId the ID of the pen to be checked again
	 */
	private void recheckPen(String penId) {
		if (connectedPens.containsKey(penId)) {
			final PenEntry penEntry = connectedPens.get(penId);
			if (penAccessConfiguration.hasAccess(penId, penEntry.pen.getZone())) {
				penEntry.pen.registerPenListener(penEntry.penListener);
			} else {
				penEntry.pen.unregisterPenListener(penEntry.penListener);
			}
		}
	}

	/**
	 * recheck access all pens against the access policy. activate or deactivate pens if needed
	 */
	private void recheckAllPens() {
		for (final String penId : connectedPens.keySet()) {
			recheckPen(penId);
		}

	}

	//implemented interface methods

	@Override
	public void allowPenInGeneral(String penId, boolean propagate) {
		penAccessConfiguration.allowPenInGeneral(penId, propagate);
		recheckPen(penId);
	}

	@Override
	public void allowPenInZone(String penId, String zone) {
		penAccessConfiguration.allowPenInZone(penId, zone);
		recheckPen(penId);
	}

	@Override
	public void deleteRuleForZone(String zone) {
		penAccessConfiguration.deleteRuleForZone(zone);
		recheckAllPens();
	}

	@Override
	public void denyPenInGeneral(String penId, boolean propagate) {
		penAccessConfiguration.denyPenInGeneral(penId, propagate);
		recheckPen(penId);
	}

	@Override
	public void denyPenInZone(String penId, String zone) {
		penAccessConfiguration.denyPenInZone(penId, zone);
		recheckPen(penId);

	}

	@Override
	public TypedMap getRulesAsMap() {
		return penAccessConfiguration.getRulesAsMap();
	}

	@Override
	public boolean loadRulesFromMap(TypedMap ruleMap) {
		final boolean success = penAccessConfiguration.loadRulesFromMap(ruleMap);
		if (success) {
			recheckAllPens();
		}
		return success;
	}

	public void setServiceConfig(TypedMap cfg) {
		if (cfg!=null) {
			final String zone = (cfg.containsKey("pap-zone")?cfg.getString("pap-zone"):null);
			if (zone != null) {
				// TODO:check how service discovery works
				// this.discoveryService.setServiceZone(zone);
			}

			final TypedMap rules = (cfg.containsKey("access")?cfg.getMap("access"):null);
			if (rules != null) {
				this.loadRulesFromMap(rules);
			}
		}
	}

	public class PenEntry {
		public final PenConnection pen;
		public final IPenListener penListener;

		public PenEntry(PenConnection pen, IPenListener penListener) {
			this.pen = pen;
			this.penListener = penListener;
		}
	}
}
