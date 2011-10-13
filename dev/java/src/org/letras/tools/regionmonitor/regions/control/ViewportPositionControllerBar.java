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
package org.letras.tools.regionmonitor.regions.control;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class ViewportPositionControllerBar extends JPanel implements MouseMotionListener {

	private static final long serialVersionUID = -8456672709845994613L;
	private JTextField statusBarXLabel;
	private JTextField statusBarYLabel;
	private JPanel changePositionConfirmPanel;
	private JTextField ppiField;
	private AbstractAction gotoAction;
	private AbstractAction cancelAction;
	private IViewportPositionDelegate viewportPositionDelegate;



	public ViewportPositionControllerBar() {
		initLayout();
		createActions();
		initComponents();
		setInputVerifier();
		setupListener();
	}
	
	
	
	private void createActions() {
		gotoAction = new AbstractAction("Go") {	
			private static final long serialVersionUID = -7739831204688295325L;

			@Override
			public void actionPerformed(ActionEvent e) {
				changePositionConfirmPanel.requestFocus();
				changePositionConfirmPanel.setVisible(false);
				final double x = Double.parseDouble(statusBarXLabel.getText());
				final double y = Double.parseDouble(statusBarYLabel.getText());
				final int scale = Integer.parseInt(ppiField.getText());
				viewportPositionDelegate.positionDidChange(x, y, scale);
			}
		};
		
		cancelAction = new AbstractAction("Cancel") {	
			private static final long serialVersionUID = -2952357742953331027L;

			@Override
			public void actionPerformed(ActionEvent e) {
				changePositionConfirmPanel.requestFocus();
				changePositionConfirmPanel.setVisible(false);
				changePositionConfirmPanel.repaint();
			}
		};
	}



	private void initComponents() { 
		
	JPanel statusLabels = new JPanel();
	
	statusLabels.add(new JLabel("x: "));
	statusBarXLabel = new JTextField(10);
	statusLabels.add(statusBarXLabel);
	
	
	statusLabels.add(new JLabel("y: "));
	statusBarYLabel = new JTextField(10);
	statusLabels.add(statusBarYLabel);
	this.add(statusLabels);
	changePositionConfirmPanel = new JPanel();
	
	ppiField = new JTextField(5);
	
	changePositionConfirmPanel.add(new JLabel("anoto coordinates per pixel:"));
	changePositionConfirmPanel.add(ppiField);
	
	
	
	final JButton gotoButton = new JButton(gotoAction);
	changePositionConfirmPanel.add(gotoButton);
	
	
	
	final JButton cancelButton = new JButton(cancelAction);
	changePositionConfirmPanel.add(cancelButton);
	
	
	KeyListener listener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {}
		@Override
		public void keyReleased(KeyEvent e) {
			if (changePositionConfirmPanel.isVisible()) {
				if (e.getKeyCode() == 27) cancelButton.doClick();
				else if (e.getKeyCode() == 10) gotoButton.doClick();
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {}
	};
	
	for (Component comp : statusLabels.getComponents()) {
		comp.addKeyListener(listener);
	}
	
	for (Component comp : changePositionConfirmPanel.getComponents()) {
		comp.addKeyListener(listener);
	}
	
	changePositionConfirmPanel.setVisible(false);
	this.add(changePositionConfirmPanel);
	}



	private void initLayout() {
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.setBorder(new BevelBorder(BevelBorder.RAISED));
	}



	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}



	@Override
	public void mouseMoved(MouseEvent e) {
		Locale.setDefault(Locale.US);
		Point2D.Double position = viewportPositionDelegate.calculateAbsoluteCoordinatesFor(e.getPoint());
		statusBarXLabel.setText(String.format("%15.3f", position.getX()));
		statusBarYLabel.setText(String.format("%15.3f", position.getY()));
	}
	
	
	private void setInputVerifier() {
		InputVerifier verifier = new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				try {
					Double.valueOf(((JTextField) input).getText());
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		};
		
		statusBarXLabel.setInputVerifier(verifier);
		statusBarYLabel.setInputVerifier(verifier);
	}
	
	public void setPositionChangeListener(IViewportPositionDelegate iPositionChangeListener) {
		this.viewportPositionDelegate = iPositionChangeListener;
	}



	private void setupListener() {
		FocusListener enableTextFieldAction = new FocusListener() {
			
			@Override
			public void focusGained(FocusEvent e) {
				JTextField textfield = (JTextField) e.getSource();
				textfield.selectAll();
				ppiField.setText(String.valueOf(viewportPositionDelegate.getCurrentAnotoCoordinatesPerPixel()));
				changePositionConfirmPanel.setVisible(true);
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				
			}
		};
		statusBarXLabel.addFocusListener(enableTextFieldAction);
		statusBarYLabel.addFocusListener(enableTextFieldAction);
	}
}
