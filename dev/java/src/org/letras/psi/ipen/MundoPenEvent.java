package org.letras.psi.ipen;

import org.letras.api.pen.IPenEvent;
import org.letras.api.pen.IPenState;
import org.letras.api.pen.PenEvent;
import org.mundo.annotation.mcSerialize;

@mcSerialize
public class MundoPenEvent implements IPenEvent {

	// members

	/**
	 * state before the change
	 */
	protected int oldState;

	/**
	 * current state
	 */
	protected int state;

	// constructors

	/**
	 * Simple no-argument constructor.
	 */
	public MundoPenEvent() {
		this.oldState = this.state = IPenState.OFF;
	}

	/**
	 * Simple constructor taking the old and the new state as arguments.
	 * 
	 * @param oldState the old state of the pen
	 * @param newState the new state of the pen
	 */
	public MundoPenEvent(int oldState, int newState) {
		this.oldState = oldState;
		this.state = newState;
	}

	@Override
	public int getState() {
		return state;
	}

	@Override
	public int getOldState() {
		return oldState;
	}

	@Override
	public PenEvent getPenEvent() {
		return new PenEvent(oldState, state);
	}
}
