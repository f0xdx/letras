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
package org.letras.util.region.document;

import java.util.HashMap;

import org.letras.psi.iregion.RegionData;
import org.mundo.rt.Mundo;

public class RegionDocumentPublisher implements IRegionDocumentListener  {
	
	private final static String DEFAULT_SERVICE_ZONE = "lan";
	private RegionDocument document;
	private final String serviceZone;
	private HashMap<String, RegionImpl> regionServiceMap = new HashMap<String, RegionImpl>();
	
	public RegionDocumentPublisher(RegionDocument document) {
		this(document, DEFAULT_SERVICE_ZONE);
	}
	
	public RegionDocumentPublisher(RegionDocument document, String serviceZone) {
		this.document = document;
		this.serviceZone = serviceZone;
		document.addDocumentListener(this);
		publishAllRegions();
	}
	
	private void publishRegion(RegionData region) {
		final RegionImpl regionService = RegionImpl.from(region);
		regionServiceMap.put(region.uri(), regionService);
		regionService.setServiceZone(serviceZone);
		Mundo.registerService(regionService);
	}
	
	private void unpublishRegion(RegionData region) {
		Mundo.unregisterService(regionServiceMap.get(region.uri()));
	}
	
	public void shutdown() {
		for (RegionData region: document.getRegions()) {
			unpublishRegion(region);
		}
	}
	
	private void publishAllRegions() {
		for (RegionData region: document.getRegions()) {
			publishRegion(region);
		}
	}

	@Override
	public void regionAdded(RegionData region) {
		publishRegion(region);
	}

	@Override
	public void regionRemoved(RegionData region) {
		unpublishRegion(region);
	}

	@Override
	public void regionModified(RegionData oldRegion, RegionData newRegion) {
		try {
			unpublishRegion(oldRegion);
		} catch (Exception e) {
		}
		publishRegion(newRegion);
	}

	@Override
	public void modificationStateChanged() {
	}

	@Override
	public void documentNameChanged() {
	}

	@Override
	public void pageChanged(RegionData newPage) {
	}
	
}
