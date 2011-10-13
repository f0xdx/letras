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

import java.awt.BorderLayout;

import org.letras.ps.region.RegionTreeNode;
import org.letras.psi.iregion.IRegion;
import org.letras.util.region.document.RegionDocument;
import org.letras.util.region.document.RegionDocumentReceiver;

public class InkViewEditor extends RegionDocumentEditor {

	private static final long serialVersionUID = -6742798384556452720L;
	private InkRegionCanvas canvas;
	private RegionDocument document;
	private RegionTreeNode pageNode;
	private RegionDocumentReceiver receiver;

	public InkViewEditor() {
		setLayout(new BorderLayout());
		canvas = new InkRegionCanvas();
		add(canvas, BorderLayout.CENTER);
	}
	
	@Override
	public void setDocument(RegionDocument document) {
		if (this.document != document) {
			if (this.document != null) {
				receiver.setActiveConsumer(null);
				receiver.shutdown();
			}
			this.document = document;
			if (document != null) {
				receiver = new RegionDocumentReceiver(document);
				receiver.setActiveConsumer(canvas);
				pageNode = new RegionTreeNode(document.getPage());
				for (IRegion region: document.getRegions()) {
					final RegionTreeNode node = new RegionTreeNode(region);
					pageNode.add(node);
				}
				canvas.setToplevelRegion(pageNode);
				setVisible(true);
			}
		}
	}

}
