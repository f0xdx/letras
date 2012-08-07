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

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.letras.api.region.RegionData;
import org.letras.ps.region.RegionTreeNode;
import org.letras.psi.iregion.IRegion;

public class RegionInspector extends JPanel implements ItemListener {

	private static final long serialVersionUID = 8105746591075674689L;
	private DesignerRegionCanvas canvas;
	private JTextField uriTextField;
	private JTextField channelTextField;
	private JComboBox hungryBox;
	private RegionEditor regionEditor;

	public RegionInspector(DesignerRegionCanvas canvas, RegionEditor regionEditor) {
		this.canvas = canvas;
		this.regionEditor = regionEditor;
		initializeComponents();
		canvas.addItemListener(this);
	}

	private void initializeComponents() {
		setBorder(new TitledBorder(new LineBorder(Color.black), "Region Inspector"));
		setLayout(new GridLayout(3, 3));
		JLabel uriLabel = new JLabel("URI:");
		add(uriLabel);
		uriTextField = new JTextField();
		uriTextField.setEnabled(false);
		add(uriTextField);
		final Runnable updateUri = new Runnable() {
			@Override
			public void run() {
				if (canvas.getSelectedItem() != null) {
					IRegion region = canvas.getSelectedItem().getRegion();
					if (region != null && !region.uri().equals(uriTextField.getText()))
							regionEditor.updateRegionUri((RegionData) region, uriTextField.getText());
				}
			}
		};
		uriTextField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(updateUri);
			}
		});
		uriTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				SwingUtilities.invokeLater(updateUri);
			}
		});
		JLabel channelLabel = new JLabel("Channel:");
		add(channelLabel);
		channelTextField = new JTextField();
		channelTextField.setEnabled(false);
		add(channelTextField);
		final Runnable updateChannel = new Runnable() { 
			@Override
			public void run() {
				if (canvas.getSelectedItem() != null) {
					IRegion region = canvas.getSelectedItem().getRegion();
					if ((region != null) && !region.channel().equals(channelTextField.getText()))
						regionEditor.updateRegionChannel((RegionData) region, channelTextField.getText());
				}
			}
		};
		channelTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(updateChannel);
			}});
		channelTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				SwingUtilities.invokeLater(updateChannel);
			}});

		JLabel hungryLabel = new JLabel("Hungry:");
		add(hungryLabel);
		hungryBox = new JComboBox(new Object[] { false, true });
		hungryBox.setEnabled(false);
		add(hungryBox);
		hungryBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// hack that ensures X11 doesn't crash when an exception is thrown in the following code.
				SwingUtilities.invokeLater(new Runnable() { 
					@Override
					public void run() {
						if (canvas.getSelectedItem() != null) {
							IRegion region = canvas.getSelectedItem().getRegion();
							if (hungryBox.getSelectedItem() != null && !hungryBox.getSelectedItem().equals(region.hungry())) {
								regionEditor.updateRegionHungry((RegionData) region, (Boolean) hungryBox.getSelectedItem());
							}
						}	
					}
				});
			}
		});
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		RegionTreeNode node = canvas.getSelectedItem();
		if (node != null && node.getRegion() != null) {
			uriTextField.setEnabled(true);
			uriTextField.setText(node.getRegion().uri());
			channelTextField.setEnabled(true);
			channelTextField.setText(node.getRegion().channel());
			hungryBox.setEnabled(true);
			hungryBox.setSelectedItem(node.getRegion().hungry());
		} else {
			uriTextField.setEnabled(false);
			uriTextField.setText("");
			channelTextField.setEnabled(false);
			channelTextField.setText("");
			hungryBox.setEnabled(false);
			hungryBox.setSelectedItem(null);
		}
	}

}
