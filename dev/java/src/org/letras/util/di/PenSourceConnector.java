/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.letras.psi.ipen.DoIPen;
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

	// CONSTRUCTORS

	public PenSourceConnector(DoObject source, DigitalInkModel model) {
		super(source, model);
		this.source = new DoIPen(source);
	}

	// INTERFACE METHODS

	@Override
	public String sourceChannel() {
		// TODO implement this
		return null;
	}

	@Override
	public void received(Message msg, MessageContext mc) {
		// TODO implement this
	}
	
}
