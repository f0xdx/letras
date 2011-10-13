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
package org.letras.ps.region.sampleprocessor;

import org.letras.ps.region.penconnector.IPenConnection;
import org.letras.ps.region.penconnector.ISampleProcessor;

/**
 * Interface for a factory, which creates instances of classes implementing the 
 * ISampleProcessor interface.
 * A class implementing ISampleProcessorFactory is needed by the PenAccessManager 
 * to create instances of a SampleProcessor for newly connected pens
 * @author niklas
 *
 */
public interface ISampleProcessorFactory {
	/**
	 * factory method returning an instance of ISampleProcessor
	 * @return a new sample processor
	 */
	public ISampleProcessor createSampleProcessor(IPenConnection pen);
}
