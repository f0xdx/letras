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

import org.letras.ps.region.document.xml.CircularShape;
import org.letras.ps.region.document.xml.RectangularShape;
import org.letras.ps.region.document.xml.Region;
import org.letras.ps.region.document.xml.ShapeChoice;
import org.letras.psi.iregion.RegionData;
import org.letras.psi.iregion.shape.IShape;
import org.letras.util.region.document.RegionDocument;

public class XmlRegionDocumentLoader {
	public static RegionDocument loadRegionDocument(File f) {
		org.letras.ps.region.document.xml.RegionDocument xmlDocument = JAXB.unmarshal(f, org.letras.ps.region.document.xml.RegionDocument.class);
		return toModel(xmlDocument);
	}

	private static RegionDocument toModel(
			org.letras.ps.region.document.xml.RegionDocument xmlDocument) {
		List<RegionData> regions = toModel(xmlDocument.getRegion());
		RegionData page = toModel(xmlDocument.getPage());
		return new RegionDocument(xmlDocument.getUri(), page, regions);
	}

	private static List<RegionData> toModel(List<Region> regions) {
		List<RegionData> result = new ArrayList<RegionData>();
		for (Region region: regions) {
			result.add(toModel(region));
		}
		return result;
	}

	private static RegionData toModel(Region region) {
		return new RegionData(region.getUri(), 
				region.getChannel() != null ? region.getChannel() : region.getUri(), 
				region.isHungry(), convert(region.getShape()));
	}

	private static IShape convert(ShapeChoice shape) {
		if (shape.getShape() instanceof RectangularShape) {
			RectangularShape rectangle = (RectangularShape) shape.getShape();
			return new org.letras.psi.iregion.shape.RectangularShape(rectangle.getLeft(), 
					rectangle.getTop(), rectangle.getWidth(), rectangle.getHeight());
		} else if (shape.getShape() instanceof CircularShape) {
			CircularShape circle = (CircularShape) shape.getShape();
			return new org.letras.psi.iregion.shape.CircularShape(circle.getCenterx(),
					circle.getCentery(), circle.getRadius());
		} else {
			throw new IllegalArgumentException("Invalid shape type "
					+ shape.getShape().getClass().getCanonicalName());
		}
	}

}
