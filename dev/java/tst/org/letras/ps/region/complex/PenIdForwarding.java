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
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.letras.api.region.RegionEvent;
import org.letras.api.region.RegionSample;
import org.letras.ps.region.RegionProcessor;
import org.letras.psi.iregion.IDigitalInkConsumer;
import org.letras.psi.iregion.IRegion;
import org.letras.tools.penrecorder.PenRecording;
import org.letras.tools.penrecorder.PenRecordingPlayer;
import org.letras.util.region.document.RegionDocument;
import org.letras.util.region.document.RegionDocumentPublisher;
import org.letras.util.region.document.RegionDocumentReceiver;
import org.mundo.rt.Mundo;

public class PenIdForwarding implements IDigitalInkConsumer {

	@BeforeClass
	public static void initializeRPS() {
		Mundo.init();
		RegionProcessor.getInstance();
	}

	private String player1Channel;
	private String player2Channel;
	private boolean player1Finished;
	private boolean player2Finished;
	
	@Test
	public void testPenIdForwarding() throws Exception {
		File f = new File("penIdTestDocument.regions");
		RegionDocument doc = RegionDocument.fromFile(f);
		RegionDocumentPublisher pub = new RegionDocumentPublisher(doc);
		new RegionDocumentReceiver(doc).setActiveConsumer(this);
		
		PenRecordingPlayer player1 = new PenRecordingPlayer();
		player1Channel = player1.penId();
		player1.setServiceZone("lan");
		Mundo.registerService(player1);
		
		PenRecordingPlayer player2 = new PenRecordingPlayer();
		player2Channel = player2.penId();
		player2.setServiceZone("lan");
		Mundo.registerService(player2);
		
		Thread.sleep(1000);

		player1.play(new PenRecording(new File("penIdStroke1.pen")));
		player2.play(new PenRecording(new File("penIdStroke2.pen")));

		player1Finished = false;
		player2Finished = false;
		
		long startTime = System.currentTimeMillis();
		synchronized(this) {
			while (!(player1Finished && player2Finished) && (System.currentTimeMillis() - startTime) < 15000) {
				wait(1000);
			}
		}
		
		assertTrue(player1Finished);
		assertTrue(player2Finished);
		Mundo.unregisterService(player1);
		Mundo.unregisterService(player2);
	}
	
	@AfterClass
	public static void tearDownRPS() {
		Mundo.shutdown();
	}

	@Override
	public void consume(IRegion source, RegionSample regionSample) {
		if (regionSample.getPscY() <= 8389000)
			assertEquals(regionSample.getPenID(), player1Channel);
		else if (regionSample.getPscY() >= 8389100)
			assertEquals(regionSample.getPenID(), player2Channel);
		else
			fail("Illegal sample received!");
	}

	@Override
	public void consume(IRegion source, RegionEvent regionEvent) {
		System.out.println("Event received: " + regionEvent);
		synchronized (this) {
			if (regionEvent.penUp()) {
				if (regionEvent.getPenID().equals(player1Channel))
					player1Finished = true;
				else if (regionEvent.getPenID().equals(player2Channel))
					player2Finished = true;
				notifyAll();
			}
		}
	}
}
