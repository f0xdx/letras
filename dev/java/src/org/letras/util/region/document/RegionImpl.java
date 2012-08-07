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
package org.letras.util.region.document;

import org.letras.api.region.RegionData;
import org.letras.api.region.shape.IShape;
import org.letras.psi.iregion.IRegion;
import org.mundo.annotation.mcSerialize;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;

/**
 * Default {@link IRegion} implementation. Clients can use this for publishing regions.
 * Publishing a region is achieved by calling {@link Mundo#registerService(Service)} on
 * an instance of this class.<p>
 * 
 * Just instantiating a <code>RegionImpl</code> is not enough to receive events and samples.
 * See {@link RegionAdapterFactory} and {@link RegionAdapter} for this.
 * 
 * @author jannik
 *
 */
@mcSerialize
public class RegionImpl extends Service implements IRegion {
	public static final String DefaultServiceZone = "lan";
	
	protected RegionData data;

	/**
	 * Initializes a new Region.
	 * @param channel the channel where the region receives events
	 * @param hungry the hungry flag of the region 
	 * @param shape the shape of the region
	 */
	public RegionImpl(String uri, String channel, boolean hungry, IShape shape) {
		data = new RegionData(uri, channel, hungry, shape);
	}
	
	/**
	 * Constructs a new Region that is a copy of another region.
	 * @param region the region to copy
	 */
	public RegionImpl(IRegion region) {
		this(region.uri(), region.channel(), region.hungry(), region.shape());
	}
	
	/**
	 * Initializes a new Region. The channel is initialized to the shape's hash code in hex.
	 * @param hungry the hungry flag of the region
	 * @param shape the shape of the region
	 */
	public RegionImpl(String uri, boolean hungry, IShape shape) {
		this(uri, uri, hungry, shape);
	}
	
	/**
	 * No-arg constructor for deserialization.
	 */
	protected RegionImpl() {
		data = new RegionData();
	}
	
	@Override
	public String uri() {
		return data.uri();
	}

	@Override
	public String channel() {
		return data.channel();
	}

	@Override
	public boolean hungry() {
		return data.hungry();
	}
	
	@Override
	public IShape shape() {
		return data.shape();
	}
	
	@Override
	public String toString() {
		return "region(" + data.uri() + ", " + data.channel() + ", " + data.hungry() + ", " + data.shape() + ")";
	}

	@Override
	public int hashCode() {
		return (data.uri() != null) ? data.uri().hashCode() : super.hashCode();
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
		return data.uri().equals(other.uri());
	}
	
	public RegionData getRegionData() {
		return this.data.copy();
	}
	
	public boolean deepEquals(RegionImpl other) {
		return data.deepEquals(other.getRegionData());
	}

	public static RegionImpl from(RegionData region) {
		return new RegionImpl(region.uri(), region.channel(), region.hungry(), region.shape());
	}
}
