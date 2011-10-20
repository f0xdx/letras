/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.letras.psi.iregion.DoIRegion;
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

	// MEMBERS

	private DoIRegion source;


	// CONSTRUCTORS

	public RegionSourceConnector(DoObject source, DigitalInkModel model) {
		super(source, model);
		this.source = new DoIRegion(source);
	}

	
	// INTERFACE METHODS

	@Override
	public String sourceChannel() {
		// TODO implement
		return null;
	}

	@Override
	public void received(Message msg, MessageContext mc) {
		// TODO implement
	}
	
}
