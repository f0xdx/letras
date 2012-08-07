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

public class Test5 extends AbstractDispatchTest {

	public Test5() throws Exception {
		super(new RegionDocumentRegionSet(RegionDocument.fromFile(new File("automated/test5.regions"))),
				"automated/test5.pen");
	}
	
	@Override
	protected void setupConsumers() {
		String mainName = "test5_main";
		String sub1Name = "test5_sub1";
		String sub2Name = "test5_sub2";
		String[] main = {mainName};
		String[] sub1 = {sub1Name};
		String[] sub2 = {sub2Name};
		final String[] emptyStringArray = new String[0];
		
//		Stroke1
		consumeEvents(RegionEvent.PEN_DOWN, emptyStringArray, sub1);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, sub1);
		consumeSamples(sub1Name);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, sub1);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, main);
		consumeSamples(mainName);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, main);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, sub2);
		consumeSamples(sub2Name);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, sub2);
		consumeEvents(RegionEvent.PEN_UP, emptyStringArray, sub2);
		
		
//		Stroke2
		consumeEvents(RegionEvent.PEN_DOWN, emptyStringArray, main);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, main);
		consumeSamples(mainName);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, main);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, sub2);
		consumeSamples(sub2Name);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, sub2);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, main);
		consumeSamples(mainName);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, main);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, sub1);
		consumeSamples(sub1Name);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, sub1);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, main);
		consumeSamples(mainName);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, main);
		consumeEvents(RegionEvent.PEN_UP, emptyStringArray, main);
	}

}
