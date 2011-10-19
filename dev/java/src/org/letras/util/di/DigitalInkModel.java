/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import java.util.HashMap;

/**
 * Central model of digital ink data. This model holds a map containing all
 * known pens and their {@link DigitalInkStructure DigtialInkStructures}. In
 * general nothing speaks against having several digital ink models, therefore
 * this class is not designed as a singelton. However, your application typically
 * will have only one central digital ink data model, i.e. an instance of this
 * class. In order to fill the model, use the {@link DigitalInkProcessor} and
 * obtain an adequate {@link DigitalInkSourceConnector} for the processing stage 
 * interface you want to collect digital ink at.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public class DigitalInkModel {
	
	// DEFAULTS
	
	private static final int MAP_CAPACITY = 8;

	// MEMBERS

	private HashMap<String, DigitalInkStructure> penData;
	private IDigitalInkModelCallback callback;

	// CONSTRUCTORS

	public DigitalInkModel() {
		this.penData = new HashMap<String, DigitalInkStructure>(MAP_CAPACITY);
	}

	// METHODS

	/**
	 * Sets a call back to be notified when the model changes (at the top level).
	 * 
	 * @param callback 
	 */
	public void setCallback(IDigitalInkModelCallback callback) {
		this.callback = callback;
	}

	/**
	 * Check whether the model has data for a particular pen available.
	 * 
	 * @param pen
	 * @return 
	 */
	public boolean hasPenData(String pen) {
		return penData.containsKey(pen);
	}

	/**
	 * Store the data for the provided pen. The old data will be replaced if the
	 * pen is already stored in this map.
	 * 
	 * @param pen
	 * @param data 
	 */
	public void storePenData(String pen, DigitalInkStructure data) {
		penData.put(pen, data);
		if (this.callback != null) this.callback.penDataStored(pen, data);
	}

	/**
	 * Get the data stored for a particular pen. If there is no data stored for
	 * this pen, the return value will be <code>null</code>.
	 * 
	 * @param pen
	 * @return 
	 */
	public DigitalInkStructure getPenData(String pen) {
		return this.penData.get(pen);
	}

	/**
	 * Remove the data of a particular pen.
	 * 
	 * @param pen
	 * @return <code>true</code> iff data for this pen was removed (i.e. if such
	 * data was stored), <code>false</code> otherwise
	 */
	public boolean removePenData(String pen) {
		if (this.penData.containsKey(pen)) {
			this.penData.remove(pen);
			if (this.callback != null) this.callback.penDataRemoved(pen);
			return true;
		}
		else return false;
	}

	// INNER TYPES

	/**
	 * Inner type for callbacks. 
	 */
	public static interface IDigitalInkModelCallback {
		/**
		 * Called when data is stored for a particular pen.
		 * 
		 * @param pen
		 * @param data 
		 */
		public void penDataStored(String pen, DigitalInkStructure data);

		/**
		 * Called when data is removed for a particular pen.
		 * 
		 * @param pen 
		 */
		public void penDataRemoved(String pen);
	}
}
