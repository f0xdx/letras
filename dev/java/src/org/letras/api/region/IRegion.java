package org.letras.api.region;

public interface IRegion {

	/**
	 * get the region data
	 * 
	 * @return RegionData
	 */
	RegionData getRegionData();

	/**
	 * register a listener for region samples and events
	 * 
	 * @param listener
	 */
	void registerRegionListener(RegionListener listener);

	/**
	 * unregister a previously registered listener for region samples and events
	 * 
	 * @param listener
	 */
	void unregisterRegionListener(RegionListener listener);

	/**
	 * RegionListener
	 * 
	 * @author niklas
	 */
	public interface RegionListener {
		void receiveRegionEvent(RegionEvent event);

		void receiveRegionSample(RegionSample sample);
	}

}
