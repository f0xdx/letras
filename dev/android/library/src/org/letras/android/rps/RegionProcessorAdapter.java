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
package org.letras.android.rps;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.ConsoleHandler;

import org.letras.ps.region.RegionProcessor;
import org.mundo.rt.Mundo;
import org.mundo.rt.TypedMap;
import org.mundo.xml.XMLDeserializer;

/**
 * The RPService a wrapper around the RegionProcessor from letras-java
 * and has been implemented as an Android service to be able to run even if no
 * Activity is visible. 
 * 
 * <br>
 * Note: the RDPService extends android.app.Service and not the Mundo Service
 * 
 * @author niklas
 * 
 */
public class RegionProcessorAdapter {

	// members
	private RegionProcessor regionProcessor;

	private boolean registered;

	
	public RegionProcessorAdapter() {
		regionProcessor = RegionProcessor.getInstance();
		RegionProcessor.setLogHandler(new ConsoleHandler());			
		try {
			TypedMap config = (TypedMap) new XMLDeserializer().deserializeObject(new InputStreamReader(this.getClass().
					getClassLoader().getResourceAsStream("assets/rps.conf.xml"), "UTF-8"));
			regionProcessor.setServiceConfigMap(config);
		} catch (UnsupportedEncodingException e) {
		
		}
	}
	
	
	public void start() {
		Mundo.registerService(regionProcessor);
		registered = true;
	}
	
	public boolean isRunning() {
		return registered;
	}
	
	public void stop() {
		Mundo.unregisterService(regionProcessor);
		registered=false;
	}

	public boolean isActive() {
		return registered;
	}

}
