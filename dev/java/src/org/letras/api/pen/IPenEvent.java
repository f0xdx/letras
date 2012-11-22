package org.letras.api.pen;

public interface IPenEvent {

	public int getState();

	public int getOldState();

	public PenEvent getPenEvent();

}
