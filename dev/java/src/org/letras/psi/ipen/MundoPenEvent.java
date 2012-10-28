package org.letras.psi.ipen;

import org.letras.api.pen.IPenState;
import org.letras.api.pen.PenEvent;

public class MundoPenEvent {

	// members

	/**
	 * state before the change
	 */
	public final int oldState;

	/**
	 * current state
	 */
	public final int state;

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

	public PenEvent getPenEvent() {
		return new PenEvent(oldState, state);
	}
}
