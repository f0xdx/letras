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


/**
 * An <code>ISegmenter</code> is a service that segments pen input based on configurable heuristics.
 * The segments can then be further processed into other components.
 * 
 * This is the client-side interface for the segmenter, which only provides facilities for
 * adjusting the heuristics model and for retrieving the interface {@link ISegmentationProvider}
 * that is exposed to the actual {@link ISemanticService} that process the segmented data.
 *  
 * @author Jannik Jochem
 *
 */
public interface ISegmenter {
//	public void setModel(Object model);
	/**
	 * @return the interface for semantic services.
	 */
	public ISegmentationProvider getSegmentationProvider();
}
