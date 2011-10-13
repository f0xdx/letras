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
package org.letras.ps.region.broker.simple;

import org.letras.psi.iregion.IRegion;

/**
 * Listens for updates to the regions managed by a {@link RegionProvider}.
 * Main implementor is currently {@link SimpleRegionBroker}.
 * @author Jannik Jochem
 *
 */
public interface IRegionProviderListener {
	/**
	 * A region addedRegion was added to the model managed by source
	 * @param addedRegion
	 * @param source
	 */
	public void regionAdded(IRegion addedRegion, RegionProvider source);
	/**
	 * A region removedRegion was removed from the model managed by source
	 * @param removedRegion
	 * @param source
	 */
	public void regionRemoved(IRegion removedRegion, RegionProvider source);
}
