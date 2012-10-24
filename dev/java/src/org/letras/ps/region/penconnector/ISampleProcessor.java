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
package org.letras.ps.region.penconnector;

import org.letras.api.pen.IPen;
import org.letras.api.pen.PenSample;

/**
 * The ISampleProcessor interface provides method that are to be implemented
 * in order to receive samples from a pen.
 * A class which realizes this interface could for example aggregate samples
 * to strokes and map them to regions.
 * @author niklas
 */
public interface ISampleProcessor {

	/**
	 * set the pen connection so that ISampleProcessor can get information
	 * about the pen for which it is the delegate.
	 * @param pen the <code>PenConnection</code>
	 */
	public void setConnectedPen(IPen pen);

	/**
	 * tell the sample processor that the pen has been put down on the paper.
	 */
	public void penDown();

	/**
	 * send a sample to be processed by the sample processor. Make sure that
	 * penDown had been called beforehand and penUp has not yet been called.
	 * @param sample the sample to be processed
	 */
	public void handleSample(PenSample sample);

	/**
	 * tell the sample processor that the pen has been lifted from the paper.
	 */
	public void penUp();

}
