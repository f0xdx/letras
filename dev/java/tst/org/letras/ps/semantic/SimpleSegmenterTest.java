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
package org.letras.ps.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.letras.ps.region.RegionProcessor;
import org.letras.ps.region.complex.AbstractDispatchTest;
import org.letras.psi.iregion.IDigitalInkConsumer;
import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.RegionEvent;
import org.letras.psi.iregion.RegionSample;
import org.letras.psi.semantic.ISegmenter;
import org.letras.tools.penrecorder.PenRecording;
import org.letras.tools.penrecorder.PenRecordingPlayer;
import org.letras.util.region.document.RegionDocument;
import org.mundo.rt.GUID;
import org.mundo.rt.Logger;
import org.mundo.rt.Mundo;

public class SimpleSegmenterTest implements IDigitalInkConsumer {
	private static RegionProcessor rp;
	private static Logger log;
	private static PenRecordingPlayer player;

	private RegionDocument doc;
	private GUID activeTrace;

	@BeforeClass
	public static void initialize() {
		Mundo.init();
		rp = RegionProcessor.getInstance();
		log = Logger.getLogger(AbstractDispatchTest.class);
		player = new PenRecordingPlayer();
	}
	
	@Test
	public void testSimpleSegmenter() throws Exception {
		ISegmenter segmenter = new SimpleSegmenter();
		DummySemanticService sink = new DummySemanticService();
		sink.setSegmenter(segmenter.getSegmentationProvider());
		
//		PublishedRegionDocument doc = PublishedRegionDocument.fromFile(new File("segmentation1.regions"));
//		doc.setActiveConsumer(this);
//		sink.addRegion(doc.get("child"));
//		doc.close();

		player.playSync(new PenRecording(new File("segmentation1.pen")));
		Thread.sleep(500);
		assertEquals(sink.takeSegmentation().getTraces().get(0), activeTrace);
	}

	@AfterClass
	public static void tearDownRPS() {
		Mundo.shutdown();
	}

	@Override
	public void consume(IRegion source, RegionSample regionSample) {
		if (activeTrace == null)
			fail("Sample received with no trace started!");
	}

	@Override
	public void consume(IRegion source, RegionEvent regionEvent) {
		if (activeTrace == null) {
			activeTrace = regionEvent.getGuid();
		} else {
			fail("Second trace started while none was expected!");
		}
	}
}
