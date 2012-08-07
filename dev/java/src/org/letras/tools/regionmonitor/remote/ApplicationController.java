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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.api.pen.IPenState;
import org.letras.api.pen.PenSample;
import org.letras.ps.region.RegionManager;
import org.letras.ps.region.RegionSampleProcessor;
import org.letras.ps.region.RegionTreeNode;
import org.letras.ps.region.broker.simple.SimpleRegionBroker;
import org.letras.ps.region.penconnector.PenConnection;
import org.letras.psi.ipen.IPen;
import org.letras.psi.iregion.IRegion;
import org.mundo.rt.Mundo;

public class ApplicationController {

	static {
		Logger logger = Logger.getLogger("org.letras.ps.region");
		logger.setLevel(Level.FINEST);
		logger.setUseParentHandlers(false);
		logger.addHandler(new ConsoleHandler());
	}
	
	class InstrumentedRegionManager extends RegionManager {
		
		public InstrumentedRegionManager() {
			super();
		}
		
		@Override
		public void addRegion(IRegion regionToAdd) {
			super.addRegion(regionToAdd);
			delegate.updateView();
		}
		
		@Override
		public void updateRegion(IRegion regionToUpdate) {
			super.updateRegion(regionToUpdate);
		}
		
		@Override
		public void deleteRegion(IRegion regionToDelete) {
			super.deleteRegion(regionToDelete);
		}
		
		public RegionTreeNode getTopLevelRegionTreeNode() {
			return super.getModel();
		}
		
	}
	
	class SimplifiedRegionSampleProcessor {
		
		IRegion lastIntersectingRegion = null; 
		
		public List<IRegion> handleSample(PenSample sample) {
			List<IRegion> regions = regionManager.getIntersectingRegionInfos(lastIntersectingRegion, sample);
			
			// cache last hit if hit occured
			if (!regions.isEmpty())
				lastIntersectingRegion = regions.get(0);
			return regions;
		}
	}
	
	
	IPen MockIpen = new IPen() {
		
		@Override
		public int penState() {return IPenState.ON;}
		
		@Override
		public String penId() {
			return "virtual";
		}
		
		@Override
		public String channel() {
			return "virtual.pen";
		}
	};
	
	RegionSampleProcessor regionSampleProcessor;
	InstrumentedRegionManager regionManager;
	SimplifiedRegionSampleProcessor simpleRegionSampleProcessor = new SimplifiedRegionSampleProcessor();
	RegionHoster regionHoster;
	SimpleRegionBrokerAppMode delegate;
	private SimpleRegionBroker regionBroker;
	
	public ApplicationController(SimpleRegionBrokerAppMode delegate) {
		this.delegate = delegate;
		regionManager = new InstrumentedRegionManager();
	}
	
	void activate() {
		if (Mundo.getState() == Mundo.STATE_UNINITIALIZED) 
			Mundo.init();
		regionBroker = new SimpleRegionBroker();
		regionManager.setBroker(regionBroker);
		regionManager.setServiceZone("lan");
		regionBroker.setRegionManager(regionManager);
		this.regionSampleProcessor = new RegionSampleProcessor(regionManager, new PenConnection(MockIpen, "lan"));
		Mundo.registerService(regionBroker);
		regionHoster = new RegionHoster();
	}
	
	void deactivate() {
		regionHoster.disconnectAllRegions();
		Mundo.unregisterService(regionBroker);
		regionBroker = null;
		regionManager = null;
	}
	
	public void discoverRegionsAt(Point2D.Double point) {
		final PenSample sample = new PenSample(point.x, point.y, 128, System.currentTimeMillis());
		simpleRegionSampleProcessor.handleSample(sample);
		delegate.updateView();
	}
	
	public void createNewRegion(Rectangle2D area) {
		regionHoster.hostNewRegion(area);	
	}
	
	public RegionTreeNode getTopLevelRegionTreeNode() {
		return this.regionManager.getTopLevelRegionTreeNode();
	}


	public void penDown() {
		regionSampleProcessor.penDown();
	}


	public void penAtPosition(Point2D.Double point) {
		final PenSample sample = new PenSample(point.x, point.y, 128, System.currentTimeMillis());
		regionSampleProcessor.handleSample(sample);
	}


	public void penUp() {
		regionSampleProcessor.penUp();
	}
}
