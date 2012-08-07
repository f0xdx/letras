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
package org.letras.tools.regionmonitor.ink.view;

import java.awt.Shape;

import org.letras.api.region.RegionSample;
import org.letras.tools.regionmonitor.ink.model.Stroke;

/**
 * This interface describes the interpolations used for drawing digital ink.
 * 
 * @author felix_h
 * @version 0.0.1
 */
public interface IInterpolation {

	/**
	 * This method is called to retrieve an interpolating shape for two samples
	 * s and t. Depending on the interpolation algorithm used, other samples forming
	 * the stroke context might be needed, so the contract foresees providing them.
	 * Note, that ff an implementing class desires to use the stroke context, it needs
	 * to check whether the stroke is already completed, or whether there still come
	 * more samples.
	 * <p>
	 * Since samples are provided in normalized region coordinates (nrc), the width
	 * and height information for the region where these should be drawn on needs to 
	 * be provided also.
	 * 
	 * @param stroke	the stroke context of the samples
	 * @param s			the sample providing the start point of the interpolation
	 * @param t			the sample providing the end point of the interpolation
	 * @param width		the width for the region where we draw on
	 * @param height	the height for the region where we draw on
	 * @return			a shape interpolating the two samples
	 */
	public Shape interpolate(Stroke stroke, RegionSample s, RegionSample t, double width, double height);
}
