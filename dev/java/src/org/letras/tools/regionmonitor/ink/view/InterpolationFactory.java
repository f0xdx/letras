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

/**
 * The {@link InterpolationFactory} provides the currently used interpolation
 * scheme to all interested parties (e.g. {@link DigitalInkRenderer}).
 * 
 * @author felix_h
 *
 */
public class InterpolationFactory {
	
	// TODO make this factory changeable: the interpolation method should be configurable,
	// which then should be handled in the digital ink service stuff
	
	public static enum Methods {
		LINEAR, CR_CUBIC_B_SPLINE;
	}

	// defaults
	
	public static final Methods DEFAULT_INTERPOLATION = Methods.CR_CUBIC_B_SPLINE;
	
	// members
 
	// constructors
	
	// methods

	/**
	 * Creates the default interpolation method.
	 */
	public static IInterpolation createInterpolation() {
		return createInterpolation(DEFAULT_INTERPOLATION);
	}
	
	/**
	 * Creates an interpolation method.
	 * 
	 * @param method
	 * @return
	 */
	public static IInterpolation createInterpolation(Methods method) {
		switch (method) {
			case LINEAR: return new LinearInterpolation();
			case CR_CUBIC_B_SPLINE: return new  CatmullRomSplineInterpolation();
			default: return null;
		}
	}
	
	/**
	 * Creates a new interpolation method. The provided string must match the
	 * name of any of the Enum fields in {@link Methods}.
	 * 
	 * @param method
	 * @return
	 */
	public static IInterpolation createInterpolation(String method) {
		System.out.format("providing interpolation:%s%n", method);
		return createInterpolation(Methods.valueOf(method));
	}

}
