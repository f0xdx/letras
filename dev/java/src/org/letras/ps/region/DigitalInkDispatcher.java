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

import java.util.Map;
import java.util.WeakHashMap;

import org.letras.api.pen.PenSample;
import org.letras.api.region.RegionEvent;
import org.letras.api.region.RegionSample;
import org.letras.psi.iregion.IRegion;
import org.mundo.rt.GUID;
import org.mundo.rt.Message;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;

/**
 * The {@link DigitalInkDispatcher} sends {@link RegionSample}s and {@link RegionEvent}s to
 * the channels of {@link IRegion}s. It hides the Mundo middleware from the rest of the
 * RegionProcessingStage. Objects of this class are meant to be used by a single thread.
 * 
 * @author Jannik Jochem
 *
 */
public class DigitalInkDispatcher extends Service {
	private final Map<String, Publisher> channelToPublishers = new WeakHashMap<String, Publisher>();
	private final RegionManager regionManager;
	private final String penId;

	public DigitalInkDispatcher(RegionManager regionManager, String penId) {
		this.regionManager = regionManager;
		this.penId = penId;
	}

	/**
	 * Sends sample to the channel of region.
	 * @param sample
	 * @param target
	 */
	public void dispatchSample(PenSample sample, IRegion target) {
		final RegionSample normalizedSample = new RegionSample(sample, target.shape().getBounds(), getPenId());
		final Publisher publisher = getPublisherLazy(target.channel());
		publisher.send(Message.fromObject(normalizedSample));
	}

	/**
	 * Dispatches a pen down event to region.
	 * @param region the region to dispatch the event to
	 * @param guid the guid for the stroke (links connected pen down and pen up events)
	 */
	public void dispatchPenDown(IRegion region, GUID guid) {
		final RegionEvent event = RegionEvent.createPenDownEvent(guid, getPenId());
		publishEvent(region, event);
	}

	/**
	 * Dispatches a pen up event to region
	 * @param region the region to dispatch the event to
	 * @param guid the guid for the stroke (links connected pen down and pen up events)
	 */
	public void dispatchPenUp(IRegion region, GUID guid) {
		final RegionEvent event = RegionEvent.createPenUpEvent(guid, getPenId());
		publishEvent(region, event);
	}

	/**
	 * Dispatch a trace start event to region
	 * @param region the region to dispatch the event to
	 * @param guid the guid for the set of continuous traces
	 * @param continues whether to create a continuing trace event
	 */
	public void dispatchTraceStart(IRegion region, GUID guid, boolean continues) {
		final RegionEvent event = continues ? RegionEvent.createContinuingTraceStartEvent(guid, getPenId())
				: RegionEvent.createTraceStartEvent(guid, getPenId());
		publishEvent(region, event);
	}

	/**
	 * Dispatch a trace end event to region
	 * @param region the region to dispatch the event to
	 * @param guid the guid for the set of continuous traces
	 * @param continues whether to create a continuing trace event
	 */
	public void dispatchTraceEnd(IRegion region, GUID guid, boolean continues) {
		final RegionEvent event = continues ? RegionEvent.createContinuingTraceEndEvent(guid, getPenId())
				: RegionEvent.createTraceEndEvent(guid, getPenId());
		publishEvent(region, event);
	}

	/**
	 * Sends event to the channel of region.
	 * @param region
	 * @param event
	 */
	protected void publishEvent(IRegion region, RegionEvent event) {
		final Publisher publisher = getPublisherLazy(region.channel());
		publisher.send(Message.fromObject(event));
	}

	private String getPenId() {
		return penId;
	}

	/**
	 * Caching lazy-init method for retrieving the publisher for a channel.
	 * @param channel the channel name to retrieve a publisher for
	 * @return a publisher for sending to channel
	 */
	private Publisher getPublisherLazy(String channel) {
		Publisher publisher = channelToPublishers.get(channel);
		if (publisher == null) {
			publisher = getSession().publish(regionManager.getServiceZone(), channel);
			channelToPublishers.put(channel, publisher);
		}
		return publisher;
	}
}
