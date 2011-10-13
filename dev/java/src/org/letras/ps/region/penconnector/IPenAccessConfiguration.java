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

import org.mundo.rt.TypedMap;

/**
 * Interface containing methods for altering the policy for excepting
 * or rejecting connections of pens to the RegionProcessingStage.
 * @author niklas
 *
 */
public interface IPenAccessConfiguration {
	
	/**
	 * The Wildcard stands for all pens. It is used to specify that all pens should
	 * be allowed/denied and can be used instead of a penId in the methods to alter
	 * the rules. 
	 */
	public final String WILDCARD = "*";
	
	/**
	 * Adds a pen to the list of allowed pens in the general rule.
	 * This does not have any effect if the pen is already allowed.
	 * @param penId the pen that should be allowed or {@link #WILDCARD} 
	 * if all pens should be allowed.
	 * @param propagate set to true if the change should propagate in all zones
	 */
	public void allowPenInGeneral(String penId, boolean propagate);
	
	/**
	 * Adds a pen to the list of denied pens in the general rule.
	 * This does not have any effect if the pen is already denied.
	 * @param penId the pen that should be denied or {@link #WILDCARD} 
	 * to deny all pens.
	 * @param propagate set to true if the change should propagate in all zones
	 */
    public void denyPenInGeneral(String penId, boolean propagate);
    
    /**
     * Adds a pen to the list of allowed pens in the rule for a 
     * specific zone.
     * @param penId the pen that should be allowed or {@link #WILDCARD}
     * to allow all pens
     * @param zone the zone for which the pen should be allowed
     */
	public void allowPenInZone(String penId, String zone);
	
	/**
	 * Adds a pen to the list of denied pens in the rule for a 
	 * specific zone.
	 * @param penId the pen that should be denied or {@link #WILDCARD}
	 * to deny all pens
	 * @param zone the zone for which the pen should be denied
	 */
	public void denyPenInZone(String penId, String zone);
	
	/**
	 * Delete the rule for the specified zone
	 * @param zone from which to delete the rule
	 */
	public void deleteRuleForZone(String zone);
	
	/**
	 * load rules from the specified TypedMap
	 * @param ruleMap map containing the rules to be used
	 * @return true if map was correct and rules have been applied; false otherwise
	 */
	public boolean loadRulesFromMap(TypedMap ruleMap);
	
	/**
	 * get the rules as a TypedMap
	 * @return TypedMap containing the rules
	 */
	public TypedMap getRulesAsMap();
}
