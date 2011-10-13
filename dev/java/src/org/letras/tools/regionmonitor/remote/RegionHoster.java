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
package org.letras.tools.regionmonitor.remote;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.mundo.rt.Mundo;
import org.mundo.rt.Service;

public class RegionHoster extends Service {

	ArrayList<BasicRegionImplementation> hostedRegions = new ArrayList<BasicRegionImplementation>();
	
	
	public void hostNewRegion(Rectangle2D area) {
		final BasicRegionImplementation newRegion = new BasicRegionImplementation(area);
		hostedRegions.add(newRegion);
		newRegion.setServiceZone("lan");
		Mundo.registerService(newRegion);
	}
	
	public void disconnectAllRegions() {
		for (BasicRegionImplementation region : hostedRegions) {
			Mundo.unregisterService(region);
		}
	}
}
