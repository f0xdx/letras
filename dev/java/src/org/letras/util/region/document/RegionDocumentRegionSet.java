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
package org.letras.util.region.document;

import org.letras.api.region.RegionData;
import org.letras.ps.region.AbstractRegionSet;
import org.letras.psi.iregion.IRegionSet;
import org.letras.util.TypedArrayIdiom;
import org.mundo.rt.GUID;
import org.mundo.rt.TypedArray;

/**
 * An {@link IRegionSet} that can be used to publish {@link RegionDocument}s.
 * 
 * @author Jannik Jochem
 *
 */
public class RegionDocumentRegionSet extends AbstractRegionSet implements IRegionDocumentListener, IRegionSet {
	private RegionDocument document;

	public RegionDocumentRegionSet(String uri, String channel, RegionDocument document) {
		super(uri, channel);
		this.document = document;
		document.addDocumentListener(this);
	}
	
	public RegionDocumentRegionSet(String uri, RegionDocument document) {
		this(uri, "RegionSet." + new GUID().shortString(), document);
	}
	
	public RegionDocumentRegionSet(RegionDocument document) {
		this(document.getPage().uri(), document);
	}

	@Override
	public TypedArray regions() {
		return TypedArrayIdiom.fromCollection(document.getRegions());
	}

	@Override
	public void addRegion(RegionData regionToAdd) {
		document.addRegion(regionToAdd);
	}

	@Override
	public void updateRegion(RegionData updatedRegion) {
		document.updateRegion(document.getRegion(updatedRegion.uri()), updatedRegion);
	}

	@Override
	public void removeRegion(RegionData regionToRemove) {
		document.removeRegion(regionToRemove);
	}

	@Override
	public void regionAdded(RegionData region) {
		fireUpdate();
	}

	@Override
	public void regionRemoved(RegionData region) {
		fireUpdate();
	}

	@Override
	public void regionModified(RegionData oldRegion, RegionData newRegion) {
		fireUpdate();
	}

	@Override
	public void pageChanged(RegionData newPage) {
	}

	@Override
	public void modificationStateChanged() {
	}

	@Override
	public void documentNameChanged() {
	}

	/**
	 * @return the published document
	 */
	public RegionDocument getDocument() {
		return document;
	}

}
