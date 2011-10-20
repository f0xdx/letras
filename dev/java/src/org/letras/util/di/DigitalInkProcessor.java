/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import java.util.HashMap;
import org.letras.psi.ipen.DoIPen;
import org.letras.psi.ipen.IPen;
import org.letras.psi.ipen.PenSample;
import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.RegionSample;
import org.mundo.rt.DoObject;
import org.mundo.rt.IReceiver;

/**
 * Base class for digital ink processing. The {@link DigitalInkProcessor} will
 * provide appropriate {@link DigitalInkSourceConnector source connectors} for
 * a given digtial ink source. In order to obtain digital ink, simply call the
 * {@link DigitalInkProcessor# } method and provide your {@link DigitalInkModel}.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public abstract class DigitalInkProcessor implements IReceiver {

	// DEFAULTS

	private static final int INITIAL_CAPACITY = 4;

	// MEMBERS

	private DigitalInkModel model;
	private HashMap<DoObject,DigitalInkSourceConnector> sourceConnectors;


	// GETTERS & SETTERS

	public HashMap<DoObject, DigitalInkSourceConnector> getSourceConnectors() {
		return sourceConnectors;
	}

	public void setSourceConnectors(HashMap<DoObject, DigitalInkSourceConnector> sourceConnectors) {
		this.sourceConnectors = sourceConnectors;
	}

	public DigitalInkModel getModel() {
		return model;
	}

	public void setModel(DigitalInkModel model) {
		this.model = model;
	}
	

	// CONSTRUCTORS

	public DigitalInkProcessor(DigitalInkModel model) {
		this.model = model;
		this.sourceConnectors = 
				new HashMap<DoObject, DigitalInkSourceConnector>(INITIAL_CAPACITY);
	}


	// METHODS

	/**
	 * Connect this processor to a source of digital ink. The source has to be
	 * a {@link DoObject} referring to some component in a processing stage
	 * interface that is capable of providing digital ink, e.g., {@link DoIPen} or
	 * {@link DoIRegion}. Such distributed objects are typically returned by
	 * the Mundo service discovery, but can also be instantiated locally by
	 * calling something like
	 * <p>
	 * <code>
	 * DoIRegion._of(mySession, myRegion);
	 * </code>
	 * <p>
	 * 
	 * @param source the source of digital ink to process
	 * @return <code>true</code> iff it was possible to connect to this source
	 * @throws UnsupportedOperationException thrown in the case that the provided
	 * source could not be handled
	 */
	public boolean connect(DoObject source) throws UnsupportedOperationException {

		// check whether source is already contained
		if (this.sourceConnectors.containsKey(source)) return false;

		// determine the right type of connector
		DigitalInkSourceConnector connector;
		if (IPen.class.getName().equals(source._getInterfaceName()))
			connector = new PenSourceConnector(source, this.model);
		else if (IRegion.class.getName().equals(source._getInterfaceName()))
			connector = new RegionSourceConnector(source, this.model);
		else
			throw new UnsupportedOperationException(
					String.format("unsupported source type: %s", 
					source._getInterfaceName())
					);

		// connect
		connector.connect(connector.sourceChannel());

		// store & return
		this.sourceConnectors.put(source, connector);
		return true;
	}

	/**
	 * Disconnect from a previously connected source.
	 * 
	 * @param source the source from which to disconnect
	 * @return <code>true</code> iff this source coule be disconnected from
	 */
	public boolean disconnect(DoObject source) {
		if (this.sourceConnectors.containsKey(source)) {
			this.sourceConnectors.get(source).disconnect();
			this.sourceConnectors.remove(source);
			return true;
		}
		else return false;
	}
}
