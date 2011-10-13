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
package org.letras.tools.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.mundo.rt.Service;
import org.mundo.service.ResultSet;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;

public class PenDiscoveryService extends Service {
	
	private List<IPenDiscoveryListener> listeners = new LinkedList<IPenDiscoveryListener>();

	@Override
	public void init() {

		ServiceInfoFilter serviceInfoFilter = new ServiceInfoFilter();
		serviceInfoFilter.filterInterface("org.letras.psi.ipen.IPen");

		try {
			ServiceManager.getInstance().contQuery(serviceInfoFilter,
					getSession(), new ResultSet.ISignal() {

						@Override
						public void removing(ResultSet arg0, int arg1, int arg2) {
						}

						@Override
						public void removed(ResultSet arg0, int arg1, int arg2) {
							firePensChanged(arg0);
						}

						@Override
						public void propChanging(ResultSet arg0, int arg1) {
							// Nothing todo here
						}

						@Override
						public void propChanged(ResultSet arg0, int arg1) {
							// Nothing todo here
						}

						@Override
						public void inserted(ResultSet arg0, int arg1, int arg2) {
							firePensChanged(arg0);
						}

					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	protected void firePensChanged(ResultSet resultSet) {
		List<ServiceInfo> serviceInfos = resultSet.getList();
		for (IPenDiscoveryListener listener: listeners)
			listener.availablePensChanged(new ArrayList<ServiceInfo>(serviceInfos));
	};
	
	public void addPenDiscoveryListener(IPenDiscoveryListener listener) {
		listeners.add(listener);
	}
	
	public void removePenDiscoveryListener(IPenDiscoveryListener listener) {
		listeners.remove(listener);
	}
}
