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
package org.letras.tools.penmonitor;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 * The InspectorDialog is a secondary window (dialog) of the PenMonitor.
 * It shows additional information to the pen which is currently selected in
 * the overview table.
 * 
 * @author niklas
 *
 */
public class InspectorDialog extends JDialog implements Observer {

	private static final long serialVersionUID = 214550887301688967L;

	/**
	 * The Pen which is currently serving as the data source for the inspector 
	 */
	private PenInformation penInformation;
	
	//labels which have to be updated
	private JLabel penIdLabel;
	private JLabel xValueLabel;
	private JLabel yValueLabel;
	private JLabel forceValueLabel;
	private JLabel delayLabel;
	
	/**
	 * Default Constructor<br>
	 * don't forget to call <code>setVisible(true)</code> later
	 */
	public InspectorDialog() {
		super(new JFrame(), "Pen Inspector", false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		initComponents();
		reset();
		setSize(200, 130);
		setLocation(500, 50);
	}
	
	/**
	 * set the currently selected pen. 
	 * @param pen to use as data source
	 */
	public void setSelectedPen (PenInformation pen) {
		if (this.penInformation != pen) {
			if (this.penInformation != null) {
				this.penInformation.deleteObserver(this);
			}
			pen.addObserver(this);
			this.penInformation = pen;
			reset();
			penIdLabel.setText(pen.getPenID());
		}
	}
	
	/**
	 * reset all labels to a default state
	 */
	private void reset() {
		penIdLabel.setText("No pen selected.");
		xValueLabel.setText("-");
		yValueLabel.setText("-");
		forceValueLabel.setText("-");
		delayLabel.setText("-");
	}
	
	/**
	 * create and add all components in the dialog. <br>
	 * this method should be called only once on initialization
	 */
	private void initComponents() {
		
		setLayout(new BorderLayout());

		//build the dataPanel
		JLabel penIdText = new JLabel("Pen ID: ");
		penIdLabel = new JLabel();
		
		JLabel xValueText = new JLabel("x: ");
		xValueLabel = new JLabel();
		
		JLabel yValueText = new JLabel("y: ");
		yValueLabel = new JLabel();
		
		JLabel forceValueText = new JLabel("force: ");
		forceValueLabel = new JLabel();
		
		JLabel delayText = new JLabel("delay (approx.): ");
		delayLabel = new JLabel();
		
		SpringLayout layout = new SpringLayout();
		JPanel centerPanel = new JPanel(layout);
		
		//add all labels
		
		centerPanel.add(penIdText);
		centerPanel.add(penIdLabel);
		centerPanel.add(xValueText);
		centerPanel.add(xValueLabel);
		centerPanel.add(yValueText);
		centerPanel.add(yValueLabel);
		centerPanel.add(forceValueText);
		centerPanel.add(forceValueLabel);
		centerPanel.add(delayText);
		centerPanel.add(delayLabel);
		//constrain the layout
		
		layout.putConstraint(SpringLayout.WEST, penIdText, 5, SpringLayout.WEST, centerPanel);
		layout.putConstraint(SpringLayout.NORTH, penIdText, 5, SpringLayout.NORTH, centerPanel);
		
		layout.putConstraint(SpringLayout.WEST, xValueText, 5, SpringLayout.WEST, centerPanel);
		layout.putConstraint(SpringLayout.NORTH, xValueText, 5, SpringLayout.SOUTH, penIdText);
		
		layout.putConstraint(SpringLayout.WEST, yValueText, 5, SpringLayout.WEST, centerPanel);
		layout.putConstraint(SpringLayout.NORTH, yValueText, 5, SpringLayout.SOUTH, xValueText);
		
		layout.putConstraint(SpringLayout.WEST, forceValueText, 5, SpringLayout.WEST, centerPanel);
		layout.putConstraint(SpringLayout.NORTH, forceValueText, 5, SpringLayout.SOUTH, yValueText);
		
		layout.putConstraint(SpringLayout.WEST, delayText, 5, SpringLayout.WEST, centerPanel);
		layout.putConstraint(SpringLayout.NORTH, delayText, 5, SpringLayout.SOUTH, forceValueText);
		
		layout.putConstraint(SpringLayout.WEST, penIdLabel, 5, SpringLayout.EAST, penIdText);
		layout.putConstraint(SpringLayout.NORTH, penIdLabel, 5, SpringLayout.NORTH, centerPanel);
		
		layout.putConstraint(SpringLayout.WEST, xValueLabel, 5, SpringLayout.EAST, xValueText);
		layout.putConstraint(SpringLayout.NORTH, xValueLabel, 5, SpringLayout.SOUTH, penIdLabel);
		
		layout.putConstraint(SpringLayout.WEST, yValueLabel, 5, SpringLayout.EAST, yValueText);
		layout.putConstraint(SpringLayout.NORTH, yValueLabel, 5, SpringLayout.SOUTH, xValueLabel);
		
		layout.putConstraint(SpringLayout.WEST, forceValueLabel, 5, SpringLayout.EAST, forceValueText);
		layout.putConstraint(SpringLayout.NORTH, forceValueLabel, 5, SpringLayout.SOUTH, yValueLabel);
		
		layout.putConstraint(SpringLayout.WEST, delayLabel, 5, SpringLayout.EAST, delayText);
		layout.putConstraint(SpringLayout.NORTH, delayLabel, 5, SpringLayout.SOUTH, forceValueLabel);
		
		JPanel dataPanel = new JPanel(new BorderLayout());
		
		dataPanel.add(centerPanel, BorderLayout.CENTER);
		
		add(dataPanel,BorderLayout.CENTER);
	}

	/**
	 * Method from interface {@link java.util.Observer}<br>
	 * code that updates the dialog should be in this method
	 */
	@Override
	public void update(Observable o, Object arg) {
		xValueLabel.setText(String.format("%f", penInformation.getCurrentXPosition()));
		yValueLabel.setText(String.format("%f", penInformation.getCurrentYPosition()));
		forceValueLabel.setText(String.format("%d", penInformation.getCurrentForce()));
		delayLabel.setText(String.format("%dms", penInformation.getCurrentDelay()));
	}
	
}
