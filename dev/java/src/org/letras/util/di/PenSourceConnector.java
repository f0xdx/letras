/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.letras.psi.ipen.DoIPen;
import org.letras.psi.ipen.IPenState;
import org.letras.psi.ipen.PenEvent;
import org.mundo.rt.DoObject;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;

/**
 * Connector for the processing stage interface <i>ipen</i>. This will interface
 * against {@link DoIPen} services and process streamed digital ink accordingly.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public class PenSourceConnector extends DigitalInkSourceConnector {

	// MEMBERS

	private DoIPen source;
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

	public PenSourceConnector(DoObject source, DigitalInkModel model) {
		super(source, model);
		this.source = new DoIPen(source);
		this.penId = this.source.penId();
		if (!this.getModel().hasPenData(this.penId))
			this.getModel().storePenData(this.penId, new DigitalInkStructure());
		this.di = this.getModel().getPenData(this.penId);
	}

	// INTERFACE METHODS

	@Override
	public String sourceChannel() {
		return this.source.channel();
	}

	/**
	 * Received method processing the data as obtained by the pen service. This
	 * implementation will regard any pen events apart from pen PEN_DOWN
	 * (PEN_UP, OFF, ERROR, OUT_OF_REACH) as ending a trace and add samples 
	 * to the current trace whenever there is a current trace. In praxis that
	 * means, that any samples on already started traces when this connector
	 * connects to the source will be dropped.
	 * 
	 * @param msg
	 * @param mc 
	 */
	@Override
	public void received(Message msg, MessageContext mc) {
		Object obj = msg.getObject();
		if (obj instanceof PenEvent) {
			PenEvent evt = (PenEvent) obj;
			if (evt.state() == IPenState.DOWN) {
				// Trace started
				this.currentTrace = new Trace();
				this.notifyTraceStarted(this.penId, this.currentTrace);
			}
			else if (evt.state() == IPenState.UP ){
				// Trace somehow ended
				di.add(this.currentTrace);
				this.notifyTraceEnded(this.penId, this.currentTrace);
			}
		}
		else {
			// check whether there is some trace at all
			if (this.currentTrace == null) return;
			Sample s = SampleFactory.createSample(obj);
			currentTrace.add(s);
			this.notifySampleAdded(this.penId, s, this.currentTrace);
		}
	}
}
