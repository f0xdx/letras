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

public class Test4 extends AbstractDispatchTest {

	public Test4() throws Exception {
		super(new RegionDocumentRegionSet(RegionDocument.fromFile(new File("automated/test4.regions"))),
				"automated/test4.pen");
	}
	
	@Override
	protected void setupConsumers() {
		String[] main = {"test4_main"};
		String[] sub1 = {"test4_sub1"};
		String[] sub11 = {"test4_sub1_1"};
		
//		Stroke1
		consumeEvents(RegionEvent.PEN_DOWN, new String[0], sub1);
		consumeEvents(RegionEvent.TRACE_START, new String[0], sub1);
		consumeSamples("test4_sub1");
		consumeEvents(RegionEvent.TRACE_END, new String[0], sub1);
		consumeEvents(RegionEvent.TRACE_START, new String[0], sub11);
		consumeSamples("test4_sub1_1");
		consumeEvents(RegionEvent.TRACE_END, new String[0], sub11);
		consumeEvents(RegionEvent.PEN_UP, new String[0], sub11);
		
//		Stroke2
		consumeEvents(RegionEvent.PEN_DOWN, new String[0], main);
		consumeEvents(RegionEvent.TRACE_START, new String[0], main);
		consumeSamples("test4_main");
		consumeEvents(RegionEvent.TRACE_END, new String[0], main);
		consumeEvents(RegionEvent.TRACE_START, new String[0], sub1);
		consumeSamples("test4_sub1");
		consumeEvents(RegionEvent.TRACE_END, new String[0], sub1);
		consumeEvents(RegionEvent.TRACE_START, new String[0], sub11);
		consumeSamples("test4_sub1_1");
		consumeEvents(RegionEvent.TRACE_END, new String[0], sub11);
		consumeEvents(RegionEvent.PEN_UP, new String[0], sub11);
		
//		Stroke3
		consumeEvents(RegionEvent.PEN_DOWN, new String[0], sub11);
		consumeEvents(RegionEvent.TRACE_START, new String[0], sub11);
		consumeSamples("test4_sub1_1");
		consumeEvents(RegionEvent.TRACE_END, new String[0], sub11);
		consumeEvents(RegionEvent.TRACE_START, new String[0], sub1);
		consumeSamples("test4_sub1");
		consumeEvents(RegionEvent.TRACE_END, new String[0], sub1);
		consumeEvents(RegionEvent.TRACE_START, new String[0], main);
		consumeSamples("test4_main");
		consumeEvents(RegionEvent.TRACE_END, new String[0], main);
		consumeEvents(RegionEvent.PEN_UP, new String[0], main);
	}

}
