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
package org.letras.api.pen.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.api.pen.IPenDiscovery;
import org.letras.ps.region.penconnector.PenAccessManager;
import org.letras.ps.region.penconnector.PenConnection;
import org.mundo.service.ResultSet;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;

/**
 * The PenDiscoveryService is responsible for discovering all reachable pens. When a pen
 * is discovered or a discovered pen does not exist anymore the {@link PenAccessManager} gets a
 * notification over the functions <code>penDiscovered</code> and <code>penLost</code>.
 * @author niklas
 * @version 0.0.1
 */
public class PenDiscovery {

	//logger
	private static Logger logger = Logger.getLogger("org.letras.api.pen");

	//members

	/**
	 * the listeners to inform when a pen is discovered or lost
	 */
	private final HashSet<IPenDiscovery> listeners = new HashSet<IPenDiscovery>();

	/**
	 * the pens already discovered
	 */
	private final HashMap<ServiceInfo, PenConnection> pens = new HashMap<ServiceInfo, PenConnection>();


	//constructor

	/**
	 * nullary constructor
	 */
	public PenDiscovery() {

	}

	//methods

	public void start() {
		final ServiceInfoFilter serviceInfoFilter = new ServiceInfoFilter();
		serviceInfoFilter.filterInterface("org.letras.psi.ipen.IPen");

		try {
			final ServiceManager instance = ServiceManager.getInstance();
			instance.contQuery(serviceInfoFilter, instance.getSession(), new ResultSet.ISignal() {

				@Override
				public void removing(ResultSet rs, int offset, int n) {
					for (final Object obj : rs.getList().subList(offset, offset + n)) {
						if (obj instanceof ServiceInfo) {
							synchronized (listeners) {
								final ServiceInfo penInfo = (ServiceInfo) obj;
								if (pens.containsKey(penInfo)) {
									final PenConnection pen = pens.remove(penInfo);
									for (final IPenDiscovery listener : listeners) {
										listener.penDisconnected(pen);
									}
								}
							}
						}
					}
				}

				@Override
				public void removed(ResultSet rs, int offset, int n) {/*Nothing to do here*/}

				@Override
				public void propChanging(ResultSet rs, int offset) {/*Nothing to do here*/}

				@Override
				public void propChanged(ResultSet rs, int offset) {/*Nothing to do here*/}

				@Override
				public void inserted(ResultSet rs, int offset, int n) {
					for (final Object obj  : rs.getList().subList(offset, offset + n)) {
						if (obj instanceof ServiceInfo) {
							synchronized (listeners) {
								final ServiceInfo penInfo = (ServiceInfo) obj;
								final PenConnection pen = PenConnection.createPenConnectionFromServiceInfo(penInfo);
								if (!pens.containsKey(penInfo)) {
									pens.put(penInfo, pen);
									for (final IPenDiscovery listener : listeners) {
										listener.penConnected(pen);
									}
								}
							}
						}
					}
				}
			});
		} catch (final Exception e) {
			logger.logp(Level.SEVERE, "PenDiscoveryService", "start", e.getMessage());
		}
	}

	public void registerPenDiscoveryListener(IPenDiscovery listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
				for (final PenConnection pen : pens.values()) {
					listener.penConnected(pen);
				}
			}
		}
	}

	public void unregisterPenDiscoveryListener(IPenDiscovery listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
}
