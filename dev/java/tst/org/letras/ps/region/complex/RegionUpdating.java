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
package org.letras.ps.region.complex;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.letras.api.region.RegionEvent;
import org.letras.api.region.RegionSample;
import org.letras.api.region.shape.RectangularShape;
import org.letras.ps.region.RegionProcessor;
import org.letras.psi.iregion.IDigitalInkConsumer;
import org.letras.psi.iregion.IRegion;
import org.letras.tools.penrecorder.PenRecording;
import org.letras.tools.penrecorder.PenRecordingPlayer;
import org.letras.util.region.document.RegionAdapterFactory;
import org.letras.util.region.document.RegionImpl;
import org.mundo.rt.Mundo;

public class RegionUpdating implements IDigitalInkConsumer {
	private static RegionProcessor rp;

	@BeforeClass
	public static void initializeRPS() {
		Mundo.init();
		rp = RegionProcessor.getInstance();
	}

	private int state = 0;
	// 0 = waiting for pen down to region1
	// 1 = waiting for sample to region1
	// 2 = waiting for pen up to region1
	// 3 = waiting for pen down to region2
	// 4 = waiting for sample to region2
	// 5 = waiting for pen up to region2
	// 6 = finished
	private RegionImpl region1;
	private RegionImpl region2;
	
	@Test
	public void testRegionUpdating() throws Exception {
		RectangularShape shape = new RectangularShape(185977914.125, 8388733.625, 640.0, 503.375);
		region1 = new RegionImpl("region://1", false, shape);
		RegionAdapterFactory.getInstance().adapt(region1).addConsumer(this);
		region2 = new RegionImpl("region://2", true, shape);
		
		Mundo.registerService(region1);
		
		PenRecordingPlayer player = new PenRecordingPlayer();
		player.setServiceZone("lan");
		Mundo.registerService(player);
		Thread.sleep(500);

		long startTime = System.currentTimeMillis();
		long delta = 0;
		player.play(new PenRecording(new File("penIdStroke1.pen")));
		synchronized(this) {
			while (state < 3 && delta < 10000) {
				System.out.println("state = " + state + ", delta = " + delta);
				wait(1000);
				delta = System.currentTimeMillis() - startTime;
			}
		}
		
		assertEquals("Samples to region 1 not received correctly", 3, state);
		Mundo.unregisterService(region1);
		RegionAdapterFactory.getInstance().adapt(region1).removeConsumer(this);
		RegionAdapterFactory.getInstance().adapt(region2).addConsumer(this);
		Mundo.registerService(region2);
		Thread.sleep(1000);
		
		startTime = System.currentTimeMillis();
		player.play(new PenRecording(new File("penIdStroke1.pen")));
		delta = 0;
		synchronized(this) {
			while (state < 6 && delta < 10000) {
				wait(1000);
				delta = System.currentTimeMillis() - startTime;
			}
		}
		
		assertEquals("Samples to region 2 not received correctly", 6, state);
		
		Mundo.unregisterService(region2);
		RegionAdapterFactory.getInstance().adapt(region2).removeConsumer(this);
		Mundo.unregisterService(player);
	}
	
	@AfterClass
	public static void tearDownRPS() {
		Mundo.shutdown();
	}

	@Override
	public void consume(IRegion source, RegionSample regionSample) {
		synchronized(this) {
			if (source == region1) {
				if (state > 2)
					fail("Sample for region1 received although it is no longer published!");
				assertTrue((state == 1) || (state == 2));
				state = 2;
			} else if (source == region2) {
				if (state < 3)
					fail("Sample for region2 received although it is not yet published!");
				assertTrue((state == 4) || (state == 5));
				state = 5;
			} else {
				fail("Sample for invalid region received!");
			}
		}
	}

	@Override
	public void consume(IRegion source, RegionEvent regionEvent) {
		assertFalse(region1 == region2);
		synchronized(this) {
			if (source == region1) {
				if (state > 2)
					fail("Event for region1 received although it is no longer published!");
				if (regionEvent.penDown()) {
					assertEquals(state, 0);
					state = 1;
				} else if (regionEvent.penUp()){
					assertEquals(state, 2);
					state = 3;
					notifyAll();
				}
			} else if (source == region2) {
				if (state < 3)
					fail("Event for region2 received although it is not yet published!");
				if (regionEvent.penDown()) {
					assertEquals(state, 3);
					state = 4;
				} else if (regionEvent.penUp()){
					assertEquals(state, 5);
					state = 6;
					notifyAll();
				}
			} else {
				fail("Event for invalid region received!");
			}
		}
	}
}
