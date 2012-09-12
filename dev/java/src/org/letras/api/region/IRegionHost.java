package org.letras.api.region;

import org.letras.api.region.RegionData.IRegionListener;

/**
 * Interface to manage own regions in Letras. Regions are defined through a {@link RegionData} instance <br />
 * Registered {@link RegionData} instances are discoverable with Letras and consequently registered with the Letras
 * pipeline.
 */
public interface IRegionHost {

	/**
	 * register a new region
	 * 
	 * @param region
	 */
	void registerRegion(RegionData regionData);

	/**
	 * unregister the region identified by the given uri
	 * 
	 * @param uri
	 */
	void unRegisterRegion(String uri);

	/**
	 * 
	 */
	void registerForAnonymousRegionSamples(IRegionListener region);

}
