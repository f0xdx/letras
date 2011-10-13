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
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.mundo.rt.Mundo;

/**
 * The PenMonitor is a monitoring tool for the RawDataProcessingStage.<br>
 * By instrumenting the RawDataProcessingStage we get an immediate feedback
 * when connecting a new pen, while using it and on disconnecting the pen.<br>
 * It consists of a main window showing all currently available pens
 * with rudimentary status information. By selecting an available pen
 * a secondary window with additional information will be displayed.
 * 
 * This class serves as the main-class for the PenMonitor and is responsible for
 * initiation of the main window as well as the setup of the <code>PenListener</code>.
 * @author niklas
 */
public class PenMonitor extends JPanel {
	private static final long serialVersionUID = 2736701740660722869L;

	/**
	 * window title
	 */
	static final String appName = "PenMonitor for Letras";
	
	/**
	 * table listing all discovered pens
	 */
	private JTable penTable;
	
	/**
	 * delegate (model) for the table
	 */
	private PenTableModel penTableModel;
	
	/**
	 * the dialog in which additional information to the selected pen is displayed
	 */
	private InspectorDialog inspector;
	
	/**
	 * default constructor
	 */
	public PenMonitor() {
		initModel();
		initComponents();
	}
	
	/**
	 * create the pen table model
	 */
	private void initModel() {
		penTableModel = new PenTableModel();
	}
	
	/**
	 * initialize graphical components of the window
	 */
	private void initComponents() {
		setLayout(new BorderLayout());
		
		penTable = new JTable(penTableModel);
	
		penTable.setColumnModel(createColumnModel());
		penTable.setAutoCreateRowSorter(true);
		penTable.setRowHeight(24);
		penTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		penTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		Dimension tableSize = new Dimension();
		tableSize.width = penTable.getColumnModel().getTotalColumnWidth();
		tableSize.height = 10 * penTable.getRowHeight();
		penTable.setPreferredScrollableViewportSize(tableSize);
		
		penTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
					//try to get the selected PenInformation 
					PenInformation penInfo = penTableModel.getPenInformation(e.getLastIndex());
					if (penInfo != null)
						getInspector().setSelectedPen(penInfo);
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(penTable);
		add(scrollPane, BorderLayout.CENTER);
		
	}
	
	/**
	 * create a ColumnTableModel for the pen table
	 * @return ColumnTableModel to be used for the pen table
	 */
	private TableColumnModel createColumnModel() {
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		
		TableCellRenderer cellRenderer = new DefaultTableCellRenderer();
	
		TableColumn column = new TableColumn();
		column.setModelIndex(PenTableModel.PENID_COLUMN);
		column.setHeaderValue("Pen ID");
		column.setPreferredWidth(140);
		column.setCellRenderer(cellRenderer);
		columnModel.addColumn(column);
		
		column = new TableColumn();
		column.setModelIndex(PenTableModel.NODE_COLUMN);
		column.setHeaderValue("Hostnode ID");
		column.setPreferredWidth(230);
		column.setCellRenderer(cellRenderer);
		columnModel.addColumn(column);
		
		column = new TableColumn();
		column.setModelIndex(PenTableModel.STATE_COLUMN);
		column.setHeaderValue("Pen State");
		column.setPreferredWidth(30);
		column.setCellRenderer(cellRenderer);
		columnModel.addColumn(column);
		
		return columnModel;
	}
	
	/**
	 * get the inspector dialog. When no dialog is available it will be created 
	 * inside this method. 
	 * @return inspectorDialog
	 */
	private InspectorDialog getInspector() {
		if (inspector == null)
			inspector = new InspectorDialog();
		inspector.setVisible(true);
		return inspector;
	}
	
	/**
	 * start the penListener which is responsible for the discovery of connected pens
	 */
	private void start() {
		PenListener penListener = new PenListener(penTableModel);
		penListener.setServiceZone("lan");
		Mundo.registerService(penListener);
	}
	
	/**
	 * main method for the PenMonitor app
	 * @param args
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Mundo.init();
				JFrame frame = new JFrame(appName);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				PenMonitor viewer = new PenMonitor();
				frame.add(viewer);
				frame.setSize(500,300);
				frame.setVisible(true);
				viewer.start();
			}
		});
	}
}
