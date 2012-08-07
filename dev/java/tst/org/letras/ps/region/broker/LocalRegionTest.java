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
package org.letras.ps.region.broker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.letras.api.region.shape.Bounds;
import org.letras.api.region.shape.IShape;
import org.letras.api.region.shape.RectangularShape;
import org.letras.ps.region.broker.simple.LocalRegion;
import org.letras.psi.iregion.IRegion;

public class LocalRegionTest {

	private IRegion testRegion1;
	
	private class RemoteRegionMock implements IRegion {
		
		private IRegion wrappedRegion;
		private int shouldRequestUriTimes;
		private int shouldRequestShapeTimes;
		private int shouldRequestHungryTimes; 
		private int shouldRequestChannelNameTimes;
		private int requestedUriTimes = 0;
		private int requestedShapeTimes = 0;
		private int requestedHungryTimes = 0;
		private int requestedChannelNameTimes = 0;
		
		public RemoteRegionMock(IRegion regionObject,
								int shouldRequestUriTimes,
								int shouldRequestShapeTimes, 
								int shouldRequestHungryTimes, 
								int shouldRequestChannelNameTimes) {
			wrappedRegion = regionObject;
			this.shouldRequestUriTimes = shouldRequestUriTimes;
			this.shouldRequestChannelNameTimes = shouldRequestChannelNameTimes;
			this.shouldRequestHungryTimes = shouldRequestHungryTimes;
			this.shouldRequestShapeTimes = shouldRequestShapeTimes;
		}

		@Override
		public String channel() {
			requestedChannelNameTimes++;
			assertTrue(String.format("Channel name requested more than %d times", requestedChannelNameTimes), requestedChannelNameTimes <= shouldRequestChannelNameTimes);
			return wrappedRegion.channel();
		}

		@Override
		public boolean hungry() {
			requestedHungryTimes++;
			assertTrue(String.format("Hungry attribute requested more than %d times", requestedHungryTimes), requestedHungryTimes <= shouldRequestHungryTimes);
			return wrappedRegion.hungry();
		}

		@Override
		public IShape shape() {
			requestedShapeTimes++;
			assertTrue(String.format("Shape requested more than %d times", requestedShapeTimes), requestedShapeTimes<= shouldRequestShapeTimes);
			return wrappedRegion.shape();
		}
		
		public void checkTimes() {
			assertEquals(shouldRequestChannelNameTimes, requestedChannelNameTimes);
			assertEquals(shouldRequestHungryTimes, requestedHungryTimes);
			assertEquals(shouldRequestShapeTimes, requestedShapeTimes);
		}

		@Override
		public String uri() {
			requestedUriTimes++;
			assertTrue(String.format("URI requested more than %d times", requestedUriTimes), requestedUriTimes<= shouldRequestUriTimes);
			return wrappedRegion.uri();
		}
		
	}
	
	@Before
	public void setUpBefore() {
		testRegion1 = createRegionFromBoundsWithName(new Bounds(5, 5, 20, 20), "channel1", "regions://1");
	}
	
	

	private IRegion createRegionFromBoundsWithName(final Bounds bounds, final String channelName, final String uri) {
		return new IRegion() {
			
			IShape shape = new RectangularShape(bounds);
			
			@Override
			public IShape shape() {
				return shape;
			}
			
			@Override
			public boolean hungry() {
				return false;
			}
			
			@Override
			public String channel() {
				return channelName;
			}

			@Override
			public String uri() {
				return uri;
			}
		};
	}
	
	@Test
	public void testConstruction() {
		RemoteRegionMock mock = new RemoteRegionMock(testRegion1, 1, 0, 1, 0);
		
		createLocalRegion(mock);
		
		mock.checkTimes();
	}
	
	@Test
	public void testProxyFunction() {
		RemoteRegionMock mock = new RemoteRegionMock(testRegion1, 1, 1, 1, 1);
		
		LocalRegion lr = createLocalRegion(mock);
		
		assertSame(testRegion1.shape(), lr.shape());
		assertSame(testRegion1.hungry(), lr.hungry());
		assertSame(testRegion1.channel(), lr.channel());
		assertSame(testRegion1.uri(), lr.uri());
		
		mock.checkTimes();
	}



	private LocalRegion createLocalRegion(IRegion mock) {
		return new LocalRegion(mock);
	}
	
	@Test
	public void testCaching() {
		RemoteRegionMock mock = new RemoteRegionMock(testRegion1, 1, 1, 1, 1);
		
		LocalRegion lr = createLocalRegion(mock);
		
		assertSame(testRegion1.shape(), lr.shape());
		assertSame(testRegion1.hungry(), lr.hungry());
		assertSame(testRegion1.channel(), lr.channel());
		assertSame(testRegion1.uri(), lr.uri());
		
		assertSame(testRegion1.shape(), lr.shape());
		assertSame(testRegion1.hungry(), lr.hungry());
		assertSame(testRegion1.channel(), lr.channel());
		assertSame(testRegion1.uri(), lr.uri());
		
		mock.checkTimes();
	}
}
