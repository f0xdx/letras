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
import org.letras.api.region.RegionEvent;
import org.letras.ps.region.penconnector.IPenConnection;
import org.letras.psi.iregion.IRegion;
import org.mundo.rt.GUID;
import org.mundo.rt.Logger;
import org.mundo.rt.Mundo;

/**
 * The {@link DigitalInkProcessor} is responsible for ordering and generating
 * {@link RegionEvent}s. Special attention is paid to generating (continuous) trace
 * events when region borders are crossed. The dispatch of events is delegated
 * to the {@link DigitalInkDispatcher}.
 * 
 * @author Jannik Jochem
 *
 */
public class DigitalInkProcessor {
	
	private DigitalInkDispatcher dispatcher;
	/**
	 * The {@link GUID} for the current pair of penDown / penUp events
	 */
	private GUID currentStroke = null;
	
	/**
	 * The {@link GUID} for the current series of continuous trace events.
	 */
	private GUID currentTrace = null;
	
	private List<IRegion> previouslyIntersectedRegions = null;
	
	private static Logger log = Logger.getLogger(DigitalInkProcessor.class);
	
	/**
	 * Simple functor for calling the actual dispatch methods in {@link DigitalInkDispatcher}
	 */
	protected abstract class DispatchFunction {
		public abstract void dispatchTo(IRegion target);
	}
	
	/**
	 * Creates a new {@link DigitalInkProcessor} with an accompanying {@link DigitalInkDispatcher}.
	 * Registers the dispatcher with Mundo.
	 * @param regionManager 
	 * @param pen the pen connection for this processor
	 */
	public DigitalInkProcessor(RegionManager regionManager, IPenConnection pen) {
		dispatcher = new DigitalInkDispatcher(regionManager, pen);
		Mundo.registerService(dispatcher);
	}

	/**
	 * Takes a sample and a post-ordered list of regions that contain the sample and dispatches
	 * the region-normalized sample to all those regions. Also, determines whether a region 
	 * boundary was crossed between this sample and the last sample. If so, 
	 * {@link #processTraceCrossingRegionBorder(List, List)} is called in order to generate and
	 * dispatch the necessary trace events.
	 * 
	 * @param intersectedRegions the post-ordered list of regions that contain the sample
	 * @param sample the sample to process
	 */
	public void processSample(List<IRegion> intersectedRegions, final PenSample sample) {
		if (intersectedRegions.isEmpty()) {
			log.warning("No Region to dispatch sample " + sample + " to.");
			return;
		}
		if (previouslyIntersectedRegions != null 
				&& (previouslyIntersectedRegions.isEmpty() ^ intersectedRegions.isEmpty() 
				|| (!previouslyIntersectedRegions.get(0).equals(intersectedRegions.get(0))))) {
			processTraceCrossingRegionBorder(previouslyIntersectedRegions, intersectedRegions);
		}
		dispatchToLeafAndHungryRegions(intersectedRegions, new DispatchFunction() {
			@Override
			public void dispatchTo(IRegion target) {
				dispatcher.dispatchSample(sample, target);
			}
		});
		previouslyIntersectedRegions = intersectedRegions;
	}
	
	/**
	 * Dispatches a pen down event to all relevant regions and initiates a new (initially
	 * non-continuous) trace.
	 * @param intersectedRegions a post-ordered list of regions that contain the location of the pen down event
	 */
	public void processPenDown(List<IRegion> intersectedRegions) {
		if (currentStroke != null) {
			log.warning("Inconsistent pen down event, forwarding pen down event anyway.");
		}
		currentStroke = new GUID();
		dispatchToLeafAndHungryRegions(intersectedRegions, new DispatchFunction() {
			@Override
			public void dispatchTo(IRegion target) {
				dispatcher.dispatchPenDown(target, currentStroke);
			}
		});
		processTraceStartNonContinuous(intersectedRegions);
	}
	
