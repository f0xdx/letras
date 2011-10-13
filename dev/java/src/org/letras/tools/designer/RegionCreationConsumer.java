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
package org.letras.tools.designer;

import org.letras.psi.iregion.IDigitalInkConsumer;
import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.RegionData;
import org.letras.psi.iregion.RegionEvent;
import org.letras.psi.iregion.RegionSample;
import org.letras.psi.iregion.shape.RectangularShape;
import org.letras.util.region.document.RegionDocument;
import org.letras.util.region.document.RegionDocumentReceiver;

public class RegionCreationConsumer implements IDigitalInkConsumer {
	private boolean drawing;
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	private RegionDocument document;
	private IInvalidRegionCallback invalidRegionCallback = null;
	private RegionDocumentReceiver receiver;
	
	public RegionCreationConsumer(RegionDocument document) {
		this.document = document;
		this.receiver = new RegionDocumentReceiver(document);
		receiver.setActiveConsumer(this);
	}
	
	@Override
	public void consume(IRegion source, RegionSample regionSample) {
		if (drawing) {
			minX = Math.min(minX, regionSample.getPscX());
			minY = Math.min(minY, regionSample.getPscY());
			maxX = Math.max(maxX, regionSample.getPscX());
			maxY = Math.max(maxY, regionSample.getPscY());
		}	
	}

	@Override
	public void consume(IRegion source, RegionEvent regionEvent) {
//		if (regionEvent.continues(regionEvent.getGuid()) && source == document.getPage()) {
//			drawing = false;
//			
//		}
		if (!drawing && regionEvent.penDown()) {
			drawing = true;
			minX = Double.POSITIVE_INFINITY;
			minY = Double.POSITIVE_INFINITY;
			maxX = Double.NEGATIVE_INFINITY;
			maxY = Double.NEGATIVE_INFINITY;
		}
		else if (drawing && regionEvent.penUp()) {
			drawing = false;
			createRegion(minX, minY, maxX - minX, maxY - minY);
		}
	}
	
	public void createRegion(double x, double y, double width, double height) {
		RegionData newRegion = new RegionData(document.generateRegionUri(), false, new RectangularShape(x, y, width, height));
		if (document.regionFits(newRegion))
			document.addRegion(newRegion);
		else if (invalidRegionCallback != null)
			invalidRegionCallback.invalidRegionDrawn();
		
	}
	
	public void setInvalidRegionCallback(IInvalidRegionCallback invalidRegionCallback) {
		this.invalidRegionCallback = invalidRegionCallback;
	}
	
	public void shutdown() {
		receiver.shutdown();
	}
	
}
