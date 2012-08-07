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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.letras.api.region.RegionData;
import org.letras.api.region.shape.Bounds;
import org.letras.api.region.shape.RectangularShape;
import org.letras.util.region.document.xml.persist.XmlRegionDocumentLoader;
import org.letras.util.region.document.xml.persist.XmlRegionDocumentPersister;

public class RegionDocument {
	private Map<String, RegionData> regions;
	private String pageUri;
	private String uri;
	
	private List<IRegionDocumentListener> listeners = new LinkedList<IRegionDocumentListener>();
	private boolean modified;
	private File file;
	
	public RegionDocument() {
		regions = new HashMap<String, RegionData>();
	}
	
	public RegionDocument(String uri, RegionData page, List<RegionData> nonPageRegions) {
		this.uri = uri;
		pageUri = page.uri();
		regions = new HashMap<String, RegionData>();
		regions.put(pageUri, page);
		for (RegionData child: nonPageRegions) {
			addRegion(child);
		}
	}
	
	public RegionDocument(RegionDocument regionDocument) {
		regions = new HashMap<String, RegionData>(regionDocument.regions);
		uri = regionDocument.uri;
		pageUri = regionDocument.pageUri;
	}
	
	public RegionDocument(String uri, double x, double y, double width, double height) {
		this();
		this.uri = uri;
		pageUri = uri + "/page";
		RegionData page = new RegionData(pageUri, false, new RectangularShape(x, y, width, height));
		regions.put(pageUri, page);
		fireRegionAdded(page);
		setModified(true);
	}

	public static RegionDocument fromFile(File f) {
		RegionDocument doc = XmlRegionDocumentLoader.loadRegionDocument(f);
		doc.file = f;
		doc.modified = false;
		return doc;
	}
	
	public void saveToFile(File f) {
		XmlRegionDocumentPersister.persistRegionDocument(this, f);
		setFile(f);
		setModified(false);
	}

	private void setFile(File f) {
		file = f;
		if (f == null || !f.equals(file)) 
			fireDocumentNameChanged();
	}

	public void save() throws Exception {
		saveToFile(file);
	}
	
	private void setModified(boolean b) {
		if (b != modified) {
			this.modified = b;
			fireModificationStateChanged();
		}
	}

	public boolean isModified() {
		return modified;
	}

	public boolean regionFits(RegionData newRegion) {
		Bounds newRegionBounds = newRegion.shape().getBounds();
		for (RegionData otherRegion: regions.values()) {
			if (otherRegion.shape().getBounds().strictIntersects(newRegionBounds))
				return false;
		}
		return true;
	}
	
	public void addRegion(RegionData newRegion) {
		String uri = newRegion.uri();
		if (regions.containsKey(uri)) {
			throw new IllegalArgumentException("Trying to add region with uri " + uri + ", but a region with that uri is already part of the document.");
		}
		boolean isNewPage = false;
		if (getPage() != null && !getPage().shape().getBounds().contains(newRegion.shape().getBounds())) {
			if (newRegion.shape().getBounds().contains(getPage().shape().getBounds()))
				isNewPage = true;
			else
				throw new IllegalArgumentException("Trying to add region " + newRegion + " that does not fit inside page of the document.");
		}
		regions.put(uri, newRegion);
		isNewPage |= getPage() == null;
		if (isNewPage)
			pageUri = uri;
		fireRegionAdded(newRegion);
		if (isNewPage)
			firePageChanged(newRegion);
		setModified(true);
	}
	
	public void removeRegion(RegionData region) {
		String uri = region.uri();
		if (uri.equals(pageUri)) {
			regions.remove(uri);
			pageUri = null;
			if (regions.size() > 0) {
				RegionData newPage = null;
				for (RegionData r: regions.values()) {
					if (newPage == null || 
							r.shape().getBounds().contains(newPage.shape().getBounds())) 
						newPage = r;
				}
				setPage(newPage);
			}
		}
		regions.remove(uri);
		fireRegionRemoved(region);
		setModified(true);
	}

	public RegionData getPage() {
		return regions.get(pageUri);
	}

