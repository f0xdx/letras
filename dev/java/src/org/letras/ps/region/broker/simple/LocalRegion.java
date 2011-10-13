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
package org.letras.ps.region.broker.simple;

import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.shape.IShape;

/**
 * A LocalRegion is an adapter as well as a proxy for the remote {@link IRegion}
 * interface.<br>
 * It behaves as an adapter as it implements the methods {@link #equals(Object)}
 * and {@link #hashCode()} that are needed locally but are not declared in the 
 * {@link IRegion} interface.
 * <br>
 * It behaves as a proxy in the way that it caches the values of the remote object
 * so that repeating calls to the getter functions will not repeatedly call the
 * remote methods. 
 *  
 * @author niklas
 *
 */
public class LocalRegion implements IRegion {

	private IRegion remoteRegion;
	
	private String uri = null;
	private String channelName = null;
	private boolean hungry;
	private IShape shape = null;

	public LocalRegion(IRegion remoteRegion) {
		this.remoteRegion = remoteRegion;
		this.uri = remoteRegion.uri();
		this.hungry = remoteRegion.hungry();
	}
	
	@Override
	public String uri() {
		return uri;
	}

	@Override
	public String channel() {
		if (this.channelName == null)
			this.channelName = remoteRegion.channel();
		
		return this.channelName;
	}

	@Override
	public boolean hungry() {
		return this.hungry;
	}

	@Override
	public IShape shape() {
		if (this.shape == null)
			this.shape = remoteRegion.shape();
		
		return this.shape;
	}

	/**
	 * The hash of a region is the hash of the region's GUID.
	 */
	@Override
	public int hashCode() {
		return uri.hashCode();
	}
	

	/**
	 * Equality of two regions is defined by checking their GUIDs.
	 */
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
