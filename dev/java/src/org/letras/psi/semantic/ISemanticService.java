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
 * An <code>ISemanticService</code> provides some sort of semantic-discovering service for
 * {@link IRegion}s. Handwriting recognition and gesture recognition are examples of
 * such services. This interface allows configuring the semantic service to use a specific
 * {@link ISegmentationProvider} and to specify which regions the semantic service
 * should be provided for.
 * 
 * @author Jannik Jochem
 *
 */
public interface ISemanticService {
	/**
	 * Sets the segmenter the service should use for identifying which traces to work on.
	 * If regions are already being observed, these are registered with the segmenter as well.
	 * @param segmenter 
	 */
	public void setSegmenter(ISegmentationProvider segmenter);
	
	/**
	 * Start observing region. If a segmenter is already set, the region
	 * will be registered with the segmenter as observed by this service.
	 * @param region
	 */
	public void addRegion(IRegion region);
	
	/**
	 * Stop observing region. If a segmenter is already set, the segmenter
	 * will be notified that this service no longer observes the region.
	 * @param region
	 */
	public void removeRegion(IRegion region);
}
