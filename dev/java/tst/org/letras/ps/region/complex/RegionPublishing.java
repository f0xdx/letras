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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.letras.api.region.RegionData;
import org.letras.api.region.shape.Bounds;
import org.letras.ps.region.RegionProcessor;
import org.letras.psi.iregion.IRegion;
import org.letras.util.region.document.RegionDocument;
import org.letras.util.region.document.RegionDocumentPublisher;
import org.mundo.rt.Mundo;

public class RegionPublishing {
	private static RegionProcessor rp;

	@BeforeClass
	public static void initializeRPS() {
		Mundo.init();
		rp = RegionProcessor.getInstance();
	}
	
	@Test
	public void testDocument1() throws Exception {
		testDocument("testDocument1.regions");
	}

	@Test
	public void testDocument2() throws Exception {
		testDocument("testDocument2.regions");
	}
	
	@Test
	public void testDocument3() throws Exception {
		testDocument("testDocument3.regions");
	}

	private boolean regionSetsEqual(Set<RegionData> set1, Set<IRegion> set2) {
		// FIXME this is a hack until LocalRegion provides a sane equals implementation
		for (RegionData r1: set1) {
			boolean found = false;
			for (IRegion r2: set2) {
				if (r1.shape().equals(r2.shape()))
					found = true;
			}
			if (!found)
				return false;
		}
		for (IRegion r1: set2) {
			boolean found = false;
			for (RegionData r2: set1) {
				if (r2.shape().equals(r1.shape()))
					found = true;
			}
			if (!found)
				return false;
		}
		return true;
	}
	
	private void testDocument(String fileName) throws Exception {
		InstrumentedRegionManager manager = (InstrumentedRegionManager) rp.getRegionManager();
		assertTrue(manager.getAllRegions().isEmpty());
		File f = new File(fileName);
		assertTrue(f.exists());
		RegionDocument doc = RegionDocument.fromFile(f);
		RegionDocumentPublisher publisher = new RegionDocumentPublisher(doc);
		Thread.sleep(1000);
		try {
			Bounds pageRegionBounds = doc.getRegions().get(0).shape().getBounds();
			manager.retrieveRegionAt(pageRegionBounds.getX() + pageRegionBounds.getWidth() / 2,
					pageRegionBounds.getY() + pageRegionBounds.getHeight() / 2);
			Thread.sleep(1000);
			assertTrue(regionSetsEqual(new HashSet<RegionData>(doc.getRegions()), manager .getAllRegions()));
		} finally {
			doc.close();
		}
		Thread.sleep(1000);
		assertTrue(manager.getAllRegions().isEmpty());
	}
	
	@AfterClass
	public static void tearDownRPS() {
		Mundo.shutdown();
	}
}
