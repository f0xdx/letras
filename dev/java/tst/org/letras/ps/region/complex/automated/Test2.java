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

public class Test2 extends AbstractDispatchTest {

	public Test2() throws Exception {
		super("automated/test2.regions", "automated/test2.pen");
	}
	
	@Override
	protected void setupConsumers() {
		String[] main = {"test2_main"};
		consumeEvents(RegionEvent.PEN_DOWN, new String[0], main);
		consumeEvents(RegionEvent.TRACE_START, new String[0], main);
		consumeSamples("test2_main");
		consumeEvents(RegionEvent.TRACE_END, new String[0], main);
		consumeEvents(RegionEvent.PEN_UP, new String[0], main);
		String[] sub1 = {"test2_sub1"};
		consumeEvents(RegionEvent.PEN_DOWN, new String[0], sub1);
		consumeEvents(RegionEvent.TRACE_START, new String[0], sub1);
		consumeSamples("test2_sub1");
		consumeEvents(RegionEvent.TRACE_END, new String[0], sub1);
		consumeEvents(RegionEvent.PEN_UP, new String[0], sub1);
	}

}
