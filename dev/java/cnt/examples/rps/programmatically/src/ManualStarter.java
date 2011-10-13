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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.letras.ps.region.RegionProcessor;
import org.letras.ps.region.broker.simple.SimpleRegionBroker;
import org.mundo.rt.Mundo;

/**
 * This class is to demonstrate how to start a RawDataProcessor manually
 * from within an existing class.
 * @author niklas  
 */
public class ManualStarter {
	public static void main(String[] args) {
	    //Start Mundo
		Mundo.init();
		
		//Create and configure the RawDataProcessor
		RegionProcessor rps;
		try {
			RegionProcessor.setLogLevel(Level.INFO);
			rps = new RegionProcessor();
			//Set the region discovery zone
			rps.setServiceZone("lan");
			//Create and set a RegionBroker
			rps.setRegionBroker(new SimpleRegionBroker());
			//Register the processor
			Mundo.registerService(rps);
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		
		//Keep the thread open until the user presses a key 
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Shutdown Mundo
		Mundo.shutdown();
	}
}
