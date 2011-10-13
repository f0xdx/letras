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
package org.letras.psi.semantic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.letras.psi.iregion.IRegion;
import org.mundo.annotation.mcSerialize;
import org.mundo.rt.GUID;

/**
 * A <code>Segmentation</code> is an ordered set of digital ink traces that is semantically related. 
 * @author Jannik Jochem
 *
 */
@mcSerialize
public class Segmentation {
	// we dont put the generic type here to avoid build warnings in the Mundo-generated Metaclass
	@SuppressWarnings("rawtypes")
	protected List traces;
	// FIXME need to put region id here instead of region, otherwise carnage will follow!
	protected IRegion region;
	
	public Segmentation() {
	}
	
	public Segmentation(Collection<? extends GUID> traces) {
		traces = new ArrayList<GUID>(traces);
	}
	
	@SuppressWarnings("unchecked")
	protected List<GUID> getTracesInternal() {
		if (traces == null) {
			traces = new ArrayList<GUID>();
		}
		return traces;
	}
	
	public List<GUID> getTraces() {
		return Collections.unmodifiableList(getTracesInternal());
	}
	
	public void addTrace(GUID g) {
		getTracesInternal().add(g);
	}
	
	public void removeTrace(GUID g) {
		getTracesInternal().remove(g);
	}
}
