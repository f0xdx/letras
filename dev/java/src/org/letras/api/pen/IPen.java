package org.letras.api.pen;

/**
 * Interface to access a pen in Letras. <br />
 * You can do the following:
 * <ul>
 * <li>Get information about the state of the pen</li>
 * <li>Register a {@link IPenListener} to receive {@link PenEvent}s and {@link PenSample}s</li>
 * </ul>
 * 
 * @version 0.3
 * @author Niklas Lochschmidt <nlochschmidt@gmail.com>
 */
public interface IPen {

	/**
	 * register a new listener for this pen
	 * 
	 * @param penListener
	 */
	public void registerPenListener(IPenListener penListener);

	/**
	 * unregister a listener from this pen
	 * 
	 * @param penListener
	 */
	public void unregisterPenListener(IPenListener penListener);

	/**
	 * get the current penState
	 * 
	 * @see {@link IPenState}
	 * @return penState
	 */
	public int getPenState();

	/**
	 * get the penId of this pen
	 * 
	 * @return penId
	 */
	public String getPenId();

	/**
	 * Implement this interface and register it to an {@link IPen} to receive {@link PenSample}s and {@link PenEvent}s
	 */
	public interface IPenListener {

		/**
		 * receive a pen event
		 * 
		 * @param penevent
		 */
		void receivePenEvent(PenEvent penEvent);

		/**
		 * receive a pen sample
		 * 
		 * @param pensample
		 */
		void receivePenSample(PenSample penSample);
	}


}
