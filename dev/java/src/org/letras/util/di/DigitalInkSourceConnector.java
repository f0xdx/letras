/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.mundo.rt.IReceiver;
import org.mundo.rt.Subscriber;
import org.mundo.service.ServiceManager;

/**
 *
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 */
public abstract class DigitalInkSourceConnector implements IReceiver{

	// MEMBERS

	private Subscriber sub;
	private DigitalInkModel model;

	public DigitalInkModel getModel() {
		return model;
	}

	public void setModel(DigitalInkModel model) {
		this.model = model;
	}

	// CONSTRUCTORS

	public DigitalInkSourceConnector(DigitalInkModel model) {
		this.model = model;
	}

	// METHODS
	
	/**
	 * Connect the source connector to a given channel in a given zone.
	 * 
	 * @param zone
	 * @param channel 
	 */
	public void connect(String zone, String channel) {
		this.disconnect();
		this.sub = ServiceManager.getInstance().getSession().subscribe(zone, channel, this);
	}

	/**
	 * Disconnect the source connector from its channel.
	 */
	public void disconnect() {
		if (this.sub != null) this.sub.unsubscribe();
	}
}
