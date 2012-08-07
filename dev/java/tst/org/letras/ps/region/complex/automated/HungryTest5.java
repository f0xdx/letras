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
package org.letras.ps.region.complex.automated;

import org.letras.api.region.RegionEvent;
import org.letras.ps.region.complex.AbstractDispatchTest;

public class HungryTest5 extends AbstractDispatchTest {

	public HungryTest5() throws Exception {
		super("automated/htest5.regions", "automated/htest5.pen");
	}
	
	@Override
	protected void setupConsumers() {
		
		String main = "htest5_main";
		String sub1 = "htest5_sub1";
		String sub11 = "htest5_sub1_1";
		
		String[] mainArray = {main};
		String[] sub1Array = {sub1};
		String[] sub11Array = {sub11};
		String[] mainsub1Array = {main, sub1};
		
		final String[] emptyStringArray = new String[0];
		
		consumeEvents(RegionEvent.PEN_DOWN, emptyStringArray, mainArray);
		consumeEvents(RegionEvent.TRACE_START, emptyStringArray, mainArray);
		consumeSamples(main);
		consumeEvents(RegionEvent.TRACE_END, mainArray, emptyStringArray);
		consumeEvents(RegionEvent.TRACE_START, mainArray, sub1Array);
		consumeSamples(main, sub1);
		consumeEvents(RegionEvent.TRACE_END, mainsub1Array, emptyStringArray);
		consumeEvents(RegionEvent.TRACE_START, mainsub1Array, sub11Array);
		consumeSamples(main, sub1, sub11);
		consumeEvents(RegionEvent.TRACE_END, mainsub1Array, sub11Array);
		consumeEvents(RegionEvent.TRACE_START, mainsub1Array, emptyStringArray);
		consumeSamples(main, sub1);
		consumeEvents(RegionEvent.TRACE_END, mainArray, sub1Array);
		consumeEvents(RegionEvent.TRACE_START, mainArray, emptyStringArray);
		consumeSamples(main);
		consumeEvents(RegionEvent.TRACE_END, emptyStringArray, mainArray);
		consumeEvents(RegionEvent.PEN_UP, emptyStringArray, mainArray);
	}

}
