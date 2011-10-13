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
package org.letras.ps.rawdata;

import org.letras.psi.ipen.PenSample;

/**
 * Pen adapters are used by a pen driver to access the logical pen 
 * representation. While the pen manager manages available pens and
 * allows for their retrieval, these pens provide only read access 
 * to a pen's data. On the other hand they support all the needed 
 * methods for accessing a pens additional capabilities (if applicable).
 * <P>
 * To maintain a clear separation between the logical access to a pen
 * needed by other parts of the system, write-access in the class pen 
 * cannot be allowed. Therefore the pen driver needs to use a pen
 * adapter in order to publish samples etc. of a given pen.
 * <P>
 * Such a pen adapter is defined by this interface. It allows to set
 * the pen's state and publish its samples, as well as to obtain the
 * unique pen Id in case this is needed by the management application.
 * Which concrete Id this is depends on the implementation and the
 * pen manager component providing this interface.
 * 
 * @author felix_h
 * @version 0.0.1
 *
 */
public interface IPenAdapter {

	/**
	 * Whenever a pen driver obtains raw data for a given pen, this raw data
	 * is published via this method. It takes a <code>PenSample</code> as
	 * specified in the <code>IPen</code> processing stage interface.
	 * <P>
	 * Typical usage of this method is as follows
	 * <ol>
	 *  <li> set the pen state to DOWN, via a call to <code>penState(IPenState.DOWN)</code>
	 *  <li> emit the pen data by publishing <code>RawDataSamples</code> via the
	 *  <code>publishSample(sample)</code> method
	 *  <li> set the pen state to UP, via a call to <code>penState(IPenState.UP)</code> 
	 * </ol>
	 * 
	 * @param sample the <code>PenSample</code> describing an obtained data sample
	 */
	public void publishSample(PenSample sample);
	
	/**
	 * Each pen adapter corresponds to a specific pen. This method can be used to
	 * obtain the unique Id of this pen and can be used e.g. for management tasks
	 * on the driver side.
	 * 
	 * @return the pen Id of the pen this adapter references
	 */
	public String penId();
	
	/**
	 * Sets the referenced pen into the specified state. Potential states are represented
	 * in the {@link org.letras.psi.ipen.IPenState} interface. Such states can be
	 * <ul>
	 *  <li> OFF: For a turned off pen
	 *  <li> ON: For a pen that is turned on
	 *  <li> UP: For a pen which is currently UP, meaning it is not sending data samples
	 * </ul> DOWN: For a pen which is currently down, i.e. sending data samples
	 * 
	 * @param state a state as defined in {@link org.letras.psi.ipen.IPenState}
	 */
	public void penState(int state);
}
