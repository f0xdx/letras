/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.letras.psi.ipen.PenSample;
import org.letras.psi.iregion.RegionSample;
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

	public static final String DEFAULT_ZONE = "lan";
	public static final String PSI_IPEN = PenSample.class.getPackage().getName();
	public static final String PSI_IREGION = RegionSample.class.getPackage().getName();

	// MEMBERS

	private DigitalInkModel model;

	public DigitalInkModel getModel() {
		return model;
	}

	public void setModel(DigitalInkModel model) {
		this.model = model;
	}

	// CONSTRUCTORS

	public DigitalInkProcessor(DigitalInkModel model) {
		this.model = model;
	}

	// METHODS

	/**
	 * Called to connect this processor to a source where digtial ink is 
	 * published. This requires specifying the used processing stage interface,
	 * e.g. <i>ipen</i> or <i>iregion</i>. The provided value must match the
	 * fully qualified package name of one of the processing stage interfaces.
	 * For convenience you can use {@link DigitalInkProcessor#PSI_IPEN} or
	 * {@link DigitalInkProcessor#PSI_IREGION}.
	 * 
	 * @param psi the processing stage interface this source belongs to
	 * @param source the source channnel of the digital ink
	 */
	public void connect(String psi, String source) {
		this.connect(psi, DEFAULT_ZONE, source);
	}

	/**
	 * Called to connect this processor to a source where digital ink is published
	 * in a given zone. This requires specifying the used processing stage interface,
	 * e.g. <i>ipen</i> or <i>iregion</i>. The provided value must match the
	 * fully qualified package name of one of the processing stage interfaces.
	 * For convenience you can use {@link DigitalInkProcessor#PSI_IPEN} or
	 * {@link DigitalInkProcessor#PSI_IREGION}.
	 *
	 * @param psi
	 * @param zone
	 * @param source 
	 */
	public void connect(String psi, String zone, String source) {
		// TODO delegate to the appropriate DigitalInkSourceConnector
	}
}
