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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.letras.api.region.RegionData;
import org.letras.ps.region.RegionTreeNode;
import org.letras.psi.iregion.IRegion;
import org.letras.util.region.document.IRegionDocumentListener;
import org.letras.util.region.document.RegionDocument;

public class RegionEditor extends RegionDocumentEditor implements IRegionDocumentListener {

	private static final long serialVersionUID = 7344636426806074623L;
	private RegionCreationConsumer consumer;
	private RegionDocument document;
	
	private RegionTreeNode pageNode;
	private Map<IRegion, RegionTreeNode> regionNodes = new HashMap<IRegion, RegionTreeNode>();
	
	private DesignerRegionCanvas canvas;
	
	public RegionEditor() {
		initializeComponents();
	}
	
	@Override
	public void setDocument(RegionDocument document) {
		if (this.document != document) {
			if (this.document != null) {
				this.document.removeDocumentListener(this);
				consumer.shutdown();
			}
			this.document = document;
			if (document != null) {
				consumer = new RegionCreationConsumer(document);
				document.addDocumentListener(this);
				pageNode = new RegionTreeNode(document.getPage());
				for (RegionData region: document.getRegions()) {
					if (region != pageNode.getRegion()) {
						RegionTreeNode node = new RegionTreeNode(region);
						regionNodes.put(region, node);
						pageNode.add(node);
					}
				}
				canvas.setToplevelRegion(pageNode);
				setVisible(true);
			}
		}
	}
	
	private void initializeComponents() {
		setLayout(new BorderLayout());
		canvas = new DesignerRegionCanvas();
		canvas.setVirtualRegionsVisible(false);
		canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "delete");
		canvas.getActionMap().put("delete", new AbstractAction("Delete Region") {

			private static final long serialVersionUID = 8451472377738588324L;

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedRegion();
			}
		});
		add(canvas, BorderLayout.CENTER);
		add(new RegionInspector(canvas, this), BorderLayout.SOUTH);
	}
	
	protected void deleteSelectedRegion() {
		if (canvas.getSelectedItem() != null) {
			document.removeRegion((RegionData) canvas.getSelectedItem().getRegion());
			canvas.setSelectedItem(null);
		}
	}

	@Override
	public void regionAdded(RegionData region) {
		setStatusMessage("Ready", Color.black);
		RegionTreeNode node = new RegionTreeNode(region);
		regionNodes.put(region, node);
		if (pageNode.getRegion().shape().getBounds().contains(region.shape().getBounds()))
			pageNode.add(node);
		canvas.repaint();
	}

	@Override
	public void regionRemoved(RegionData region) {
		RegionTreeNode oldNode = regionNodes.remove(region);
		if (pageNode.getRegion() != region || oldNode != null) {
			pageNode.remove(oldNode);
			canvas.repaint();
		}
	}
	
	@Override
	public void regionModified(RegionData oldRegion, RegionData newRegion) {
		System.out.println("Modified to: " + newRegion.hashCode());
		setStatusMessage("Ready", Color.black);
		regionNodes.remove(oldRegion);
		RegionTreeNode node = new RegionTreeNode(newRegion);
		regionNodes.put(newRegion, node);
		if (pageNode.getRegion() != oldRegion) {
			pageNode.add(node);
			canvas.repaint();
		}
	}
	
	@Override
	public void pageChanged(RegionData newPage) {
		System.out.println("Page changed: " + newPage.hashCode());
		RegionTreeNode leftChild = pageNode.getLeftChild();
		RegionTreeNode rightChild = pageNode.getRightChild();
		pageNode = regionNodes.get(newPage);
		if (leftChild != null && pageNode != leftChild)
			pageNode.add(leftChild);
		if (rightChild != null && pageNode != rightChild)
			pageNode.add(rightChild);
		if (pageNode == null)
			throw new IllegalStateException("Page Changed to was not previously added!");
		canvas.setToplevelRegion(pageNode);
	}

	public void updateRegionChannel(RegionData regionToUpdate, String newChannel) {
		RegionTreeNode oldNode = regionNodes.get(regionToUpdate); 
		RegionData newRegion = document.updateRegionChannel(regionToUpdate, newChannel);
		if (oldNode == pageNode) {
			pageNode = regionNodes.get(newRegion);
			canvas.setToplevelRegion(pageNode);
		}
		if (oldNode == canvas.getSelectedItem())
			canvas.setSelectedItem(regionNodes.get(newRegion));
	}

	public void updateRegionHungry(RegionData regionToUpdate, boolean newHungry) {
		RegionTreeNode oldNode = regionNodes.get(regionToUpdate); 
		IRegion newRegion = document.updateRegionHungry(regionToUpdate, newHungry);
		if (oldNode == pageNode) {
			pageNode = regionNodes.get(newRegion);
			canvas.setToplevelRegion(pageNode);
		}
		if (oldNode == canvas.getSelectedItem())
			canvas.setSelectedItem(regionNodes.get(newRegion));
	}

	public void updateRegionUri(RegionData regionToUpdate, String uri) {
		RegionTreeNode oldNode = regionNodes.get(regionToUpdate);
		document.removeRegion(regionToUpdate);
		RegionData newRegion = new RegionData(uri, regionToUpdate.channel(), regionToUpdate.hungry(), regionToUpdate.shape());
		document.addRegion(newRegion);
		if (oldNode == pageNode) {
			pageNode = regionNodes.get(newRegion);
			canvas.setToplevelRegion(pageNode);
		}
		if (oldNode == canvas.getSelectedItem())
			canvas.setSelectedItem(regionNodes.get(newRegion));
	}

	@Override
	public void documentNameChanged() {
	}

	@Override
	public void modificationStateChanged() {
	}

	public RegionDocument getDocument() {
		return document;
	}


}
