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

/**
 * A pen adapter factory allows creation of a specific pen adapter. Implementing classes
 * need to instantiate concrete implementations of the <code>IPenAdapter</code> interface
 * and guarantee that these adapters connect to the appropriate pen instances. Normally,
 * a pen driver will use an injected pen adapter factory to instantiate new pen adapters
 * as needed. This injection is carried out by the surrounding framework, namely a pen driver
 * management component.
 * 
 * @author felix_h
 * @version 0.0.1
 *
 */
public interface IPenAdapterFactory {
	
	/**
	 * This method allows to create a new pen adapter. Normally, a pen driver will invoke
	 * this method in order to create an adapter for a pen. This adapter is then used in order
	 * to send samples obtained by the pen.
	 * <P>
	 * Parameters of this method are used by the factory to create a unique pen id for each pen.
	 * The contract for this id guarantees that each pen obtains the same unique id each time it
	 * is connected, as long as the same driver class and the same token are passed upon adapter
	 * creation.
	 * 
	 * @param driver the driver requiring creation of the pen adapter
	 * @param token a token associated with the adapted pen
	 * @return the created pen adapter
	 */
	public IPenAdapter create(IPenDriver driver, String token);

}
