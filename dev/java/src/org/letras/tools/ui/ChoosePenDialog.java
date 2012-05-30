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
 * The Original Code is Letras (Java).
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
package org.letras.tools.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.mundo.rt.Mundo;
import org.mundo.service.ServiceInfo;

public class ChoosePenDialog extends JDialog implements IPenDiscoveryListener {
	
	private static final long serialVersionUID = -7015284185856138931L;
	private PenDiscoveryService penDiscoveryService;
	private JComboBox penChooser;
	private JButton okButton;
	private ServiceInfo result = null;
	private JButton cancelButton;
	protected class ServiceInfoWrapper {
		public ServiceInfo serviceInfo;
		
		public ServiceInfoWrapper(ServiceInfo serviceInfo) {
			this.serviceInfo = serviceInfo;
		}
		
		@Override
		public String toString() {
			return serviceInfo.instanceName;
		}
	}

	public ChoosePenDialog(JFrame owner) {
		super(owner);
		initComponents();
		penDiscoveryService = new PenDiscoveryService();
		penDiscoveryService.setServiceZone("lan");
		penDiscoveryService.addPenDiscoveryListener(this);
		Mundo.registerService(penDiscoveryService);
		hookListeners();
	}

	private void hookListeners() {
		penChooser.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				result = ((ServiceInfoWrapper) penChooser.getSelectedItem()).serviceInfo;
				okButton.setEnabled(result != null);
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = ((ServiceInfoWrapper) penChooser.getSelectedItem()).serviceInfo;
				Mundo.unregisterService(penDiscoveryService);
				setVisible(false);
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = null;
				Mundo.unregisterService(penDiscoveryService);
				setVisible(false);
			}
		});
	}
	private void initComponents() {
		setSize(280, 120);
		setTitle("Select a Pen");
		setLocationRelativeTo(getOwner());
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().setLayout(new GridBagLayout());
		
		penChooser = new JComboBox();
		GridBagConstraints penChooserConstraints = new GridBagConstraints();
		penChooserConstraints.gridx = 0;
		penChooserConstraints.gridy = 0;
		penChooserConstraints.gridheight = 1;
		penChooserConstraints.gridwidth = 2;
		penChooserConstraints.weightx = 2;
		penChooserConstraints.insets = new Insets(5, 5, 5, 5);
		penChooserConstraints.fill = GridBagConstraints.HORIZONTAL;
		getContentPane().add(penChooser, penChooserConstraints);
		
		okButton = new JButton("Ok");
		GridBagConstraints okButtonConstraints = new GridBagConstraints();
		okButtonConstraints.gridx = 0;
		okButtonConstraints.gridy = 1;
		okButtonConstraints.gridheight = 1;
		okButtonConstraints.gridwidth = 1;
		okButtonConstraints.anchor = GridBagConstraints.CENTER;
		okButtonConstraints.insets = new Insets(5, 5, 5, 5);
		okButton.setEnabled(false);
		getContentPane().add(okButton, okButtonConstraints);
		
		cancelButton = new JButton("Cancel");
		GridBagConstraints cancelButtonConstraints = new GridBagConstraints();
		cancelButtonConstraints.gridx = 1;
		cancelButtonConstraints.gridy = 1;
		cancelButtonConstraints.gridheight = 1;
		cancelButtonConstraints.gridwidth = 1;
		cancelButtonConstraints.anchor = GridBagConstraints.CENTER;
		cancelButtonConstraints.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(cancelButton, cancelButtonConstraints);
	}

	@Override
	public void availablePensChanged(List<ServiceInfo> penServiceInfos) {
		List<ServiceInfoWrapper> wrappers = new ArrayList<ServiceInfoWrapper>();
		for (ServiceInfo info: penServiceInfos)
			wrappers.add(new ServiceInfoWrapper(info));
		penChooser.setModel(new DefaultComboBoxModel(wrappers.toArray()));
		okButton.setEnabled(penChooser.getSelectedItem() != null);
	}
	
	public ServiceInfo getResult() {
		return result;
	}

}
