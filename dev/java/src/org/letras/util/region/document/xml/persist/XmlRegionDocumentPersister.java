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
package org.letras.util.region.document.xml.persist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;

import org.letras.ps.region.document.xml.Region;
import org.letras.ps.region.document.xml.ShapeChoice;
import org.letras.psi.iregion.RegionData;
import org.letras.psi.iregion.shape.CircularShape;
import org.letras.psi.iregion.shape.IShape;
import org.letras.psi.iregion.shape.RectangularShape;
import org.letras.util.region.document.RegionDocument;

public class XmlRegionDocumentPersister {
	public static void persistRegionDocument(RegionDocument doc, File file) {
		JAXB.marshal(convertToData(doc), file);
	}
	
	private static org.letras.ps.region.document.xml.RegionDocument convertToData(RegionDocument document) {
		org.letras.ps.region.document.xml.RegionDocument result = new org.letras.ps.region.document.xml.RegionDocument();
		List<RegionData> nonPageRegions = new ArrayList<RegionData>(document.getRegions());
		nonPageRegions.remove(document.getPage());
		result.getRegion().addAll(convertToData(nonPageRegions));
		result.setPage(convertToData(document.getPage()));
			result.setUri(document.getUri());
		return result;
	}

	private static List<Region> convertToData(List<RegionData> regions) {
		List<Region> result = new ArrayList<Region>(regions.size());
		for (RegionData region: regions)
			result.add(convertToData(region));
		return result;
	}

	private static Region convertToData(RegionData region) {
		Region result = new Region();
		result.setUri(region.uri());
		result.setShape(convertToData(region.shape()));
		result.setChannel(region.channel());
		result.setHungry(region.hungry());
		return result;
	}

	private static ShapeChoice convertToData(IShape shape) {
		ShapeChoice result = new ShapeChoice();
		if (shape instanceof RectangularShape)
			result.setShape(convertToData((RectangularShape) shape));
		else if (shape instanceof CircularShape)
			result.setShape(convertToData((CircularShape) shape));
		else
			throw new IllegalArgumentException("Unknown shape specified!");
		return result;
	}
	
	private static org.letras.ps.region.document.xml.RectangularShape convertToData(RectangularShape rectangle) {
		org.letras.ps.region.document.xml.RectangularShape result = new org.letras.ps.region.document.xml.RectangularShape();
		result.setLeft(rectangle.getX());
		result.setTop(rectangle.getY());
		result.setWidth(rectangle.getWidth());
		result.setHeight(rectangle.getHeight());
		return result;
	}
	
	private static org.letras.ps.region.document.xml.CircularShape convertToData(CircularShape circle) {
		org.letras.ps.region.document.xml.CircularShape result = new org.letras.ps.region.document.xml.CircularShape();
		result.setCenterx(circle.getX());
		result.setCentery(circle.getY());
		result.setRadius(circle.getRadius());
		return result;
	}
}
