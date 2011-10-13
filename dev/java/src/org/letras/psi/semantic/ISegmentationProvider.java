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
package org.letras.psi.semantic;

import org.letras.psi.iregion.IRegion;

/**
 * <code>ISegmentationProvider</code> is the interface that is provided to {@link ISemanticService}s
 * by {@link ISegmenter}s. The {@link ISemanticService} registers itself and the {@link IRegion}s
 * it is working on through this interface.
 * 
 * @author Jannik Jochem
 *
 */
public interface ISegmentationProvider {
	/**
	 * Register an {@link ISemanticService} with this {@link ISegmentationProvider}.
	 * @param id the id of the service to register
	 */
	public void registerSemanticService(String id);
	
	/**
	 * Remove registration of an {@link ISemanticService} from this {@link ISegmentationProvider}.
	 * Remove all region registrations of this service as well.
	 * @param id the id of the service to unregister
	 */
	public void unregisterSemanticService(String id);
	
	/**
	 * Notifies the <code>ISegmentationProvider</code> that it should provide
	 * {@link Segmentation}s on region to the {@link ISemanticService}
	 * referenced by serviceId.
	 * 
	 * @param serviceId
	 * @param region
	 */
	public void registerRegion(String serviceId, IRegion region);
	
	/**
	 * Notifies the <code>ISegmentationProvider</code> that it should stop
	 * providing {@link Segmentation}s on region to the {@link ISemanticService}
	 * referenced by serviceId.
	 * 
	 * @param serviceId 
	 * @param region
	 */
	public void unregisterRegion(String serviceId, IRegion region);
}
