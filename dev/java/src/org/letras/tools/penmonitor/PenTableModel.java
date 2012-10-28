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
import java.util.HashMap;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.letras.api.pen.IPen;
import org.letras.api.pen.IPenDiscovery;

/**
 * The PenTableModel manages access to information about the pens.
 * It serves as a delegate for the JTable displayed in the main window
 * 
 * @author niklas
 */
public class PenTableModel extends AbstractTableModel implements IPenDiscovery {

	private static final long serialVersionUID = 9114463176138867675L;

	//Ids for the columns
	public static final int PENID_COLUMN = 0;
	public static final int STATE_COLUMN = 1;

	//column count
	public static final int COLUMN_COUNT = 2;

	/**
	 * list of all the pens connected to the system and displayed in the pen table
	 */
	private final List<IPen> pens = new ArrayList<IPen>();

	private final HashMap<IPen, PenInformation> listener = new HashMap<IPen, PenInformation>();

	/**
	 * add a discovered pen to the table
	 */
	@Override
	public void penConnected(final IPen ipen) {
		if (!pens.contains(ipen)) {
			final int index = pens.size();
			pens.add(ipen);
			final PenInformation listener = new PenInformation(ipen.getPenId());
			ipen.registerPenListener(listener);
			this.listener.put(ipen, listener);
			fireTableRowsInserted(index, index);
		}
	}

	/**
	 * delete a pen from the table
	 * 
	 * @param penInfo
	 */
	@Override
	public void penDisconnected(IPen pen) {
		pen.unregisterPenListener(this.listener.get(pen));
		final int index = pens.indexOf(pen);
		pens.remove(pen);
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
		final PenInformation pen = listener.get(pens.get(rowIndex));
		switch (columnIndex) {
		case PENID_COLUMN:
			return pen.getPenID();
		case STATE_COLUMN:
			return pen.getPenState();
		}
		return null;
	}

	/**
	 * retrive the penInformation instance associated with the index
	 * 
	 * @param index of the table row
	 * @return IPen instance
	 */
	public PenInformation getPenInformation(int index) {
		if (index >= 0 && index < pens.size())
			return listener.get(pens.get(index));
		else return null;
	}


}
