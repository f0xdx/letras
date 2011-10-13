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

import java.util.LinkedList;
import java.util.List;

import org.letras.psi.iregion.IDigitalInkConsumer;
import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.RegionEvent;
import org.letras.psi.iregion.RegionMessage;
import org.letras.psi.iregion.RegionMessageProcessor;
import org.letras.psi.iregion.RegionSample;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Subscriber;

public class RegionAdapter implements IReceiver, RegionMessageProcessor {
	protected List<IDigitalInkConsumer> consumers;
	private IRegion region;
	private String channel;
	private Subscriber subscriber;
	private RegionAdapterFactory owningFactory;
	
	protected RegionAdapter(IRegion region, RegionAdapterFactory owningFactory) {
		this.region = region;
		this.owningFactory = owningFactory;
		consumers = new LinkedList<IDigitalInkConsumer>();
	}
	
	/**
	 * Adds a consumer that should receive this region's {@link RegionMessage}s.
	 * @param consumer a new consumer for this region
	 */
	public void addConsumer(IDigitalInkConsumer consumer) {
		consumers.add(consumer);
		if (subscriber == null) {
			channel = region.channel();
			subscriber = owningFactory.getSession().subscribe(owningFactory.getServiceZone(), channel, this);
		}
	}
	
	public void removeConsumer(IDigitalInkConsumer consumer) {
		if (consumers.size() < 1) {
			subscriber.unsubscribe();
			subscriber = null;
			channel = null;
		}
	}
	
	public String getChannel() {
		return channel;
	}
	
	public void switchChannel() {
		String newChannel = region.channel();
		if (subscriber != null && !channel.equals(newChannel)) {
			subscriber.unsubscribe();
			channel = newChannel;
			subscriber = owningFactory.getSession().subscribe(owningFactory.getServiceZone(), newChannel, this);
		}
	}
	
	@Override
	public void received(Message msg, MessageContext ctx) {
		if (msg.getObject() instanceof RegionMessage) {
			((RegionMessage) msg.getObject()).accept(this);
		}
	}
	
	@Override
	public void process(RegionEvent regionEvent) {
		for (IDigitalInkConsumer consumer: consumers)
			consumer.consume(region, regionEvent);
	}
	
	@Override
	public void process(RegionSample regionSample) {
		for (IDigitalInkConsumer consumer: consumers)
			consumer.consume(region, regionSample);
	}

	protected IRegion getRegion() {
		return region;
	}
	
	protected void setRegion(IRegion newRegion) {
		this.region = newRegion;
		switchChannel();
	}
}
