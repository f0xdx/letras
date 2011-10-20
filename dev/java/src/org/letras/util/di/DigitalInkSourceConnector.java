/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.mundo.rt.DoObject;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Subscriber;
import org.mundo.service.ServiceManager;

/**
 * Abstract base class for connectors of sources of digital ink.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public abstract class DigitalInkSourceConnector implements IReceiver{

	// DEFAULTS

	public static final String DEFAULT_ZONE = "lan";
	

	// MEMBERS

	private Subscriber sub;
	private DigitalInkModel model;
	private DoObject source;


	// GETTERS & SETTERS
	
	public DoObject getSource() {
		return source;
	}

	public void setSource(DoObject source) {
		this.source = source;
	}

	public DigitalInkModel getModel() {
		return model;
	}

	public void setModel(DigitalInkModel model) {
		this.model = model;
	}


	// CONSTRUCTORS

	public DigitalInkSourceConnector(DoObject source, DigitalInkModel model) {
		this.model = model;
		this.source = source;
	}


	// METHODS
	
	/**
	 * Connect the source to a given channel in the 
	 * {@link DigitalInkSourceConnector#DEFAULT_ZONE default zone}.
	 * 
	 * @param channel 
	 */
	public void connect(String channel) {
		this.connect(DEFAULT_ZONE, channel);
	}

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


	// ABSTRACT METHODS

	public abstract String sourceChannel();
		
}
