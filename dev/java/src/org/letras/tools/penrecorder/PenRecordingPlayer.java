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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.letras.psi.ipen.IPen;
import org.letras.psi.ipen.PenSample;
import org.mundo.annotation.mcRemote;
import org.mundo.rt.GUID;

@mcRemote
public class PenRecordingPlayer extends AbstractPenRecordingPlayer implements Runnable, IPen {
	
	private Lock playLock = new ReentrantLock();
	private AtomicBoolean play = new AtomicBoolean();
	private double speed = 1.0;

	public PenRecordingPlayer() {
		channel = "Letras Player " + new GUID().toString();
	}
	
	public void play(PenRecording penRecording) {
		playLock.lock();
		load(penRecording);
		new Thread(this).start();
		playLock.unlock();
	}
	
	public void playSync(PenRecording penRecording) {
		load(penRecording);
		run();
	}
	
	public synchronized void setSpeed(double newSpeed) {
		speed = newSpeed;
	}
	
	@Override
	public void run() {
		playLock.lock();
		play.set(true);
		while (currentRecording.hasNext()) {
			Object message = currentRecording.getNext();
			if (!play.get())
				break;
			processMessage(message);
		}
		currentRecording.reset();
		currentTime = -1;
		playLock.unlock();
	}

	@Override
	protected void processSample(PenSample sample) {
		if (currentTime == -1)
			currentTime = sample.getTimestamp();
		else {
			try {
				double currentSpeed;
				synchronized(this) {
					currentSpeed = speed;
				}
				Thread.sleep((long) ((sample.getTimestamp() - currentTime) / currentSpeed));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.processSample(sample);
		currentTime = sample.getTimestamp();
	}

	public void stop() {
		play.set(false);
	}
	
}
