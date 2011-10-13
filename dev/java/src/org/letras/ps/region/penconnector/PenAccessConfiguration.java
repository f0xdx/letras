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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mundo.rt.TypedMap;


/**
 * A PenAccessConfiguration stores the rules for allowing and denying pens which try to 
 * connect to this instance of the <code>RegionProcessingStage</code>.
 * The configuration can be changed at runtime by selectively specifying pens that should 
 * be allowed or denied, or by providing a <code>TypedMap</code> with access rules. 
 * A request for the current access rules returned in a <code>TypedMap</code> is also possible.
 *
 * The PenAccessConfiguration can be set to either let all pens, all with exception of 
 * blacklisted ones only the pens on a whitelist or no pens connect to the stage. Different 
 * rules can be defined for each zone, for example to allow all pens from the <code>rt</code> 
 * zone but only whitelisted pens from the <code>lan</code> zone.
 * <p>
 * The configuration of the PenAccessConfiguration is done by loading a TypedMap. This map 
 * should originate from a class with the service configuration interface(<code>IConfigure</code>)
 * provided by Mundo. Therefore configuration happens in the <code>node.conf.xml</code>.  
 * <p>
 * The access rules are configured in two steps. <br />
 * <ol>
 *   <li>Define a general rule for all zones
 *   <li>Define special rules for single zones which completly override the general rule
 * </ol>
 * A valid configuration looks like this:
 * <pre>
 * &lt;access&gt;
 *  &lt;general&gt;DENY:*&lt;/general&gt;
 *  &lt;zones&gt;
 *    &lt;rt&gt;ALLOW:@p68763 @p92152&lt;/rt&gt;
 *  &lt;/zones&gt;
 * &lt;/access&gt;
 * </pre>
 * This would deny access the all pens accept the two whitelisted for the zone <code>rt</code>. 
 * @author niklas
 *
 */
class PenAccessConfiguration implements IPenAccessConfiguration{

	private static final Logger logger = Logger.getLogger("org.letras.ps.region.penconnector");
	
	/**
	 * used if a pen connects from within a zone for which no specific rule exists
	 */
	private ZoneAccessRule generalAccessRule;
	
	/**
	 * map of zone specific access rules.
	 */
	private HashMap<String, ZoneAccessRule> zoneAccessRules;
	
	public PenAccessConfiguration() {
		generalAccessRule = new ZoneAccessRule();
		zoneAccessRules = new HashMap<String, ZoneAccessRule>();
	}
	
	/**
	 * check if a pen coming from within a zone is allowed to access. 
	 * @param penId identifier of the connecting pen
	 * @param zone name of the zone in which the pen resides
	 * @return <code>true</code> if pen is allowed to access; <code>false</code> otherwise
	 */
	public boolean hasAccess(String penId, String zone) {
		if (zoneAccessRules.containsKey(penId)) {
			return zoneAccessRules.get(penId).hasAccess(penId);
		} else {
			return generalAccessRule.hasAccess(penId);
		}
	}
	
	@Override
	public void allowPenInGeneral(String penId, boolean propagate) {
		generalAccessRule.allowPen(penId);
		
		if (propagate)
			for (Entry<String, ZoneAccessRule> entry : zoneAccessRules.entrySet()) {
				entry.getValue().allowPen(penId);
				if (entry.getValue().equals(generalAccessRule)) {
					zoneAccessRules.remove(entry.getKey());
				}
			}
	}
	
	@Override
	public void denyPenInGeneral(String penId, boolean propagate) {
		generalAccessRule.denyPen(penId);
		
		if (propagate) {
			for (Entry<String, ZoneAccessRule> entry : zoneAccessRules.entrySet()) {
				entry.getValue().denyPen(penId);
				if (entry.getValue().equals(generalAccessRule)) {
					zoneAccessRules.remove(entry.getKey());
				}
			}
		}
	}

	@Override
	public void allowPenInZone(String penId, String zone) {
		if (zoneAccessRules.containsKey(zone)) {
			zoneAccessRules.get(penId).allowPen(penId);
		} else {
			if (!generalAccessRule.hasAccess(penId)) {
				ZoneAccessRule newRule = generalAccessRule.clone();
				newRule.allowPen(penId);
				zoneAccessRules.put(zone, newRule);
			}
		}
	}

	@Override
	public void denyPenInZone(String penId, String zone) {
		if (zoneAccessRules.containsKey(zone)) {
			zoneAccessRules.get(zone).denyPen(penId);
		} else {
			if (generalAccessRule.hasAccess(penId)) {
				ZoneAccessRule newRule = generalAccessRule.clone();
				newRule.denyPen(penId);
				zoneAccessRules.put(zone, newRule);
			}
		}
	}
	
	@Override
	public void deleteRuleForZone(String zone) {
		zoneAccessRules.remove(zone);
	}

	/**
	 * get a <code>TypedMap</code> representation of the rules stored inside this <code>PenAccessConfiguration</code>
	 * @return the map containing all the rules
	 */
	public TypedMap getRulesAsMap() {
		TypedMap map = new TypedMap();
		map.put("general", generalAccessRule.ruleAsString());
		
		TypedMap zones = new TypedMap();
		for (Entry<String, ZoneAccessRule> entry : zoneAccessRules.entrySet()) {
			zones.put(entry.getKey(), entry.getValue().ruleAsString());
		}
		
		map.put("zones", zones);
		
		return map;
	}
	
	/**
	 * discard the current rules and replace them with the rules stored inside the TypedMap
	 * @param map containing rules
	 * @return <code>true</code> if map was correctly formatted and changes have been made;
	 * 			<code>false</code> otherwise
	 */
	public boolean loadRulesFromMap(TypedMap map) {
		ZoneAccessRule savedGeneralRule = generalAccessRule;
		HashMap<String, ZoneAccessRule>  savedZoneAccessRules = zoneAccessRules;
		
		try {
			if (map.containsKey("general")) {
				generalAccessRule = new ZoneAccessRule(map.getString("general"));
			} else return false;
			
			if (map.containsKey("zones")) {
				TypedMap zonesMap = map.getMap("zones");
				for (String zone : zonesMap.keySet()) {
					zoneAccessRules.put(zone, new ZoneAccessRule(zonesMap.getString(zone)));
				} 
			}
			return true;
			
		} catch (IllegalArgumentException iae) {
			logger.logp(Level.WARNING, "PenAccessConfiguration", "loadRulesfromMap", String.format("Error loading access rules from configuration: %s", iae.getMessage()));
			generalAccessRule = savedGeneralRule;
			zoneAccessRules = savedZoneAccessRules;
			return false;
		}
	}

}
