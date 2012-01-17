/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import java.util.concurrent.CopyOnWriteArrayList;
import org.letras.util.di.DigitalInkSourceConnector.IDigitalInkSourceObserver;

/**
 * Convenience class in clase if multiple {@link DigitalInkSourceObserver} objects
 * should be connected to a single {@link DigitalInkSourceConnector} instance.
 * The standard mechanism is to inform only a single observer for the sake of
 * efficiency. There are, however, cases where multiple observers are required,
 * e.g., if an ink source should be rendered and gestures should be recognized 
 * also. This class helps in these cases as it allows using several observers 
 * over the single observer interface in a fanout pattern. In order to use it,
 * set an instance of this class as the source observer to an ink source:
 * <code>
 * source.setObserver(new DiSourceFanout());
 * </code>
 * Now you can add all neede observers to this fanout, e.g., without keeping
 * an explicit reference to it, by using:
 * <code>
 * source.getObserver().register(myObserver);
 * source.getObserver().register(myOtherObserver);
 * </code>
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.1
 */
public class DiSourceFanout implements IDigitalInkSourceObserver {

	// MEMBERS

	private CopyOnWriteArrayList<IDigitalInkSourceObserver> observers;

	// GETTERS & SETTERS

	public CopyOnWriteArrayList<IDigitalInkSourceObserver> getObservers() {
		return observers;
	}

	public void setObservers(CopyOnWriteArrayList<IDigitalInkSourceObserver> observers) {
		this.observers = observers;
	}

	// CONSTRUCTORS

	public DiSourceFanout() {
		this.observers = new CopyOnWriteArrayList<IDigitalInkSourceObserver>();
	}

	// METHODS

	public boolean unregister(IDigitalInkSourceObserver o) {
		return observers.remove(o);
	}

	public boolean contains(Object o) {
		return observers.contains(o);
	}

	public boolean register(IDigitalInkSourceObserver o) {
		return observers.add(o);
	}

	// INTERFACE METHODS
	
	@Override
	public void traceStarted(String pen, Trace trace) {
		for (IDigitalInkSourceObserver o : this.observers)
			o.traceStarted(pen, trace);
	}

	@Override
	public void traceEnded(String pen, Trace trace) {
		for (IDigitalInkSourceObserver o : this.observers)
			o.traceEnded(pen, trace);
	}

	@Override
	public void sampleAdded(String pen, Sample sample, Trace trace) {
		for (IDigitalInkSourceObserver o : this.observers)
			o.sampleAdded(pen, sample, trace);
	}
}
