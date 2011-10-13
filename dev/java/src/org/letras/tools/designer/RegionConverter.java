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
package org.letras.tools.designer;

import java.io.File;

import org.letras.util.region.document.RegionDocument;
import org.letras.util.region.document.xml.persist.XmlRegionDocumentPersister;

public class RegionConverter {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		String dir = args[0];
		traverse(new File(dir));
	}

	private static void traverse(File file) {
		if (file.getName().endsWith(".regions"))
			convert(file);
		if (file.isDirectory()) {
			System.out.println("Searching " + file + " for regions files...");
			for (File f: file.listFiles())
				traverse(f);
		}
	}

	private static void convert(File file) {
		try {
			System.out.println("Converting " + file);
			RegionDocument document = RegionDocument.fromFile(file);
			XmlRegionDocumentPersister.persistRegionDocument(document, file);
		} catch (Exception e) {
			System.out.println("Error converting " + file);
		}
	}

}
