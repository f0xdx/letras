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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.letras.api.region.RegionData;
import org.letras.ps.region.RegionProcessor;
import org.letras.tools.designer.RegionCreationConsumer;
import org.letras.tools.penrecorder.PenRecording;
import org.letras.tools.penrecorder.PenRecordingStepper;
import org.letras.util.region.document.IRegionDocumentListener;
import org.letras.util.region.document.RegionDocument;
import org.letras.util.region.document.RegionDocumentPublisher;
import org.mundo.rt.Mundo;

public class RegionCreation {
	@BeforeClass
	public static void initializeRPS() {
		Mundo.init();
		RegionProcessor.getInstance();
	}
	
	@Test
	public void regionCreationTest() throws Exception {
		RegionDocument doc1 = RegionDocument.fromFile(new File("testDocument1.regions"));
		RegionDocumentPublisher publisher1 = new RegionDocumentPublisher(doc1);
		doc1.close();
		RegionDocument doc2 = RegionDocument.fromFile(new File("testDocument2.regions"));
		RegionDocumentPublisher publisher2 = new RegionDocumentPublisher(doc2);
		final Semaphore creationSemaphore = new Semaphore(1);
		
		doc2.addDocumentListener(new IRegionDocumentListener() {
			
			@Override
			public void regionRemoved(RegionData region) {
				throw new IllegalArgumentException();
			}
			
			@Override
			public void regionAdded(RegionData region) {
				// Make sure we get something meaningful. Testing for exact values is out of the scope.
				assertFalse(region.shape().getBounds().getX() == 0);
				assertFalse(region.shape().getBounds().getY() == 0);
				assertFalse(region.shape().getBounds().getWidth() == 0);
				assertFalse(region.shape().getBounds().getHeight() == 0);
				creationSemaphore.release();
			}

			@Override
			public void documentNameChanged() {
			}

			@Override
			public void modificationStateChanged() {
			}

			@Override
			public void regionModified(RegionData oldRegion,
					RegionData newRegion) {
			}

			@Override
			public void pageChanged(RegionData newPage) {
			}
		});
		
		new RegionCreationConsumer(doc2);
		
		PenRecordingStepper player = new PenRecordingStepper();
		player.setServiceZone("lan");
		Mundo.registerService(player);
		player.load(new PenRecording(new File("testStroke.pen")));
		Thread.sleep(500);
		creationSemaphore.acquire();
		while(player.hasNext()) {
			player.stepFigure();
			assertTrue("No region created", creationSemaphore.tryAcquire(20, TimeUnit.SECONDS));
		}
		Mundo.unregisterService(player);
	}
	
	@AfterClass
	public static void tearDownRPS() {
		Mundo.shutdown();
	}

}
