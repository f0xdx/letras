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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.letras.api.pen.PenEvent;
import org.letras.api.pen.PenSample;

public class PenRecording {
	private List<Object> messages = new ArrayList<Object>();
	private int currentMessage = 0;
	private long startTime = Long.MAX_VALUE;
	private long endTime = Long.MIN_VALUE;
	private long currentTime = -1;
	private List<IPositionListener> listeners = new LinkedList<IPositionListener>();

	public PenRecording() {
	}
	
	public PenRecording(File f) {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(f));
			String line = r.readLine();
			while (line != null) {
				String[] fields = line.split(";");
				if (fields.length != 5 && fields.length != 3)
					throw new IllegalStateException("File format not recognized");
				String type = fields[0].trim();
				if (type.equals("PenEvent")) {
					messages.add(makeEvent(fields));
				} else if (type.equals("PenSample")) {
					messages.add(makeSample(fields));
				} else {
					throw new IllegalStateException("File format not recognized");
				}
				line = r.readLine();
			}
			startTime = getStartTime();
			endTime = getEndTime();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (r != null)
				try {
					r.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	private PenSample makeSample(String[] fields) {
		if (fields.length != 5)
			throw new IllegalArgumentException("File format not recognized");
		return new PenSample(Double.valueOf(fields[1].trim()), Double.valueOf(fields[2].trim()), Integer.valueOf(fields[3].trim()), Long.valueOf(fields[4].trim()));
	}

	private PenEvent makeEvent(String[] fields) {
		if (fields.length != 3) 
			throw new IllegalArgumentException("File format not recognized");
		return new PenEvent(Integer.valueOf(fields[1].trim()), Integer.valueOf(fields[2].trim()));
	}

	public void record(PenSample o) {
		currentTime = o.getTimestamp();
		startTime = Math.min(startTime, currentTime);
		endTime = Math.max(endTime, currentTime);
		firePositionChanged();
		messages.add(o);
	}

	public void record(PenEvent o) {
		messages.add(o);
	}

	public void save(File f) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(f));
			for (Object o: messages) {
				if (o instanceof PenSample) {
					writeSample((PenSample) o, pw);
				}
				else if (o instanceof PenEvent) {
					writeEvent((PenEvent) o, pw);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
		}
	}
	
	private void writeEvent(PenEvent event, PrintWriter pw) {
		pw.println("PenEvent; " + event.getOldState() + "; " + event.getNewState());
	}

	private void writeSample(PenSample sample, PrintWriter pw) {
		pw.println("PenSample; " + sample.getX() + "; " + sample.getY() + "; " + sample.getForce() + "; " + sample.getTimestamp());
	}

	public boolean hasNext() {
		return currentMessage < messages.size();
	}
	
	public Object getNext() {
		if (currentTime < 0) {
			currentTime = startTime;
		}
		Object message = messages.get(currentMessage++);
		if (message instanceof PenSample) {
			currentTime = ((PenSample) message).getTimestamp();
		}
		firePositionChanged();
		return message;
	}
	
	private long getStartTime() {
		for (int i=0; i < messages.size(); i++) {
			if (messages.get(i) instanceof PenSample) {
				return ((PenSample) messages.get(i)).getTimestamp();
			}
		}
		return 0;
	}
	
	private long getEndTime() {
		for (int i=messages.size() - 1; i >= 0; i--) {
			if (messages.get(i) instanceof PenSample) {
				return ((PenSample) messages.get(i)).getTimestamp();
			}
		}
		return 0;
	}
			
	public long getLength() {
		return endTime - startTime;
	}
	
	public long getPosition() {
		return currentTime - startTime;
	}
	
	public void addPositionListener(IPositionListener l) {
		listeners.add(l);
	}
	
	public void removePositionListener(IPositionListener l) {
		listeners.remove(l);
	}
	
	protected void firePositionChanged() {
		for (IPositionListener l: listeners) {
			l.positionChanged(getPosition(), getLength());
		}
	}

	public void reset() {
		currentTime = startTime;
		currentMessage = 0;
		firePositionChanged();
	}

}
