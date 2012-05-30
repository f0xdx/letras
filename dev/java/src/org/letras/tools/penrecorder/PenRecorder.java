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
package org.letras.tools.penrecorder;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.letras.tools.ui.ChoosePenDialog;
import org.mundo.rt.Mundo;
import org.mundo.service.ServiceInfo;

public class PenRecorder extends JFrame implements IPositionListener {
	private static final long serialVersionUID = -8615580745861330393L;
	
	private FileFilter penFileFilter = new FileFilter() {
		
		@Override
		public String getDescription() {
			return "Pen Recordings (*.pen)";
		}
		
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".pen");
		}
	};
	
	private enum State {
		Stopped, Playing, Recording
	}
	
	private PenRecordingSession penRecordingSession;
	private PenRecordingPlayer player;
	
	private PenRecording recording;
	private File recordingFile;
	private boolean modified;
	
	private State currentState;

	private JToggleButton recordButton;
	private JButton stopButton;
	private JToggleButton playButton;

	private JSlider transportBar;

	private JLabel currentPosition;

	private JLabel recordingLength;

	public PenRecorder() {
		initializeComponents();
	}
	
	private void initializeComponents() {
		setSize(400,200);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (closeCurrentRecording()) {
					setVisible(false);
					Mundo.unregisterService(player);
					Mundo.shutdown();
					System.exit(0);
				}
			}
		});
		JMenuBar menuBar = new JMenuBar();
		initializeMenuBar(menuBar);
		setJMenuBar(menuBar);
		JToolBar toolBar = new JToolBar();
		add(toolBar, BorderLayout.NORTH);
		initializeToolbar(toolBar);
		JPanel transportPanel = new JPanel();
		initializeTransportPanel(transportPanel);
		add(transportPanel, BorderLayout.CENTER);
		player = new PenRecordingPlayer();
		player.setServiceZone("lan");
		Mundo.registerService(player);
		setVisible(true);
		setState(State.Stopped);
	}

	private void initializeTransportPanel(JPanel transportPanel) {
		transportPanel.setLayout(new GridBagLayout());
		currentPosition = new JLabel("0:00");
		GridBagConstraints currentPositionConstraints = new GridBagConstraints();
		currentPositionConstraints.gridx = 0;
		currentPositionConstraints.gridy = 0;
		currentPositionConstraints.gridwidth = 1;
		currentPositionConstraints.gridheight = 1;
		transportPanel.add(currentPosition, currentPositionConstraints);
		
		transportBar = new JSlider(JSlider.HORIZONTAL);
		GridBagConstraints transportBarConstraints = new GridBagConstraints();
		transportBarConstraints.gridx = 1;
		transportBarConstraints.gridy = 0;
		transportBarConstraints.gridwidth = 1;
		transportBarConstraints.gridheight = 1;
		transportBarConstraints.weightx = 2.0;
		transportBarConstraints.weighty = 1.0;
		transportBarConstraints.fill = GridBagConstraints.BOTH;
		transportBar.setEnabled(false);
		transportBar.setMinimum(0);
		transportBar.setMaximum(1);
		transportBar.setValue(0);
		transportPanel.add(transportBar, transportBarConstraints);
		
		recordingLength = new JLabel("0:00");
		GridBagConstraints recordingLengthConstraints = new GridBagConstraints();
		recordingLengthConstraints.gridx = 2;
		recordingLengthConstraints.gridy = 0;
		recordingLengthConstraints.gridwidth = 1;
		recordingLengthConstraints.gridheight = 1;
		recordingLengthConstraints.weightx = 2.0;
		recordingLengthConstraints.weighty = 1.0;
		recordingLengthConstraints.fill = GridBagConstraints.BOTH;
		transportPanel.add(recordingLength, recordingLengthConstraints);
	}
	
	@Override
	public void positionChanged(long position, long length) {
		int intPosition = (int) (position / 1000);
		int intLength = (int) (length / 1000);
		if (transportBar.getMaximum() != intLength) {
			transportBar.setMaximum(intLength);
		}
		transportBar.setValue(intPosition);
		currentPosition.setText(String.format("%d:%02d", intPosition / 60, intPosition % 60));
		recordingLength.setText(String.format("%d:%02d", intLength / 60, intLength % 60));
		if (position == 0) {
			setState(State.Stopped);
		}
	}
	
	@SuppressWarnings("serial")
	private void initializeMenuBar(JMenuBar menuBar) {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		AbstractAction newRecordingAction = new AbstractAction("New") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				newRecording();
			}
		};
		fileMenu.add(newRecordingAction).setMnemonic(KeyEvent.VK_N);
		
		AbstractAction openRecordingAction = new AbstractAction("Open...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				openRecording();
			}
		};
		fileMenu.add(openRecordingAction).setMnemonic(KeyEvent.VK_O);
		
		AbstractAction saveRecordingAction = new AbstractAction("Save As...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveRecording();
			}
		};
		fileMenu.add(saveRecordingAction).setMnemonic(KeyEvent.VK_A);
		menuBar.add(fileMenu);
	}

	@SuppressWarnings("serial")
	private void initializeToolbar(JToolBar toolBar) {
		Action recordAction = new AbstractAction("Record", new ImageIcon("images/record.png")) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (recordButton.isSelected())
					startRecording();
				
			}
		};
		recordButton = new JToggleButton(recordAction);
		recordButton.setHideActionText(true);
		toolBar.add(recordButton);
		
		Action stopAction = new AbstractAction("Stop", new ImageIcon("images/stop.png")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		};
		stopButton = toolBar.add(stopAction);
		
		Action playAction = new AbstractAction("Play", new ImageIcon("images/play.png")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (playButton.isSelected())
					playRecording();
			}
		};
		playButton = new JToggleButton(playAction);
		playButton.setHideActionText(true);
		toolBar.add(playButton);
		
		JLabel speedLabel = new JLabel("speed: ");
		toolBar.add(speedLabel);
		
		final JSpinner speedSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.25, 5.0, 0.5));
		toolBar.add(speedSpinner);
		speedSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				player.setSpeed((Double) speedSpinner.getValue());
			}
		});
	}

	private boolean closeCurrentRecording() {
		if (currentState != State.Stopped)
			stop();
		if (recording == null && !modified)
			return true;
		int result = JOptionPane.showConfirmDialog(this, "The currently open recording has unsaved changes. Do you want to save your changes?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
		if (result == JOptionPane.CANCEL_OPTION) {
			return false;
		} else if (result == JOptionPane.YES_OPTION) {
			saveRecording();
		}
		return true;
	}

	protected void saveRecording() {
		if (recording != null) {
			JFileChooser chooser = (recordingFile != null) ? new JFileChooser(recordingFile.getParent()) : new JFileChooser(System.getProperty("user.dir"));
			chooser.setFileFilter(penFileFilter);
			JCheckBox autoExtensionCheckbox = new JCheckBox("Automatically add extension");
			autoExtensionCheckbox.setSelected(true);
			chooser.setAccessory(autoExtensionCheckbox);
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				recordingFile = chooser.getSelectedFile();
				if (autoExtensionCheckbox.isSelected() && !recordingFile.getName().endsWith(".pen"))
					recordingFile = new File(recordingFile.getAbsolutePath() + ".pen");
				recording.save(recordingFile);
				modified = false;
				updateTitle();
			}
		}
	}

	protected void openRecording() {
		if (closeCurrentRecording()) {
			JFileChooser chooser = (recordingFile != null) ? new JFileChooser(recordingFile.getParent()) : new JFileChooser(System.getProperty("user.dir"));
			chooser.setFileFilter(penFileFilter);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				recording = new PenRecording(chooser.getSelectedFile());
				recordingFile = chooser.getSelectedFile();
				recording.addPositionListener(this);
				modified = false;
				setState(State.Stopped);
				positionChanged(0, recording.getLength());
			}
		}
	}

	protected void newRecording() {
		if (closeCurrentRecording()) {
			recording.removePositionListener(this);
			recording = null;
			recordingFile = null;
			modified = false;
			setState(State.Stopped);
		}
	}

	protected void playRecording() {
		setState(State.Playing);
		player.play(recording);
		
	}

	protected void stop() {
		if (currentState == State.Recording) {
			penRecordingSession.stop();
			setState(State.Stopped);
			Mundo.unregisterService(penRecordingSession);
			recording = penRecordingSession.getRecording();
		} else if (currentState == State.Playing) {
			player.stop();
			setState(State.Stopped);
		}
	}

	protected void startRecording() {
		ChoosePenDialog choosePenDialog = new ChoosePenDialog(this);
		choosePenDialog.setVisible(true);
		ServiceInfo serviceInfo = choosePenDialog.getResult();
		if (serviceInfo != null) {
			penRecordingSession = new PenRecordingSession(serviceInfo);
			Mundo.registerService(penRecordingSession);
			penRecordingSession.record();
			recording = penRecordingSession.getRecording();
			recording.addPositionListener(this);
			modified = true;
			setState(State.Recording);
		}
	}
	
	private void setState(State state) {
		currentState = state;
		switch (currentState) {
		case Playing:
			playButton.setSelected(true);
			playButton.setEnabled(false);
			stopButton.setSelected(false);
			stopButton.setEnabled(true);
			recordButton.setSelected(false);
			recordButton.setEnabled(false);
			break;
			
		case Recording:
			playButton.setSelected(false);
			playButton.setEnabled(false);
			stopButton.setSelected(false);
			stopButton.setEnabled(true);
			recordButton.setSelected(true);
			recordButton.setEnabled(false);
			break;
			
		case Stopped:
			playButton.setSelected(false);
			playButton.setEnabled(recording != null);
			stopButton.setSelected(false);
			stopButton.setEnabled(false);
			recordButton.setSelected(false);
			recordButton.setEnabled(recording == null);
			break;
		}
		updateTitle();
	}
	
	private void updateTitle() {
		StringBuilder builder = new StringBuilder();
		if (currentState == State.Recording)
			builder.append("[Recording] ");
		else if (currentState == State.Playing)
			builder.append("[Playing] ");
		if (recording != null) {
			if (modified)
				builder.append("*");
			if (recordingFile != null)
				builder.append(recordingFile.getName());
			else 
				builder.append("New Recording");
			builder.append(" - ");
		}
		builder.append("Letras Pen Recorder");
		setTitle(builder.toString());
	}

	public static void main(String[] args) {
		Mundo.init();
		new PenRecorder();
	}

}
