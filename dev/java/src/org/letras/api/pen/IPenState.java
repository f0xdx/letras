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
package org.letras.api.pen;

/**
 * This interface specifies constants used as flags to determine a pen's state.
 *
 * @author felix_h
 * @version 0.3
 */
public interface IPenState {
	
	/**
	 * Used to set the pen into state: OFF.
	 */
	public static final int OFF = 0;
	
	/**
	 * Used to set the pen into state: ON.
	 */
	public static final int ON = 1;
	
	// ON = UP, that means: a pen which is in state UP, needs
	// to be ON, but nothing else (especially not DOWN)
	
	/**
	 * Used to set the pen into state: UP. Note that this
	 * state implies that the pen is also ON.
	 */
	public static final int UP = 1;
	
	/**
	 * Used to set the pen into state: DOWN. Note that this
	 * state implies that the pen is also ON.
	 */
	public static final int DOWN = 3;
	
	/**
	 * Used to set the pen into state: EXCEPTION. Note that this
	 * state alone is used to define that the pen is probably in a state
	 * where auto-recovery is impossible. A user interface should
	 * inform the user when the pen is entering this state to allow
	 * manual recovery. When additional states are active such as 
	 * OUT_OF_REACH or DOWN this indicates that it is a pen-specific
	 * error and could be temporary.
	 */
	public static final int EXCEPTION = 4;
	
	/**
	 * Used to set the pen into state OUT_OF_REACH. Note that this
	 * state implies that the pen is also ON. A pen in state
	 * OUT_OF_REACH can still be recovered when coming into reach
	 * again but some samples may be lost in between.
	 * <p>
	 * Depending on the pen model, the pen state while eventually
	 * change from state OUT_OF_REACH to state OFF or to state EXCEPTION.
	 */
	public static final int OUT_OF_REACH = 5;
	
	
}
