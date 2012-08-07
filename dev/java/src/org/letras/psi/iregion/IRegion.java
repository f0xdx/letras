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

import org.letras.api.region.shape.IShape;
import org.mundo.annotation.mcMethod;
import org.mundo.annotation.mcRemote;

/**
 * This interfaces provides the functionality to retrieve an interactive region's
 * meta-data. Every interactive region needs to provide a service implementing
 * this interface, in order to allow the <code>RegionProcessingStage</code> to
 * obtain meta-data on the region used to detect pen samples on it.
 * 
 * @author felix_h
 * @version 0.0.1
 */
@mcRemote
public interface IRegion {
	
	/**
	 * @return the Uniform Resource Identifier that makes up the identity of this region
	 */
	public String uri();
	
	/**
	 * Used to obtain the shape of this region. For convenience the
	 * provided shapes in {@link org.letras.api.region.shape} should
	 * be used by implementing classes.
	 * 
	 * @return 	the {@link IShape} describing the interactive regions
	 * 			shape
	 */
	@mcMethod
	public IShape shape();
	
	/**
	 * Used to obtain the name of the channel this region will receive its
	 * samples on.
	 * 
	 * @return name of the channel this region receives its samples on
	 */
	@mcMethod
	public String channel();
	
	/**
	 * This method indicates whether this region is hungry. Being hungry
	 * means, that the region is also interested in the events and samples
	 * on any of its child regions.
	 * 
	 * @return	true if the regions is hungry, false otherwise
	 */
	@mcMethod
	public boolean hungry();
}
