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
package org.letras.ps.rawdata.penmanager;

import org.letras.api.pen.PenSample;
import org.letras.ps.rawdata.IPenAdapter;

/**
 * Generic implementation of the <code>IPenAdapter</code> interface. This is
 * is suitable for all pen drivers.
 * 
 * @author felix_h
 * @version 0.0.1
 */
public class GenericPenAdapter implements IPenAdapter {

	// members

	private String penId;

	private PenService service;

	public String getPenId() {
		return penId;
	}

	public void setPenId(String penId) {
		this.penId = penId;
	}

	public PenService getService() {
		return service;
	}

	public void setService(PenService service) {
		this.service = service;
	}

	// constructors
	
	/**
	 * Simple constructor taking the used pen id and the service reference
	 * as arguments
	 * 
	 * @param penId		the id of the pen
	 * @param service	the <code>PenService</code> this adapter adapts to
	 */
	public GenericPenAdapter(String penId, PenService service) {
		this.penId = penId;
		this.service = service;
	}
	
	// methods

	/**
	 * Interface method from {@link org.letras.ps.rawdata.IPenAdapter}.
	 */
	@Override
	public String penId() {
		return this.penId;
	}

	/**
	 * Interface method from {@link org.letras.ps.rawdata.IPenAdapter}.
	 */
	@Override
	public void penState(int state) {
		this.service.setPenState(state);
	}

	/**
	 * Interface method from {@link org.letras.ps.rawdata.IPenAdapter}.
	 */
	@Override
	public void publishSample(PenSample sample) {
		this.service.publishSample(sample);
	}

}
