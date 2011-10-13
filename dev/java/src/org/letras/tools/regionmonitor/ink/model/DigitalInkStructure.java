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

import java.awt.geom.Rectangle2D;

import org.letras.psi.iregion.IRegion;

/**
 * This is the common base class for all digital ink data structures.
 *
 * @author Felix Heinrichs <felix.heinrichs@gmail.com>
 * @version 0.1
 */
public class DigitalInkStructure {

	// members

	protected Rectangle2D.Double boundingBox;
	protected long lowerTBound;
	protected long upperTBound;
	protected String penId;
	protected IRegion region;

	/**
	 * @return the penId
	 */
	public String getPenId() {
		return penId;
	}

	/**
	 * @param penId the penId to set
	 */
	public void setPenId(String penId) {
		this.penId = penId;
	}

	/**
	 * @return the boundingBox
	 */
	public Rectangle2D.Double getBoundingBox() {
		return boundingBox;
	}

	/**
	 * @param boundingBox the boundingBox to set
	 */
	public void setBoundingBox(Rectangle2D.Double boundingBox) {
		this.boundingBox = boundingBox;
	}

	/**
	 * @return the region
	 */
	public IRegion getRegion() {
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(IRegion region) {
		this.region = region;
	}

	// constructors

	/**
	 * No-argument constructor used for initialization.
	 */
	public DigitalInkStructure(){
		this.boundingBox = new Rectangle2D.Double();
	}

	// methods

	/**
	 * This returns the lower temporal bound of this digital
	 * ink structure (t_s).
	 * 
	 * @return the lower temporal bound of this digital ink
	 * strucutre
	 */
	public long getLowerTBound() {
		return this.lowerTBound;
	}

	/**
	 * This returns the upper temporal bound of this digital
	 * ink structure (t_e).
	 *
	 * @return the upper temporal bound of this digital ink
	 * structure
	 */
	public long getUpperTbound() {
		return this.upperTBound;
	}
}
