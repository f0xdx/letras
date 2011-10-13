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
package org.letras.tools.penmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.letras.psi.ipen.DoIPen;
import org.mundo.rt.Service;
import org.mundo.rt.Subscriber;
import org.mundo.service.ResultSet;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;

/**
 * The PenListener is responsible for handling newly discovered pens.
 * The pens are discovered by using a continuous service query on the <code>ServiceManager</code>
 * @author niklas
 */
public class PenListener extends Service {
	
	//members
	
	PenTableModel penTableModel;
	ServiceManager serviceManager;
	
	HashMap<ServiceInfo, PenInformation> penMap;
	HashMap<ServiceInfo, Subscriber> subMap;
	
	/**
	 * Default Constructor
	 * @param penTable where discovery events will be relayed to
	 */
	public PenListener(PenTableModel penTable) {
		penTableModel = penTable;
		penMap = new HashMap<ServiceInfo, PenInformation>();
		subMap = new HashMap<ServiceInfo, Subscriber>();
	}
	
	//methods
	
	@Override
	public void init() {
		ServiceInfoFilter serviceInfoFilter = new ServiceInfoFilter();
		serviceInfoFilter.filterInterface("org.letras.psi.ipen.IPen");
		
		try {
			ServiceManager.getInstance().contQuery(serviceInfoFilter, getSession(), new ResultSet.ISignal() {
				
				@Override
				public void removing(ResultSet arg0, int arg1, int arg2) {
					for (Object obj : arg0.getList().subList(arg1, arg1+arg2)) {
						//ResultSet should return List<ServiceInfo> but I check it anyway
						if (obj instanceof ServiceInfo) {
							final ServiceInfo serviceInfo = (ServiceInfo) obj;
							if (subMap.containsKey(serviceInfo)) {
								Subscriber subcriber = subMap.get(serviceInfo);
								getSession().unsubscribe(subcriber);
							}
							if (penMap.containsKey(serviceInfo)) {
								PenInformation penInfo = penMap.get(serviceInfo);
								penTableModel.delete(penInfo);
							}
						}
					}
				}
				
				@Override
				public void removed(ResultSet arg0, int arg1, int arg2) {
					//Nothing todo here
				}
				
				@Override
				public void propChanging(ResultSet arg0, int arg1) {
					//Nothing todo here
				}
				
				@Override
				public void propChanged(ResultSet arg0, int arg1) {
					//Nothing todo here
				}
				
				@Override
				public void inserted(ResultSet arg0, int arg1, int arg2) {
					List<PenInformation> discoveredPens = new ArrayList<PenInformation>(arg2);
					
					for (Object obj : arg0.getList().subList(arg1, arg1+arg2)) {
						//ResultSet should return List<ServiceInfo> but I check it anyway
						if (obj instanceof ServiceInfo) {
							final ServiceInfo serviceInfo = (ServiceInfo) obj;
						    final DoIPen doIPen = new DoIPen(serviceInfo.doService);
							final PenInformation penInfo = new PenInformation(doIPen, serviceInfo.nodeId);
							final Subscriber sub = getSession().subscribe(serviceInfo.zone, doIPen.channel(), penInfo);
							subMap.put(serviceInfo, sub);
							discoveredPens.add(penInfo);
							penMap.put(serviceInfo, penInfo);
						}
					}
					penTableModel.add(discoveredPens);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
