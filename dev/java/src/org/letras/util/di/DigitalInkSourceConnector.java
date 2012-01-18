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

	private IDigitalInkSourceObserver observer;

	private Subscriber sub;
	private DigitalInkModel model;
	private DoObject source;


	// GETTERS & SETTERS

	/**
	 * Get the current observer that is notified of any source events.
	 * 
	 * @return 
	 */
	public IDigitalInkSourceObserver getObserver() {
		return observer;
	}

	/**
	 * Set the observer that will be notified of any source events, such as
	 * trace start, trace end and added samples.
	 * 
	 * @param observer 
	 */
	public void setObserver(IDigitalInkSourceObserver observer) {
		this.observer = observer;
	}
	
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
		if (this.sub != null) this.disconnect();
		this.sub = ServiceManager.getInstance().getSession().subscribe(zone, channel, this);
	}

	/**
	 * Disconnect the source connector from its channel.
	 */
	public void disconnect() {
		if (this.sub != null) this.sub.unsubscribe();
		this.sub = null;
	}

	/**
	 * Notify the current observer of a trace start.
	 * 
	 * @param trace 
	 * @param pen
	 */
	protected void notifyTraceStarted(String pen, Trace trace) {
		if (this.observer != null) this.observer.traceStarted(pen, trace);
	}

	/**
	 * Notify the current observer of a trace end.
	 * 
	 * @param trace 
	 * @param pen
	 */
	protected void notifyTraceEnded(String pen, Trace trace) {
		if (this.observer != null) this.observer.traceEnded(pen, trace);
	}

	/**
	 * Notify the current observer of an added sample.
	 * 
	 * @param sample
	 * @param trace 
	 * @param pen
	 */
	protected void notifySampleAdded(String pen, Sample sample, Trace trace) {
		if (this.observer != null) this.observer.sampleAdded(pen, sample, trace);
	}

	// ABSTRACT METHODS

	public abstract String sourceChannel();


	// INNER TYPES

	/**
	 * Interface that has to be implemented by any observer that is interested
	 * in source events, such as starting traces and adding samples to a trace.
	 * 
	 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
	 * @version 0.3.0
	 */
	public static interface IDigitalInkSourceObserver {

		public void traceStarted(String pen, Trace trace);

		public void traceEnded(String pen, Trace trace);

		public void sampleAdded(String pen, Sample sample, Trace trace);
		
	}
}
