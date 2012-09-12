package org.letras.api.pen;

import org.letras.api.ILetras;


/**
 * Callback that can be registered with {@link ILetras} to receive a notification when a pen is connected/disconnected
 * 
 * @version 0.3
 * @author Niklas Lochschmidt <nlochschmidt@gmail.com>
 */
public interface IPenDiscovery {

	/**
	 * callback once a new pen connected
	 * 
	 * @param pen
	 */
	public void penConnected(IPen pen);

	/**
	 * callback once a known pen disconnects.<br />
	 * once this method gets called a call to {@link IPen#getPenState()} yields {@link IPenState#OFF}
	 * 
	 * @param pen
	 */
	public void penDisconnected(IPen pen);

}
