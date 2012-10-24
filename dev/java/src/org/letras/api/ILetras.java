package org.letras.api;

import org.letras.api.pen.IPenDiscovery;
import org.letras.api.region.IRegionDiscovery;
import org.letras.api.region.IRegionHost;

public interface ILetras {

	/*--------------------------------------- Pen related API -------------------------------------------*/
	/**
	 * register an instance of {@link IPenDiscovery} which will then be called when a new pen connects
	 * to Letras
	 * 
	 * @param penDiscovery the callback for when new pens connect
	 */
	public void registerPenDiscovery(IPenDiscovery penDiscovery);

	public void unregisterPenDiscovery(IPenDiscovery penDiscovery);

	/*--------------------------------------- Region related API -------------------------------------------*/
	/**
	 * register an instance of {@link IRegionDiscovery} which will then be called when a new region gets registered
	 * in Letras
	 * 
	 * @param regionDiscovery the callback for when new regions are registered
	 */
	public void registerRegionDiscovery(IRegionDiscovery regionDiscovery);

	/**
	 * get an instance of IRegionHost to register Regions in Letras
	 * 
	 * @return an instance of IRegionHost
	 */
	public IRegionHost createRegionHost();


}
