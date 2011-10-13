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
package org.letras.tools.regionmonitor.ink.model;

import org.letras.psi.iregion.RegionSample;

/**
 * This interface defines the methods a stroke observer needs to
 * provide in order to follow a strokes states. When a stroke gets
 * created it is initially set into the DRAWING state. Then the samples
 * of the stroke are added successively. Finally the stroke is set into
 * DRAWN, which means that no samples will follow except if reset into
 * DRAWING mode.
 * 
 * @author felix_h
 * @version 0.0.1
 */
public interface IStrokeObserver {

	/**
	 * This method will be called on each registered stroke observer, whenever
	 * the mode of this stroke changes.
	 * 
	 * @param stroke		a reference to the stroke that changed
	 * @param oldMode		the former mode this stroke was set to
	 * @param newMode		the new mode this stroke is set to
	 */
	public void strokeModeChanged(Stroke stroke, int oldMode, int newMode);
	
	/**
	 * This method will be called each time a <code>Sample</code> is added to 
	 * a stroke. Calls to this method will only be happen if the stroke is currently
	 * in DRAWING mode. If it is not, the implementing class might assume, that
	 * a call to <code>strokeModeChanged()</code> precedes the call <code>sampleAdded()</code>.
	 * 
	 * @param stroke		a reference to the stroke that changed
	 * @param sample		the newly added sample
	 */
	public void sampleAdded(Stroke stroke, RegionSample sample);
}
