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
package org.letras.tools.regionmonitor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

import org.letras.tools.regionmonitor.local.LocalAppMode;
import org.letras.tools.regionmonitor.remote.SimpleRegionBrokerAppMode;

/**
 * The <code>RegionMonitor</code> is a tool that helps testing and debugging the
 * Region Processing Stage.
 * There are several modes of operation for the RegionMonitor each of which is
 * represented by a subclass of {@link ApplicationMode}
 * To implement a new mode subclass the ApplicationMode (or implement 
 * {@link IApplicationMode}) and register an instance of your new mode with 
 * the RegionMonitor by calling {@link #addApplicationMode(ApplicationMode)}
 * 
 * @author niklas
 */
public class RegionMonitor extends JFrame {

	private static final long serialVersionUID = -6529872961202513538L;
	
	/**
	 * registered ApplicationModes
	 */
	private List<IApplicationMode> appModes;

	/**
	 * The mode in which the application is currently executing in
	 */
	private IApplicationMode currentMode = new IApplicationMode() {
		// This is a default ApplicationMode to be used on startup
		JLabel label =  new JLabel("Select a mode from the menubar");
		
		public JComponent getPanel() {
			return label;
		}
		
		@Override
		public String getNameForMenuBar() {
			return null;
		}

		@Override
		public void activate() {}

		@Override
		public void deactivate() {}

		@Override
		public JMenu getMenuBarItem() {return null;}
	};
	
	/**
	 * Default Constructor
	 */
	public RegionMonitor() {
		appModes = new LinkedList<IApplicationMode>();
		getContentPane().setLayout(new BorderLayout());
		initializeMenuBar();
		getContentPane().add(currentMode.getPanel(), BorderLayout.CENTER);	
	}
	
	/**
	 * Initialize the MenuBar by using the appModes that are currently registered
	 */
	private void initializeMenuBar() {
		final JMenuBar menuBar = new JMenuBar();
		JMenu modeMenu = new JMenu("Mode");
		ButtonGroup group = new ButtonGroup();
		
		for (final IApplicationMode appMode : appModes) {
			JRadioButtonMenuItem localMenuItem = new JRadioButtonMenuItem(appMode.getNameForMenuBar());
			localMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (currentMode != appMode) {
						if (currentMode != null) {
							getContentPane().remove(currentMode.getPanel());
							for (int menuIndex = menuBar.getComponentCount()-1; menuIndex > 0 ; menuIndex--) {
								menuBar.remove(menuIndex);
							}
							currentMode.deactivate();
						}
						appMode.activate();
						if (appMode.getMenuBarItem() != null)
							menuBar.add(appMode.getMenuBarItem());
						getContentPane().add(appMode.getPanel(), BorderLayout.CENTER);
						currentMode = appMode;
						validate();
					}
				}
			});
			group.add(localMenuItem);
			modeMenu.add(localMenuItem);
		}
		menuBar.add(modeMenu);
		setJMenuBar(menuBar);
	}

	/**
	 * add a new {@link ApplicationMode} to the <code>RegionMonitor</code>
	 * @param localAppMode
	 */
	public void addApplicationMode(ApplicationMode localAppMode) {
		appModes.add(localAppMode);
		initializeMenuBar();
	}
	
	public static void main(String[] args) {
		RegionMonitor regionMonitor = new RegionMonitor();
		regionMonitor.addApplicationMode(new LocalAppMode());
		regionMonitor.addApplicationMode(new SimpleRegionBrokerAppMode());
		regionMonitor.setSize(800, 600);
		regionMonitor.setDefaultCloseOperation(EXIT_ON_CLOSE);
		regionMonitor.setVisible(true);
	}
	
}
