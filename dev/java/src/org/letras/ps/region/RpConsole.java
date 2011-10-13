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
package org.letras.ps.region;

import java.io.Console;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a simple console UI to the raw data processor controlling
 * a Mundo node providing the raw data processing stage in the pipeline.
 * As of version 0.0.1, the only supported operation is a node shutdown.
 * 
 * @author felix_h
 * @version 0.0.1
 * 
 */
public class RpConsole implements Runnable {

	// logger

	private static final Logger logger = 
		Logger.getLogger("org.letras.ps.region");
	
	// defaults
	private static final String RP_NAME = "region processor";
	private static final String CONSOLE_PROMPT = "[%s] ";
	
	private static final char CMD_EPSILON = '0';
	private static final char CMD_QUIT = 'q';
	private static final char CMD_HELP = 'h';
	
	// members

	private Console commandLine;
	
	private boolean available;
	
	public boolean isAvailable() {
		return available;
	}

	// constructors
	
	/**
	 * Simple no-argument constructor obtain
	 */
	public RpConsole() {
		
		// check whether the system console is available
		try {
			System.class.getMethod("console", new Class[0]);
			this.available = ((this.commandLine = System.console()) != null);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		
		// report if it is not
		if (this.isAvailable()) logger.logp(Level.FINE, "RpConsole", "RpConsole", "console available");
		else logger.logp(Level.FINE, "RpConsole", "RpConsole", "console not available");
	}
	
	// methods

	/**
	 * Simple helper method to read a command char.
	 * 
	 * @return either the empty command (epsilon) or the first character of the user input
	 */
	private char readCommand() {
		String input = this.commandLine.readLine(CONSOLE_PROMPT, RP_NAME);
		return ((input == null)||(input.equals(""))) ?
				CMD_EPSILON : input.charAt(0);
	}
	
	/**
	 * Small helper method to print out the help message on the command line.
	 */
	private void helpMessage() {
		if (!this.isAvailable()) {
			logger.logp(Level.FINE, "RpConsole", "printHelpMessage", "console unavailable");
			return;
		}
		// format a help message
		this.commandLine.format("\n   Key     Usage\n   ---\n");
		this.commandLine.format("   %c     Exit\n", CMD_QUIT );
		this.commandLine.format("   %c     Prints this help message\n", CMD_HELP);
		this.commandLine.format("   ---\n\n");
	}
	
	/**
	 * Helper method to quit the raw data processing stage.
	 */
	private void quit() {
		// stop the region processing stage
		RegionProcessor.getInstance().stop();
	}
	
	/**
	 * Main method used to run a console.
	 */
	@Override
	public void run() {
		boolean running = true;
		char command = CMD_EPSILON;
		
		while (running) {
			command = this.readCommand();
			
			switch (command) {
			case CMD_QUIT:
					this.quit();
					running = false;
					break;
			case CMD_HELP:
					this.helpMessage();
					break;
			default:
					break;
			}
			
			// let other threads execute
			Thread.yield();
		}
	}
}
