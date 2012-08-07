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
package org.letras.tools.regionmonitor.remote;

import java.awt.geom.Rectangle2D;

import org.letras.api.region.shape.IShape;
import org.letras.api.region.shape.RectangularShape;
import org.letras.psi.iregion.IRegion;
import org.mundo.rt.Service;

/**
 * {@code BasicRegionImplementation} is a simple Implementation of the IRegion 
 * interface. This implementation should only be used by the RegionMonitor tool 
 * and only for debugging. 
 * @author niklas
 */
class BasicRegionImplementation extends Service implements IRegion {

	//members
	private String uri;
	private String channel;
	private RectangularShape shape;
	
	
	/**
	 * Creates a new IRegion-Instance with the given dimensions
	 * @param area dimensions of the region
	 */
	public BasicRegionImplementation(Rectangle2D area) {
		channel = String.valueOf(area.hashCode());
		uri = channel;
		shape = new RectangularShape(area.getX(), area.getY(), area.getWidth(), area.getHeight());
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public String channel() {
		return channel;
	}

	@Override
	public boolean hungry() {
		return false;
	}

	@Override
	public IShape shape() {
		return shape;
	}

	@Override
	public String uri() {
		return uri;
	}

	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IRegion))
			return false;
		IRegion other = (IRegion) obj;
		return uri.equals(other.uri());
	}
	
}
