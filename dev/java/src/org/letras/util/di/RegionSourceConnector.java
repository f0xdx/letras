/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import java.util.HashMap;
import org.letras.psi.iregion.DoIRegion;
import org.letras.psi.iregion.RegionEvent;
import org.letras.psi.iregion.RegionSample;
import org.mundo.rt.DoObject;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;

/**
 * Connector for the processing stage interface <i>iregion</i>. This will interface
 * against {@link DoIRegion} services and process streamed digital ink accordingly.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public class RegionSourceConnector extends DigitalInkSourceConnector {

	// DEFAULTS

	private static final int INITIAL_CAPACITY = 8;

	// MEMBERS

	private DoIRegion source;
	private HashMap<String, PenProcessor> penProcessors;


	// CONSTRUCTORS

	public RegionSourceConnector(DoObject source, DigitalInkModel model) {
		super(source, model);
		this.source = new DoIRegion(source);
		this.penProcessors = new HashMap<String, PenProcessor>(INITIAL_CAPACITY);
	}

	public RegionSourceConnector(DoObject source) {
		super(source);
		this.source = new DoIRegion(source);
		this.penProcessors = new HashMap<String, PenProcessor>(INITIAL_CAPACITY);
	}
	

	// METHODS

	/**
	 * Private helper method to obtain the correct pen processor given a particular
	 * pen id.
	 * 
	 * @param pen
	 * @return 
	 */
	private PenProcessor getPenProcessor(String pen) {
		PenProcessor pp = this.penProcessors.get(pen);
		if (pp == null) {
			pp = new PenProcessor(pen, this.getModel());
			this.penProcessors.put(pen, pp);
		}
		return pp;
	}
	
	// INTERFACE METHODS

	@Override
	public String sourceChannel() {
		return this.source.channel();
	}

	@Override
	public void received(Message msg, MessageContext mc) {
		Object obj = msg.getObject();
		if (obj instanceof RegionEvent) {
			RegionEvent evt = (RegionEvent) obj;
			(this.getPenProcessor(evt.getPenID())).process(evt);
		}
		else {
			RegionSample smp =  (RegionSample) obj;
			(this.getPenProcessor(smp.getPenID())).process(smp);
		}
	}
	
	// INNER TYPES

	/**
	 * Inner class to handle processing of data for a particular pen
	 */
	public class PenProcessor {

		// MEMBERS

		private String penId;
		private Trace currentTrace;
		private DigitalInkStructure di;


		// GETTERS & SETTERS

		public String getPenId() {
			return penId;
		}

		public void setPenId(String penId) {
			this.penId = penId;
		}


		// CONSTRUCTORS

		/**
		 * Constructor for the specific pen processor.
		 * 
		 * @param penId the id of the pen to process
		 * @param model the model of digital ink data, where the structure of
		 * the handled pen should be stored
		 */
		public PenProcessor(String penId, DigitalInkModel model) {
			this.penId = penId;
			if (model != null) {
				if (!model.hasPenData(this.penId))
					model.storePenData(this.penId, new DigitalInkStructure());
				this.di = model.getPenData(this.penId);
			}
		}

		
		// METHODS
		
		public void process(RegionEvent evt) {
			if (evt.traceStart()) {
				this.currentTrace = new Trace(evt.getGuid().toString());
				notifyTraceStarted(this.penId, this.currentTrace);
			}
			else if (evt.traceEnd()) {
				if (this.di != null) this.di.add(this.currentTrace);
				notifyTraceEnded(this.penId, this.currentTrace);
			}
		}

		public void process(RegionSample smp) {
			if (this.currentTrace == null) return;
			Sample s = SampleFactory.createSample(smp);
			this.currentTrace.add(s);
			notifySampleAdded(this.penId, s, this.currentTrace);
		}
	}
}
