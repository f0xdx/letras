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

import org.mundo.rt.Service;
import org.mundo.service.IConfigure;
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
public class PenDiscoveryService extends Service implements IConfigure {

	//logger
	private static Logger logger = Logger.getLogger("org.letras.ps.region.penconnector");
	
	//members
	
	/**
	 * the object to inform when a pen is discovered or lost
	 */
	private PenAccessManager delegate;

	//setter and getter
	
	void setDelegate(PenAccessManager delegate) {
		this.delegate = delegate;
	}
	
	//constructor
	
	/**
	 * nullary constructor
	 */
	public PenDiscoveryService() {
		
	}
	
	//methods
	
	@Override
	public void init() {
		ServiceInfoFilter serviceInfoFilter = new ServiceInfoFilter();
		serviceInfoFilter.filterInterface("org.letras.psi.ipen.IPen");

		try {
			ServiceManager.getInstance().contQuery(serviceInfoFilter, this.getSession(), new ResultSet.ISignal() {

				@Override
				public void removing(ResultSet rs, int offset, int n) {
					for (Object obj  : rs.getList().subList(offset, offset + n)) {
						if (obj instanceof ServiceInfo) {
							delegate.penLost((ServiceInfo) obj);							
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
					for (Object obj  : rs.getList().subList(offset, offset + n)) {
						if (obj instanceof ServiceInfo) {
							delegate.penDiscovered((ServiceInfo) obj);
						}
					}
				}
			});
		} catch (Exception e) {
			logger.logp(Level.SEVERE, "PenDiscoveryService", "init", e.getMessage());
		}
	}
}
