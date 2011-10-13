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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import org.letras.psi.iregion.RegionData;
import org.letras.tools.designer.pagecalibration.IPageCalibrationListener;
import org.letras.tools.designer.pagecalibration.PageCalibrationService;
import org.letras.tools.ui.ChoosePenDialog;
import org.letras.util.region.document.IRegionDocumentListener;
import org.letras.util.region.document.RegionDocument;
import org.letras.util.region.document.RegionDocumentPublisher;
import org.mundo.rt.Logger;
import org.mundo.rt.Mundo;
import org.mundo.service.ServiceInfo;

public class RegionDesigner extends JFrame implements IPageCalibrationListener, IRegionDocumentListener {
	private static final long serialVersionUID = 165247229825018230L;
	private static Logger log = Logger.getLogger(RegionDesigner.class);
	private RegionDocument document;
	private RegionDocumentPublisher publisher;

	private JLabel messageLabel;
	private ServiceInfo penServiceInfo;

	private JPanel statusPanel;
	private JLabel statusLabel;
	
	private RegionDocumentEditor activeEditor;
	
	private String previousDirectory;
	
	private FileFilter regionFileFilter = new FileFilter() {
		@Override
		public String getDescription() {
			return "Region Definitions (*.regions)";
		}
		
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".regions");
		}
	};
	private PageCalibrationService pageCalibrationService;
	
	public RegionDesigner() {
		setSize(800, 600);
		updateTitle();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (closeCurrentDocument()) {
					RegionDesigner.this.setVisible(false);
					Mundo.shutdown();
					System.exit(0);
				}
			}
		});
		initComponents();
		setVisible(true);
	}

	protected boolean closeCurrentDocument() {
		if (pageCalibrationService != null) {
			messageLabel.setText("");
			pageCalibrationService.close();
			pageCalibrationService = null;
		} else if (document != null) {
			if (document.isModified()) {
				int result = JOptionPane.showConfirmDialog(this, "The currently open document has unsaved changes. Do you want to save your changes?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
				if (result == JOptionPane.CANCEL_OPTION) {
					return false;
				} else if (result == JOptionPane.YES_OPTION) {
					saveDocument();
				}
			}
			document.removeDocumentListener(this);
			document.close();
			if (activeEditor != null) {
				activeEditor.setDocument(null);
				remove(activeEditor);
				add(messageLabel, BorderLayout.CENTER);
				messageLabel.setText("");
				activeEditor = null;
			}
			publisher = null;
			updateTitle();
			setStatusMessage("No document loaded", Color.black);
			setVisible(true);

			messageLabel.setText("");
			add(messageLabel, BorderLayout.CENTER);
		}
		return true;
	}

	private void newDocument() {
		ChoosePenDialog dialog = new ChoosePenDialog(this);
		dialog.setVisible(true);
		penServiceInfo = dialog.getResult();
		if (penServiceInfo != null) {
			statusLabel.setText("Connected to " + penServiceInfo + ".");
			messageLabel.setText("Tip the upper left corner of your page.");
			pageCalibrationService = new PageCalibrationService(penServiceInfo, this);
		}
	}

	@Override
	public void documentCalibrated(double left, double top, double width,
			double height) {
		log.info("page calibration successful: (" + left + "," + top + "," + width + "," + height + ")");
		remove(messageLabel);
		String baseUri = JOptionPane.showInputDialog(this, "Enter a base URI for the new Document.", "http://www.letras.org/regions/" );
		document = new RegionDocument(baseUri, left, top, width, height);
		publisher = new RegionDocumentPublisher(document);
		document.addDocumentListener(this);
		updateTitle();
		pageCalibrationService = null;
		showRegionEditor();
	}

	private void initComponents() {
		JMenuBar menuBar = new JMenuBar();
		initMenuBarActions(menuBar);
		setJMenuBar(menuBar);
		
		messageLabel = new JLabel();
		messageLabel.setForeground(Color.darkGray);
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		add(messageLabel, BorderLayout.CENTER);

		statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setLayout(new BorderLayout());
		add(statusPanel, BorderLayout.SOUTH);

		statusLabel = new JLabel("No document loaded");
		statusLabel.setFont(statusLabel.getFont().deriveFont(0));
		statusLabel.setHorizontalAlignment(JLabel.RIGHT);
		statusPanel.add(statusLabel, BorderLayout.CENTER);
	}
	
	@SuppressWarnings("serial")
	private void initMenuBarActions(JMenuBar menuBar) {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		AbstractAction newDocumentAction = new AbstractAction("New..") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (closeCurrentDocument())
					newDocument();
			}
		};
		fileMenu.add(newDocumentAction).setMnemonic(KeyEvent.VK_N);
		
		AbstractAction openDocumentAction = new AbstractAction("Open..") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (closeCurrentDocument())
					openDocument();
			}
		};
		fileMenu.add(openDocumentAction).setMnemonic(KeyEvent.VK_O);
		
		AbstractAction saveDocumentAction = new AbstractAction("Save") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDocument();
			}
		};
		fileMenu.add(saveDocumentAction).setMnemonic(KeyEvent.VK_S);
		
		AbstractAction saveDocumentAsAction = new AbstractAction("Save As..") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDocumentAs();
			}
		};
		fileMenu.add(saveDocumentAsAction).setMnemonic(KeyEvent.VK_A);
		menuBar.add(fileMenu);
		
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		
		final JCheckBoxMenuItem viewInkItem = new JCheckBoxMenuItem("View Ink");
		viewInkItem.setMnemonic(KeyEvent.VK_I);
		viewInkItem.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (viewInkItem.isSelected()) {
					showInkView();
				} else {
					showRegionEditor();
				}
			}
		});
		viewMenu.add(viewInkItem);
		menuBar.add(viewMenu);
	}

	private void closeActiveEditor() {
		if (activeEditor != null) {
			activeEditor.setDocument(null);
			activeEditor.setRegionDesigner(null);
			remove(activeEditor);
			activeEditor = null;
		}
	}

	private void showRegionEditor() {
		closeActiveEditor();
		activeEditor = new RegionEditor();
		activeEditor.setRegionDesigner(this);
		activeEditor.setDocument(document);
		add(activeEditor, BorderLayout.CENTER);
		setVisible(true);
	}

	protected void showInkView() {
		closeActiveEditor();
		activeEditor = new InkViewEditor();
		activeEditor.setRegionDesigner(this);
		add(activeEditor, BorderLayout.CENTER);
		setVisible(true);
		activeEditor.setDocument(document);
	}

	protected void saveDocumentAs() {
		if (previousDirectory == null) {
			previousDirectory = System.getProperty("user.dir");
		}
		JFileChooser chooser = new JFileChooser(previousDirectory);
		chooser.setFileFilter(regionFileFilter);
		JCheckBox autoExtensionCheckbox = new JCheckBox("Automatically add extension");
		autoExtensionCheckbox.setSelected(true);
		chooser.setAccessory(autoExtensionCheckbox);
		
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			previousDirectory = f.getParentFile().getAbsolutePath();
			if (autoExtensionCheckbox.isSelected() && !f.getName().endsWith(".regions"))
				f = new File(f.getAbsolutePath() + ".regions");
			try {
				document.saveToFile(f);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Saving the document failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}

	protected void saveDocument() {
		if ( document.getFile() != null) {
			try {
				document.save();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Saving the document failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			saveDocumentAs();
		}
	}

	protected void openDocument() {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		chooser.setFileFilter(regionFileFilter);
		if (chooser.showOpenDialog(this) == JFileChooser.OPEN_DIALOG) {
			try {
				document = RegionDocument.fromFile(chooser.getSelectedFile());
				publisher = new RegionDocumentPublisher(document);
				document.addDocumentListener(this);
				showRegionEditor();
				setStatusMessage("Ready", Color.black);
				updateTitle();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Opening the document failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				publisher = null;
			}
		}
	}

	@Override
	public void firstCornerIdentified() {
		messageLabel.setText("Now tip the bottom right corner of your page.");
	}

	public void setStatusMessage(String message, Color color) {
		statusLabel.setText(message);
		statusLabel.setForeground(color);
	}
	
	@Override
	public void modificationStateChanged() {
		updateTitle();
	}
	
	@Override
	public void documentNameChanged() {
		updateTitle();
	}

	private void updateTitle() {
		StringBuilder titleBuilder = new StringBuilder();
		if (publisher != null) {
			if (document.isModified()) 
				titleBuilder.append('*');
			if (document.getFile() != null)
				titleBuilder.append(document.getFile().getName());
			else
				titleBuilder.append("New Document");
			titleBuilder.append(" - ");
		}
		titleBuilder.append("Letras Region Designer");
		setTitle(titleBuilder.toString());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Mundo.init();
		new RegionDesigner();
	}

	@Override
	public void regionAdded(RegionData region) {
	}

	@Override
	public void regionRemoved(RegionData region) {
	}

	@Override
	public void regionModified(RegionData oldRegion, RegionData newRegion) {
	}

	@Override
	public void pageChanged(RegionData newPage) {
	}

}
