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

import org.letras.ps.region.sampleprocessor.ISampleProcessorFactory;
import org.mundo.rt.Mundo;
import org.mundo.rt.TypedMap;
import org.mundo.service.ServiceInfo;

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
	private PenAccessConfiguration penAccessConfiguration;
	
	/**
	 * a mapping from penId to PenConnection including all connected pens
	 */
	private HashMap<String, PenConnection> connectedPens;
	
	/**
	 * reference to the discoveryService in case the shutdown is invoked
	 */
	private PenDiscoveryService discoveryService;
	
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
		connectedPens = new HashMap<String, PenConnection>();
		discoveryService = new PenDiscoveryService();
		discoveryService.setDelegate(this);
	}
	
	//methods
	
	/**
	 * Starts the discovery service for remote pens. Make sure <code>Mundo.init()</code>
	 * has been called beforehand.
	 */
	public void init() {
		setUpDiscoveryService();
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
		logger.logp(Level.FINE, "PenAccessManager", "shutdown", "shutting down the pen discovery service");
		discoveryService.shutdown();
		logger.logp(Level.FINE, "PenAccessManager", "shutdown", "disconnecting the pens");
		for (PenConnection pen : connectedPens.values()) {
			pen.deactivatePen();
		}
	}
	
	/**
	 * set up a discovery service and connect it to Mundo
	 */
	private void setUpDiscoveryService() {
		Mundo.registerService(discoveryService);
	}
	
	/**
	 * call-back method for when a new pen has been discovered
	 * @param serviceInfo information about the discovered pen
	 */
	void penDiscovered(ServiceInfo serviceInfo) {
		logger.logp(Level.FINEST, "PenDiscoveryService", "penDiscovered", "New pen tries to connect to the system");
		
		PenConnection penConnection = PenConnection.createPenConnectionFromServiceInfo(serviceInfo);
		
		final String penId = penConnection.getPenId();
		if (connectedPens.containsKey(penId)) {
			logger.logp(Level.WARNING, "PenDiscoveryService", "penDiscovered", String.format("Pen with PenID %s was already connected", penId));
		} else {
			connectedPens.put(penId, penConnection);
			
			if (penAccessConfiguration.hasAccess(penId, penConnection.getZone())) {
				penConnection.activatePen(discoveryService.getSession(), factory.createSampleProcessor(penConnection));
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
	void penLost(ServiceInfo serviceInfo) {
		logger.logp(Level.FINEST, "PenDiscoveryService", "penLost", "Pen is disconnecting from the system");
		
		PenConnection penConnection = PenConnection.createPenConnectionFromServiceInfo(serviceInfo);
		
		final String penId = penConnection.getPenId();
		
		if (connectedPens.containsKey(penId)) {
			penConnection = connectedPens.remove(penId);		
			if (penConnection.isActive()) {
				penConnection.deactivatePen();
			}
			logger.logp(Level.FINEST, "PenDiscoveryService", "penLost", String.format("Pen with PenID %s has been disconnected", penId));
		} else {
			logger.logp(Level.WARNING, "PenDiscoveryService", "penLost", String.format("Pen with PenID %s was not connected", penId));
		}
	}

	/**
	 * recheck a specific pen against the access policy. activate or deactivate the pen if needed
	 * @param penId the ID of the pen to be checked again
	 */
	private void recheckPen(String penId) {
		if (connectedPens.containsKey(penId)) {
			PenConnection penConnection = connectedPens.get(penId);
			if (penAccessConfiguration.hasAccess(penId, penConnection.getZone())) {
				if (!penConnection.isActive()) {
					penConnection.activatePen(discoveryService.getSession(), factory.createSampleProcessor(penConnection));
				}
			} else {
				if (penConnection.isActive()) {
					penConnection.deactivatePen();
				}
			}
		}
	}

	/**
	 * recheck access all pens against the access policy. activate or deactivate pens if needed
	 */
	private void recheckAllPens() {
		for (String penId : connectedPens.keySet()) {
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
		boolean success = penAccessConfiguration.loadRulesFromMap(ruleMap);
		if (success) {
			recheckAllPens();
		}
		return success;
	}
	
	public void setServiceConfig(TypedMap cfg) {
		if (cfg!=null) {
			String zone = (cfg.containsKey("pap-zone")?cfg.getString("pap-zone"):null);
			if (zone != null) {
				this.discoveryService.setServiceZone(zone);
			}
			
			TypedMap rules = (cfg.containsKey("access")?cfg.getMap("access"):null);
			if (rules != null) {
				this.loadRulesFromMap(rules);
			}
		}		
	}
}
