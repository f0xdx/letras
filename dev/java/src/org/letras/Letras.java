package org.letras;

import org.letras.api.ILetras;
import org.letras.api.pen.IPenDiscovery;
import org.letras.api.region.IRegionDiscovery;
import org.letras.api.region.IRegionHost;
import org.letras.psi.ipen.impl.MundoPenDiscovery;

public class Letras implements ILetras {

	private final MundoPenDiscovery penDiscoveryService;

	private static class SingletonHolder {
		static final Letras instance = new Letras();
	}

	public static ILetras getInstance() {
		return SingletonHolder.instance;
	}

	private Letras() {
		penDiscoveryService = new MundoPenDiscovery();
		penDiscoveryService.start();
	}

	@Override
	public void registerPenDiscovery(IPenDiscovery penDiscovery) {
		penDiscoveryService.registerPenDiscoveryListener(penDiscovery);
	}

	@Override
	public void unregisterPenDiscovery(IPenDiscovery penDiscovery) {
		penDiscoveryService.unregisterPenDiscoveryListener(penDiscovery);
	}

	@Override
	public void registerRegionDiscovery(IRegionDiscovery regionDiscovery) {
		// TODO Auto-generated method stub
	}

	@Override
	public IRegionHost createRegionHost() {
		// TODO Auto-generated method stub
		return null;
	}
}
