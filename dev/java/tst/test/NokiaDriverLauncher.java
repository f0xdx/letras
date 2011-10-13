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
package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.rawdata.IPenAdapter;
import org.letras.ps.rawdata.IPenAdapterFactory;
import org.letras.ps.rawdata.IPenDriver;
import org.letras.ps.rawdata.driver.nokia.NokiaPenDriver;
import org.letras.psi.ipen.PenSample;
import org.mundo.rt.TypedMap;

/**
 * A Launcher to test the nokia driver.
 * <p>
 * The Events received by the driver will be printed to stdout.
 * @author niklas
 */
public class NokiaDriverLauncher {
	
	//create a mockup factory
	
	IPenAdapterFactory factory = new IPenAdapterFactory() {
		@Override
		public IPenAdapter create(IPenDriver driver, String token) {
			
			//final so that it can be used in the nested class
			final String penId = token;
			
			//create a mockup PenAdapter with report functionalities
			IPenAdapter adapter = new IPenAdapter() {
				
				@Override
				public void publishSample(PenSample sample) {
					System.out.println(String.format("%s - Sample: x = %f2 y = %f force = %d",penId, sample.getX(), sample.getY(), sample.getForce()));
					
				}
				
				@Override
				public void penState(int state) {
					System.out.println(String.format("%s - State set to: %d", penId, state));
					
				}
				
				@Override
				public String penId() {
					return penId;
				}
			};
		
			return adapter;
		}
	};
	
	
	/**
	 * Setup and run the driver
	 */
	public void run() {
		
		//setup the driver
		
		IPenDriver driver = new NokiaPenDriver();
		driver.inject(factory);
		driver.init();
		
		//wait for any input
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//shutdown the driver
		driver.shutdown();
	}
	
	
	/**
	 * main function to start the driver
	 * @param args should not be used
	 */
	public static void main(String[] args) {
		Logger.getLogger("org.letras.ps.rawdata.nokia").setLevel(Level.ALL);
		NokiaDriverLauncher instance = new NokiaDriverLauncher();
		instance.run();
		
	}

}
