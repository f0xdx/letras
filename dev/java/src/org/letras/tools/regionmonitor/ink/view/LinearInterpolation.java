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
import java.awt.geom.Line2D;

import org.letras.api.region.RegionSample;
import org.letras.tools.regionmonitor.ink.model.Stroke;

/**
 * This provides a simple linear interpolation (i.e. the connecting shapes between
 * two samples are drawn as lines)
 * 
 * @author felix_h
 * @version 0.0.1
 */
public class LinearInterpolation implements IInterpolation {

	@Override
	public Shape interpolate(Stroke stroke, RegionSample s, RegionSample t, double width,
			double height) {
		Line2D.Double line = new Line2D.Double(s.getX() * width, s.getY() * height, 
				t.getX() * width, t.getY() * height);
		return line;
	}
	
	// members

	// constructors

	// methods
}