	/**
	 * Dispatches a pen up event to all relevant regions and closes the current trace
	 * non-continuously.
	 * @param intersectedRegions a post-ordered list of regions that contain the location of the pen up event
	 */
	public void processPenUp(List<IRegion> intersectedRegions) {
		previouslyIntersectedRegions = null;
		processTraceEndNonContinuous(intersectedRegions);
		if (currentStroke == null) {
			log.warning("Inconsistent pen upevent, forwarding pen up event anyway.");
		}
		dispatchToLeafAndHungryRegions(intersectedRegions, new DispatchFunction() {
			@Override
			public void dispatchTo(IRegion target) {
				dispatcher.dispatchPenUp(target, currentStroke);
			}
		});
		currentStroke = null;
	}

	/**
	 * Initiates a new, initially non-continuous trace. This is called when the pen is brought down on the paper.
	 * @param intersectedRegions a post-ordered list of regions that contain the location where the trace
	 * was started
	 */
	protected void processTraceStartNonContinuous(
			List<IRegion> intersectedRegions) {
		if (currentTrace != null) {
			log.warning("Trying to start a new trace when the previous trace has not yet ended. Starting a new trace anyway.");
		}
		currentTrace = new GUID();
		dispatchToLeafAndHungryRegions(intersectedRegions, new DispatchFunction() {
			@Override
			public void dispatchTo(IRegion target) {
				dispatcher.dispatchTraceStart(target, currentTrace, false);
			}
		});
		
	}

	/**
	 * Closes the current trace non-continuously. This is called when the pen is taken up from the paper.
	 * @param intersectedRegions a post-ordered list of regions that contain the location where the trace
	 * was ended
	 */
	protected void processTraceEndNonContinuous(
			List<IRegion> intersectedRegions) {
		if (currentTrace == null) {
			log.warning("Trying to end a trace that was never started. Suppresing trace end event.");
		} else {
			dispatchToLeafAndHungryRegions(intersectedRegions, new DispatchFunction() {
				@Override
				public void dispatchTo(IRegion target) {
					dispatcher.dispatchTraceEnd(target, currentTrace, false);
				}
			});
		}
		currentTrace = null;
	}
	
	/**
	 * This is called when the pen crosses a region border. The following steps are performed:
	 * <ol>
	 * <li>All regions that contained the previous sample are sent a stroke end event. The end event is continuous iff the region also contains the current sample.
	 * <li>All regions that contain the current sample are sent a stroke start event. The start event is continuous iff the region also contains the previous sample.
	 * </ol>
	 * @param previouslyIntersectedRegions a post-ordered list of the locations that contained the last sample that was processed before crossing a border.
	 * @param intersectedRegions a post-ordered list of the locatons that contain the sample that is being processed after crossing the a border.
	 */
	protected void processTraceCrossingRegionBorder(
			final List<IRegion> previouslyIntersectedRegions,
			final List<IRegion> intersectedRegions) {
		dispatchToLeafAndHungryRegions(previouslyIntersectedRegions, new DispatchFunction() {
			@Override
			public void dispatchTo(IRegion target) {
				dispatcher.dispatchTraceEnd(target, currentTrace, intersectedRegions.contains(target) && target.hungry());
			}
		});
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dispatchToLeafAndHungryRegions(intersectedRegions, new DispatchFunction() {
			@Override
			public void dispatchTo(IRegion target) {
				dispatcher.dispatchTraceStart(target, currentTrace, previouslyIntersectedRegions.contains(target) && target.hungry());
			}
		});
	}

	/**
	 * Calls dispatch function for the first element of intersectedRegions as well as any region element that is hungry (for more). 
	 * @param intersectedRegions a post-ordered list of regions from the region model
	 * @param dispatchFunction a functor to call for each region
	 */
	protected void dispatchToLeafAndHungryRegions(List<IRegion> intersectedRegions, 
			DispatchFunction dispatchFunction) {
		if (!intersectedRegions.isEmpty()) {
			dispatchFunction.dispatchTo(intersectedRegions.get(0));
			for (IRegion region: intersectedRegions.subList(1, intersectedRegions.size())) {
				if (region.hungry()) {
					dispatchFunction.dispatchTo(region);
				}
			}
		}
	}
}
