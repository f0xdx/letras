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
package org.letras.tools.regionmonitor.ink;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;

import org.letras.ps.region.RegionTreeNode;
import org.letras.tools.regionmonitor.ink.model.InkRegion;
import org.letras.tools.regionmonitor.ink.model.StrokeSupplier;
import org.letras.tools.regionmonitor.ink.view.DigitalInkPanel;
import org.mundo.rt.Mundo;

public class RegionInkInspector extends JDialog {
	private static final long serialVersionUID = -3070869457176036490L;

	private InkRegion inkRegion;
	
	StrokeSupplier supplier;
	
	private DigitalInkPanel inkPanel;
	
	public RegionInkInspector(RegionTreeNode regionTreeNode) {
		super(new Frame(), "Region Ink Inspector", false);
		this.setSize(500, 500);
		inkRegion = new InkRegion(regionTreeNode.getRegion());
		supplier = new StrokeSupplier(regionTreeNode.getRegion(), inkRegion);
		Mundo.registerService(supplier);
		inkPanel = new DigitalInkPanel(inkRegion, new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB));
		this.getContentPane().add(inkPanel, BorderLayout.CENTER);
	}
	
	@Override
	public void setVisible(boolean b) {
		supplier.connect("lan");
		super.setVisible(b);
	}
}
