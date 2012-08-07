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

import org.letras.api.region.RegionData;
import org.letras.api.region.RegionEvent;
import org.letras.api.region.RegionSample;
import org.letras.psi.iregion.IDigitalInkConsumer;
import org.letras.psi.iregion.IRegion;

public class RegionDocumentReceiver implements IDigitalInkConsumer, IRegionDocumentListener {
	
	private IDigitalInkConsumer activeConsumer;
	private RegionDocument document;

	public RegionDocumentReceiver(RegionDocument document) {
		this.document = document;
		this.document.addDocumentListener(this);
		for (RegionData region: document.getRegions()) {
			RegionAdapterFactory.getInstance().adapt(region).addConsumer(this);
		}
	}
	
	public void setActiveConsumer(IDigitalInkConsumer activeConsumer) {
		this.activeConsumer = activeConsumer;
	}
	
	@Override
	public void consume(IRegion source, RegionSample regionSample) {
		if (activeConsumer != null)
			activeConsumer.consume(source, regionSample);
	}

	@Override
	public void consume(IRegion source, RegionEvent regionEvent) {
		if (activeConsumer != null)
			activeConsumer.consume(source, regionEvent);
	}
	
	public void shutdown() {
		for (RegionData region: document.getRegions()) {
			RegionAdapterFactory.getInstance().adapt(region).removeConsumer(this);
		}
	}

	@Override
	public void regionAdded(RegionData region) {
		RegionAdapterFactory.getInstance().adapt(region).addConsumer(this);
	}

	@Override
	public void regionRemoved(RegionData region) {
		RegionAdapterFactory.getInstance().adapt(region).removeConsumer(this);
	}

	@Override
	public void regionModified(RegionData oldRegion, RegionData newRegion) {
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
