/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import java.util.HashMap;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Subscriber;
import org.mundo.service.ServiceManager;

/**
 * Base class for digital ink processing. Concrete implementations will
 * interface one of the several types of digital ink that is provided by the
 * letras pipeline. In order to use a digital ink processor, one should obtain
 * the concrete digital ink processor using the {@link DigitalInkProcessorFactory}.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public abstract class DigitalInkProcessor implements IReceiver {

	// DEFAULTS

	public static final String DEFAULT_ZONE = "lan";

	// MEMBERS

	private HashMap<String, Subscriber> subscriptions;

	// CONSTRUCTORS

	// METHODS

	/**
	 * Called to connect this processor to a channel where digtial ink is 
	 * published.
	 * 
	 * @param channel 
	 */
	public void connect(String channel) {
		this.connect(DEFAULT_ZONE, channel);
	}

	/**
	 * Called to connect this processor to a channel in a given zone where digital
	 * ink is published.
	 * 
	 * @param channel
	 * @param zone 
	 * @return <code>true</code> iff this processor could connect to the given
	 * channel, <code>false</code> if it was already subscribed
	 */
	public boolean connect(String zone, String channel) {
		if (this.subscriptions.containsKey(channel)) return false;
		else {
			this.subscriptions.put(channel, ServiceManager.getInstance().
					getSession().subscribe(zone, channel, this));
			return true;
		}
	}

	/**
	 * Called to disconnect this processor from a channel.
	 * 
	 * @param channel
	 * @return <code>true</code> iff this processor could disconnect from the given
	 * channel, <code>false</code> if it was not subscribed
	 */
	public boolean disconnect(String channel) {
		if (this.subscriptions.containsKey(channel)) {
			this.subscriptions.remove(channel).unsubscribe();
			return true;
		}
		else return false;
	}
	
}
