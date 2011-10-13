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
package org.letras.psi.iregion;



/**
 * Processing delegate for {@link IRegion}. Classes implementing this
 * interface can receive {@link RegionSample}s and {@link RegionEvent}s from
 * {@link IRegion}.
 * 
 * @author niklas, jannik
 *
 */
public interface IDigitalInkConsumer {
	/**
	 * This is called when a {@link RegionSample} is received.
	 * @param source the {@link IRegion} that received the {@link RegionSample}
	 * @param regionSample the {@link RegionSample} that was received
	 */
	public void consume(IRegion source, RegionSample regionSample);
	
	/**
	 * This is called when a {@link RegionEvent} is received.
	 * @param source the {@link IRegion} that received the {@link RegionEvent}
	 * @param regionEvent the {@link RegionEvent} that was received
	 */
	public void consume(IRegion source, RegionEvent regionEvent);
}
