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
package org.letras.ps.region;

import java.util.List;

import org.letras.api.pen.PenSample;
import org.letras.ps.region.penconnector.IPenConnection;
import org.letras.ps.region.penconnector.ISampleProcessor;
import org.letras.psi.iregion.IRegion;

/**
 * The {@link RegionSampleProcessor} contains a region model and dispatches
 * samples to the applications that are registered on the model elements.
 * 
 * The connection between model regions and applications is provided by {@link IRegion}s
 * 
 * @author Jannik Jochem
 *
 */
public class RegionSampleProcessor implements ISampleProcessor {
	// members
	
	private IRegion lastIntersectingRegion;
	private List<IRegion> lastIntersectingRegions;
	private RegionManager regionManager;
	private DigitalInkProcessor digitalInkProcessor;
	private boolean penDownQueued;
	
	// constructors
	
	public RegionSampleProcessor(RegionManager regionManager, IPenConnection pen) {
		this.regionManager = regionManager;
		this.digitalInkProcessor = new DigitalInkProcessor(regionManager, pen);
	}

	/* 
	 * This queries the model for the region the sample is contained within.
	 * Starts from the previously intersected region (i.e., "caching" is employed).
	 * Calls doDispatch for all Regions with a non-null DispatchInfo.
	 * (non-Javadoc)
	 * @see org.letras.ps.region.penconnector.ISampleProcessor#handleSample(org.letras.psi.ipen.PenSample)
	 */
	@Override
	public void handleSample(final PenSample sample) {
		List<IRegion> regions = regionManager.getIntersectingRegionInfos(lastIntersectingRegion, sample);

		if (penDownQueued) {
			digitalInkProcessor.processPenDown(regions);
			penDownQueued = false;
		}

		digitalInkProcessor.processSample(regions, sample);

		if (!regions.isEmpty()) {

			// cache last hit
			lastIntersectingRegions = regions;
			lastIntersectingRegion = regions.get(0);
		}
	}

	/* 
	 * Calls doDispatchPenDown for all Regions on a path from the root node to
	 * the last intersected region that have a non-null DispatchInfo. 
	 * (non-Javadoc)
	 * @see org.letras.ps.region.penconnector.ISampleProcessor#penDown()
	 */
	@Override
	public void penDown() {
		// we need to postpone sending the pen down event until we know where it happened
		// so we can dispatch it to the correct regions
		penDownQueued = true;
	}

	/* 
	 * Calls doDispatchPenDown for all Regions on a path from the root node to
	 * the last intersected region that have a non-null DispatchInfo. 
	 * (non-Javadoc)
	 * @see org.letras.ps.region.penconnector.ISampleProcessor#penUp()
	 */
	@Override
	public void penUp() {
		if (lastIntersectingRegions != null) {
			digitalInkProcessor.processPenUp(lastIntersectingRegions);
		}
	}

	@Override
	public void setConnectedPen(IPenConnection pen) {
		throw new UnsupportedOperationException("Cannot change the pen of the RegionSampleProcessor after initial setup. Use a new RegionSampleProcessor instead!");
	}

}