	public void setPage(RegionData newPage) {
		String newPageUri = newPage.uri();
		pageUri = newPageUri;
		if (!regions.containsKey(newPageUri)) {
			regions.put(newPageUri, newPage);
			fireRegionAdded(newPage);
		}
		firePageChanged(newPage);
		setModified(true);
	}

	protected void updateRegion(RegionData regionToUpdate, RegionData newRegion) {
		String uri = regionToUpdate.uri();
		if (!uri.equals(newRegion.uri()))
				throw new IllegalArgumentException("Cannot change region uri!");
		regions.put(uri, newRegion);
		RegionAdapterFactory.getInstance().adapt(newRegion);
		fireRegionModified(regionToUpdate, newRegion);
		if (newRegion.uri().equals(pageUri))
			firePageChanged(newRegion);
		setModified(true);
	}

	
	public RegionData updateRegionChannel(RegionData regionToUpdate, String newChannel) {
		if (!getRegions().contains(regionToUpdate))
			throw new IllegalArgumentException("Trying to update unknown region!");
		RegionData region = (RegionData) regionToUpdate;
		RegionData newRegion = new RegionData(region.uri(), newChannel, region.hungry(), region.shape());
		if (!region.channel().equals(newRegion.channel())) {
			updateRegion(regionToUpdate, newRegion);
			return newRegion;
		} else {
			return region;
		}
	}

	public RegionData updateRegionHungry(RegionData regionToUpdate, boolean newHungry) {
		if (!getRegions().contains(regionToUpdate))
			throw new IllegalArgumentException("Trying to update unknown region!");
		RegionData region = (RegionData) regionToUpdate;
		RegionData newRegion = new RegionData(region.uri(), region.channel(), newHungry, region.shape());
		if (region.hungry() != newRegion.hungry()) {
			updateRegion(regionToUpdate, newRegion);
			return newRegion;
		} else {
			return region;
		}
	}
	
	public void close() {
		for (RegionData region: new ArrayList<RegionData>(regions.values())) {
			if (region.uri().equals(pageUri))
				continue;
			removeRegion(region);
		}
		removeRegion(getPage());
		listeners.clear();
		regions.clear();		
	}
	
	public List<RegionData> getRegions() {
		return new ArrayList<RegionData>(regions.values());
	}
	
	public RegionData getRegion(String uri) {
		return regions.get(uri);
	}
	
	public void addDocumentListener(IRegionDocumentListener listener) {
		listeners.add(listener);
	}

	public void removeDocumentListener(IRegionDocumentListener listener) {
		listeners.remove(listener);
	}

	protected void fireRegionAdded(RegionData newRegion) {
		for (IRegionDocumentListener listener: new ArrayList<IRegionDocumentListener>(listeners))
			listener.regionAdded(newRegion);
	}
	
	protected void firePageChanged(RegionData newPage) {
		for (IRegionDocumentListener listener: new ArrayList<IRegionDocumentListener>(listeners))
			listener.pageChanged(newPage);
	}

	protected void fireModificationStateChanged() {
		for (IRegionDocumentListener listener: new ArrayList<IRegionDocumentListener>(listeners))
			listener.modificationStateChanged();
	}

	protected void fireDocumentNameChanged() {
		for (IRegionDocumentListener listener: new ArrayList<IRegionDocumentListener>(listeners))
			listener.documentNameChanged();
	}

	protected void fireRegionRemoved(RegionData removedRegion) {
		for (IRegionDocumentListener listener: new ArrayList<IRegionDocumentListener>(listeners))
			listener.regionRemoved(removedRegion);	
	}
	
	private void fireRegionModified(RegionData oldRegion, RegionData newRegion) {
		for (IRegionDocumentListener listener: new ArrayList<IRegionDocumentListener>(listeners))
			listener.regionModified(oldRegion, newRegion);	
	}

	public File getFile() {
		return file;
	}
	
	public String getUri() {
		return uri;
	}
	
	public String generateRegionUri() {
		int i = regions.size() - 1;
		String uri = null;
		do {
			i++;
			uri = this.uri + "/" + i;
		} while (regions.containsKey(uri));
		return uri;
	}
}
