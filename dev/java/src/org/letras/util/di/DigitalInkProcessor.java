/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.mundo.rt.IReceiver;
import org.mundo.rt.Subscriber;
import org.mundo.service.ServiceManager;

/**
 * Abstract base class for digital ink processors. Concrete implementations will
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

	private Subscriber sub;

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
	 */
	public void connect(String zone, String channel) {
		this.sub = ServiceManager.getInstance().getSession().subscribe(zone, channel, this);
	}
	
	protected 
}
