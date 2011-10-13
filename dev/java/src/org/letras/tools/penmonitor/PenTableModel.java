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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

/**
 * The PenTableModel manages access to information about the pens.
 * It serves as a delegate for the JTable displayed in the main window
 * 
 * @author niklas
 */
public class PenTableModel extends AbstractTableModel implements Observer {
	
	private static final long serialVersionUID = 9114463176138867675L;
	
	//Ids for the columns
	public static final int PENID_COLUMN = 0;
	public static final int STATE_COLUMN = 1;
	public static final int NODE_COLUMN = 2;
	
	//column count
	public static final int COLUMN_COUNT = 3;
	
	/**
	 * list of all the pens connected to the system and displayed in the pen table
	 */
	private List<PenInformation> pens = new ArrayList<PenInformation>();
	
	/**
	 * add a bunch of discovered pens to the table
	 */
	public void add(List<PenInformation> discoveredPens) {
		for (PenInformation penInformation : discoveredPens) {
			if (!pens.contains(penInformation)) {
				int index = pens.size();
				pens.add(penInformation);
				penInformation.addObserver(this);
				fireTableRowsInserted(index, index);
			}
		}
	}

	/**
	 * delete a disconnected pen from the table
	 * @param penInfo
	 */
	public void delete(PenInformation penInfo) {
		penInfo.deleteObservers();
		int index = pens.indexOf(penInfo);
		pens.remove(penInfo);
		fireTableRowsDeleted(index, index);
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	@Override
	public int getRowCount() {
		return pens.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PenInformation pen = pens.get(rowIndex);
		switch (columnIndex) {
		case PENID_COLUMN:
			return pen.getPenID();
		case STATE_COLUMN:
			return pen.getPenState();
		case NODE_COLUMN:
			return pen.getNodeId();
		}
		return null;
	}

	/**
	 * retrive the penInformation instance associated with the index
	 * @param index of the table row
	 * @return penInformation instance
	 */
	public PenInformation getPenInformation(int index) {
		if (index >= 0 && index < pens.size())
			return pens.get(index);
		else return null;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PenInformation) {
			final int index = pens.indexOf(o);
			fireTableRowsUpdated(index, index);
		}
	}


}
