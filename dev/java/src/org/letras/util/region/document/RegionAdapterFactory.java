/*******************************************************************************
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is MundoCore Java.
 * 
 * The Initial Developer of the Original Code is Telecooperation Group,
 * Department of Computer Science, Technische Universität Darmstadt.
 * Portions created by the Initial Developer are
 * Copyright © 2009-2011 the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 * Felix Heinrichs
 * Niklas Lochschmidt
 * Jannik Jochem
 ******************************************************************************/
package org.letras.util.region.document;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.letras.psi.iregion.IRegion;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;

public class RegionAdapterFactory extends Service {
	private static RegionAdapterFactory instance;
	
	/**
	 * This is the cache for region adapters. Because this factory is a singleton and thus
	 * part of the root set, we need to make sure that {@link RegionAdapter}s that are no
	 * longer in use can be gc'ed. Because of this, the adapters are only referenced weakly
	 * here.
	 */
	private Map<String, WeakReference<RegionAdapter>> urisToAdaptersMap = new HashMap<String, WeakReference<RegionAdapter>>();
	
	private boolean initialized = false;
	
	private RegionAdapterFactory() {
	}
	
	public static RegionAdapterFactory getInstance() {
		if (instance == null) {
			instance = new RegionAdapterFactory();
		}
		return instance;
	}
	
	@Override
	public void init() {
		super.init();
		initialized = true;
	}
	
	public RegionAdapter adapt(IRegion region) {
		if (!initialized) {
			Mundo.registerService(this);
		}
		String uri = region.uri();
		RegionAdapter adapter = null;
		WeakReference<RegionAdapter> adapterReference = urisToAdaptersMap.get(uri);
		if (adapterReference != null)
			adapter = adapterReference.get();
		if (adapter == null) {
			adapter = new RegionAdapter(region, this);
			urisToAdaptersMap.put(uri, new WeakReference<RegionAdapter>(adapter));
		} else if (!region.equals(adapter.getRegion())) {
			adapter.setRegion(region);
		}
		return adapter;
	}
	
}
