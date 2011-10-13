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

/**
 * This interface defines the methods a region observer needs to implement
 * in order to follow the states of a region. Methods relate to the strokes
 * inside a region and the contained sub-region(s).
 * <p>
 * NOTE that this interface is highly likely to change adding and removing
 * of sub-regions should also be followed by the region observers, so the next
 * version will include methods for doing so
 * 
 * @author felix_h
 *
 */
public interface IInkRegionObserver {

	/**
	 * Called whenever a stroke has been added to a region.
	 * 
	 * @param region		the <code>Region</code> a <code>Stroke</code> 
	 * 						has been added to
	 * @param stroke		the <code>Stroke</code> that has been added
	 */
	public void strokeAdded(InkRegion region, Stroke stroke);
	
	/**
	 * Called whenever a stroke has been removed from a region.
	 * 
	 * @param region		the <code>Region</code> a <code>Stroke</code> 
	 * 						has been removed from
	 * @param stroke		the <code>Stroke</code> that has been removed
	 */
	public void strokeRemoved(InkRegion region, Stroke stroke);
}
