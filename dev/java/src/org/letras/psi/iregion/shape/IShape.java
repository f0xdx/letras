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
package org.letras.psi.iregion.shape;




/**
 * A shape in the Region Model. Shapes need to be able to perform a point test and compute their minimal bounding box.
 * @author jannik
 *
 */
public interface IShape {
	
	/**
	 * @return the guaranteed-minimal bounding box of this shape.
	 */
	public Bounds getBounds();
	
	/**
	 * @param x
	 * @param y
	 * @return true iff the point (x,y) is inside the shape or on the boundary of the shape
	 */
	public boolean contains(double x, double y);
}
