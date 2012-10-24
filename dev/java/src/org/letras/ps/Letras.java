package org.letras.ps;

import org.letras.api.ILetras;
import org.letras.api.pen.IPenDiscovery;
import org.letras.api.pen.impl.PenDiscovery;
import org.letras.api.region.IRegionHost;

public class Letras implements ILetras {

	private static Letras instance;
	private static Object creationLock = new Object();

	private Letras() {

	}

	public static ILetras getInstance() {
		if (instance == null) {
			synchronized (creationLock) {
				if (instance == null)
					instance = new Letras();
			}
		}
		return instance;
	}

	private PenDiscovery penDiscovery;

	@Override
	public void registerPenDiscovery(IPenDiscovery penDiscoveryListener) {
		if (penDiscovery == null) {
			synchronized (creationLock) {
				if (penDiscovery == null) {
					penDiscovery = new PenDiscovery();
					penDiscovery.start();
				}
			}
		}
		penDiscovery.registerPenDiscoveryListener(penDiscoveryListener);
	};

	@Override
	public void unregisterPenDiscovery(IPenDiscovery penDiscoveryListener) {
		penDiscovery.unregisterPenDiscoveryListener(penDiscoveryListener);
	}

	@Override
	public void registerRegionDiscovery(org.letras.api.region.IRegionDiscovery regionDiscovery) {

	};

	@Override
	public IRegionHost createRegionHost() {
		// TODO Auto-generated method stub
		return null;
	}

}
