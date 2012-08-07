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
package org.letras.ps.rawdata.driver.anoto.adp301;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.letras.ps.rawdata.IPenAdapterFactory;
import org.letras.ps.rawdata.IPenDriver;

/**
 * The AnotoPenDriver represents the concrete native pen driver implementation to the PenDriverManager.
 * This class is responsible for selecting and loading the correct library for the platform. Because native
 * libraries can not be loaded directly from inside a jar-file, the library in question must first be
 * extracted to a temporary location to be loaded.
 * 
 * @author niklas
 *
 */
public class AnotoPenDriver implements IPenDriver {

	/**
	 * logger
	 */
	private final static Logger logger = Logger.getLogger("org.letras.ps.rawdata.driver.anoto.adp301");
	
	/**
	 * the factory from which to obtain new IPenAdapter instances
	 */
	private IPenAdapterFactory factory;
	
	/**
	 * a map of known PenAdapterNativeAdapters
	 */
	private HashMap<String, PenAdapterNativeAdapter> tokenToNativeAdapterMap = new HashMap<String, PenAdapterNativeAdapter>();
	
	/**
	 * this variable indicates whether the jni-library has been loaded
	 */
	private boolean nativeExtensionLoaded = false;
	
	@Override
	public void init() {
		//try to load the native extension
		if (loadNativeExtension()) {
			nativeExtensionLoaded = true;
			//the call to runNativeDriver() must occur from a different thread because the call is blocking
			new Thread(new Runnable() {
				@Override
				public void run() {
					runNativeDriver();
				}
			}).start();
		}
		else {
			logger.logp(Level.WARNING, "AnotoPenDriver", "init", "Could not initialize native driver. Platform not yet supported.");
		}
	}

	/**
	 * look at the file "AnotoPenDriver.c" for the implementation of this method.
	 * The call to this method is blocking and will only return if shutdownNativeDriver() is called
	 */
	private native void runNativeDriver();
	
	/**
	 * look at the file "AnotoPenDriver.c" for the implementation of this method.
	 * Cancels discovery of new pens and shutdown the native pen driver.
	 */
	private native void shutdownNativeDriver();
	
	/**
	 * get the PenAdapterNativeAdapter for the specified token.
	 * @param token the unique id of the pen
	 * @return the corresponding PenAdapterNativeAdapter
	 */
	private PenAdapterNativeAdapter createNewNativePenAdapter(String token) {
		//check if adapter is in the map
		PenAdapterNativeAdapter adapter = tokenToNativeAdapterMap.get(token);
		if(adapter == null) {
			adapter = new PenAdapterNativeAdapter(factory.create(this, token));
			tokenToNativeAdapterMap.put(token, adapter);
		}
		return adapter;
	}
	
	@Override
	public void setPenAdapterFactory(IPenAdapterFactory factory) {
		this.factory = factory;
		
	}

	@Override
	public void shutdown() {
		if (nativeExtensionLoaded) {
			this.shutdownNativeDriver();
		}
		for (PenAdapterNativeAdapter penAdapter : tokenToNativeAdapterMap.values()) {
			penAdapter.penDisconnected();
		}
	}

	
	/**
	 * detects the operating system and loads the corresponding native extension.
	 * @return  true: if native extension is loaded
	 * 			false: if an error occured
	 */
	private boolean loadNativeExtension() {
			
		final String osName = System.getProperty("os.name").toLowerCase();
		final String osArch = System.getProperty("os.arch").toLowerCase();
		
		String libName = null;
		
		logger.logp(Level.FINE,"AnotoPenDriver", "loadNativeExtension", "os.name = " + osName + "; os.arch = " + osArch);
		if (osName.contains("mac os x")) {
			libName = "libadp301.jnilib";
		} else if (osName.contains("linux")) {
			if (osArch.equals("x86") || osArch.equals("i386") || osArch.equals("i686")) {
				libName = "libadp301.so";
			} else if (osArch.startsWith("x86_64") || osArch.startsWith("amd64")) {
				libName = "libadp301_64.so";
			}
		} else if (osName.contains("windows")) {
			if (osArch.equals("x86")) {
				libName = "adp301.dll";
			}
		}
		
		if (libName == null) {
			return false;
		} else {
			File lib = copyLibraryToTempFile(libName);
			if (lib != null) {
				try {
					System.load(lib.getAbsolutePath());
					return true;
				} catch (UnsatisfiedLinkError ule) {
					logger.logp(Level.SEVERE, "AnotoPenDriver", "loadNativeExtension", String.format("Could not load library: %s",ule.getLocalizedMessage()));
				}
			}
			return false;
		}
	}
	
	/**
	 * copies the native library from the drivers jar-file to a temporary location and returns the <code>File</code>
	 * instance or null if copying the library failed.
	 * @param libName the name of the library to load (e.g. libadp301.jnilib)
	 * @return the <code>File</code> instance of the copied library or null
	 */
	File copyLibraryToTempFile(String libName) {
		//get the inputstream from the classloader
		InputStream libInStream = this.getClass().getClassLoader().getResourceAsStream(libName);
		
		if (libInStream != null) {
			//seperate extension from filename for use as prefix and suffix when creating the tempfile
			int dotIndex = libName.lastIndexOf(".");
			
			File temp;
			FileOutputStream libOutStream;
			
			try {
				if (dotIndex == -1) {
					//seperation was not possible because there was no dot in the libraries name but it
					//should also work with a generic extension.
					temp = File.createTempFile(libName, "lib");
				}
				else {
					//seperation was possible
					temp = File.createTempFile(libName.substring(0, dotIndex), "." + libName.substring(dotIndex+1));
				}
				//create the stream to the destination file
				libOutStream = new FileOutputStream(temp);
			} catch (IOException e) {
				logger.logp(Level.SEVERE, "AnotoPenDriver", "copyLibraryToTempFile", String.format("Could not create tempfile: %s",e.getLocalizedMessage()));
				return null;
			}
			
			//copy the file
			byte[] buffer = new byte[10240];
			int len;
			try {
				while ((len = libInStream.read(buffer)) != -1) {
				    libOutStream.write(buffer, 0, len);
				}
				libOutStream.close();
			} catch (IOException e) {
				logger.logp(Level.SEVERE, "AnotoPenDriver", "copyLibraryToTempFile", String.format("Error copiing library to tempfile: %s",e.getLocalizedMessage()));
				return null;
			}
			
			return temp;
		}
		else {
			logger.logp(Level.WARNING, "AnotoPenDriver", "copyLibraryToTempFile", "Could not locate library for the current platform. AnotoPenDriver can not be used.");
			return null;
		}
	}
}
