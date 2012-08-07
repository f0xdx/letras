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
package org.letras.ps.region.complex.automated.regionset;

import java.io.File;

import org.letras.api.region.RegionEvent;
import org.letras.ps.region.complex.AbstractDispatchTest;
import org.letras.util.region.document.RegionDocumentRegionSet;
import org.letras.util.region.document.RegionDocument;

public class HungryTest4 extends AbstractDispatchTest {

	public HungryTest4() throws Exception {
		super(new RegionDocumentRegionSet(RegionDocument.fromFile(new File("automated/htest4.regions"))),
				"automated/htest4.pen");
	}
	
	@Override
	protected void setupConsumers() {
		
		String main = "htest4_main";
		String sub1 = "htest4_sub1";
		String sub11 = "htest4_sub1_1";
		
		String[] mainArray = {main};
		String[] sub1Array = {sub1};
		String[] sub11Array = {sub11};
		final String[] emptyStringArray = new String[0];
		
		consumeEvents(RegionEvent.PEN_DOWN, emptyStringArray, mainArray);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, mainArray);
		consumeSamples(main);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, mainArray);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, sub1Array);
		consumeSamples(sub1);
		consumeEvents(RegionEvent.TRACE_END, sub1Array, emptyStringArray);
		consumeEvents(RegionEvent.TRACE_START, sub1Array, sub11Array);
		consumeSamples(sub1, sub11);
		consumeEvents(RegionEvent.TRACE_END, sub1Array, sub11Array);
		consumeEvents(RegionEvent.TRACE_START, sub1Array, emptyStringArray);
		consumeSamples(sub1);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, sub1Array);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, mainArray);
		consumeSamples(main);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, mainArray);
		consumeEvents(RegionEvent.PEN_UP, emptyStringArray, mainArray);
	}

}
