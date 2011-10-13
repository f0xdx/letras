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

import java.util.HashSet;

/**
 * A ZoneAccessRule stores all information about the rule for pens to access a specific zone.
 * Keep in mind that the ZoneAccessRule is unaware of the zone it is responsible for. This way
 * you can easily create a clone of the ZoneAccessRule and use it for another zone.
 * By default, a new instance of ZoneAccessRule allows all connections. To initialize a ZoneAccessRule
 * with specific data use the corresponding constructor.
 * 
 * @author niklas
 */
public class ZoneAccessRule implements Cloneable{
	
	/**
	 * Working state identifier
	 */
	private static int ALLOW_MODE = 0, DENY_MODE = 1;
	
	/**
	 * String for building string representation of the rule
	 */
	private static String ALLOW = "ALLOW", DENY = "DENY", TYP_SEPERATOR = ":", ELEMENT_SEPERATOR = " ";
	
	/**
	 * general state in which we are operating 
	 */
	private int mode;
	
	/**
	 * flag whether to allow/deny everything. together with the mode this forms the state
	 */
	boolean all;
	
	/**
	 * penIDs of the rule
	 */
	private HashSet<String> penIds = new HashSet<String>();
	
	/**
	 * Nullary constructor.
	 * Creates a rule that allows all pens.
	 */
	ZoneAccessRule() {
		this("ALLOW:*");
	}
	
	/**
	 * Construct a rule from the specified String. If the String has the wrong 
	 * format an IllegalArgumentException will be thrown.
	 * @param ruleString rule as a string
	 * @throws IllegalArgumentException if ruleString has wrong format
	 */
	ZoneAccessRule(String ruleString) throws IllegalArgumentException {
		
		//split the rule in type and list
		String[] splitString = ruleString.trim().split(TYP_SEPERATOR);

		if (splitString.length == 2) {
			
			String type = splitString[0].trim();
			//determine the type
			if (type.equals(ALLOW)) {
				mode = ALLOW_MODE;
			} else if (type.equals(DENY)) {
				mode = DENY_MODE;
			} else {
				throw new IllegalArgumentException("The rule type " + type + " is unknown");
			}
			
			//parse the list
			String[] list = splitString[1].trim().split(ELEMENT_SEPERATOR);
			
			if (list.length < 1) {
				throw new IllegalArgumentException("Second part of rule is missing");
			}
			
			if (list.length == 1 && list[0].equals(IPenAccessConfiguration.WILDCARD)) {
				all = true;
			} else {
				all = false;
				for (String penId : list) {
					if (penId.trim().length() > 0)
						penIds.add(penId.trim());
				}
			}
			return;
		}
	
		throw new IllegalArgumentException("Could not decompose rule. There can only be one \""+ TYP_SEPERATOR +"\" per rule.");
	}
	
	/**
	 * Creates and returns an exact copy of this rule
	 * @return copy
	 */
	public ZoneAccessRule clone() {
		ZoneAccessRule clone = new ZoneAccessRule();
		clone.mode = mode;
		clone.all = all;
		clone.penIds = new HashSet<String>(penIds);
		return clone;
	}
	
	/**
	 * check if pen with specified penId has access. 
	 * @param penId
	 * @return true if pen has access
	 */
	boolean hasAccess(String penId) {
		if (mode == ALLOW_MODE) {
			if (all) 
				return true;
			else 
				return penIds.contains(penId);
		} else
			if (all)
				return false;
			else
				return !penIds.contains(penId);
	}
	
	/**
	 * add a pen to the list of allowed pens
	 * @param penId
	 */
	void allowPen(String penId) {
		addRuleforPen(penId, ALLOW_MODE);
	}
	
	/**
	 * add a pen to the list of denied pens
	 * @param penId
	 */
	void denyPen(String penId) {
		addRuleforPen(penId, DENY_MODE);
	}

	/**
	 * helper method adding a rule for a pen
	 * @param penId
	 * @param usedMode
	 */
	private void addRuleforPen(String penId, int usedMode) {
		if (mode == usedMode) {
			if (!all) {
				penIds.add(penId);
			}
		} else {
			if (all) {
				mode = usedMode;
				all = false;
				penIds.clear();
				penIds.add(penId);
			} else {
				if (penIds.remove(penId) && penIds.isEmpty()) {
					mode = usedMode;
					all = true;
				}
			}
		}
	}
	
	/**
	 * change rule to allow access for all pens
	 */
	void allowAll() {
		mode = ALLOW_MODE;
		all = true;
		penIds = new HashSet<String>();
	}
	
	/**
	 * change rule to deny access for all pens
	 */
	void denyAll() {
		mode = DENY_MODE;
		all = true;
		penIds = new HashSet<String>();
	}
	
	/**
	 * return a string representation of the rule defined in this object
	 * @return the rule as string
	 */
	String ruleAsString() {
		String rule = (mode == ALLOW_MODE)?ALLOW:DENY;
		rule += TYP_SEPERATOR;
		if (all) {
			rule += IPenAccessConfiguration.WILDCARD;
		} else {
			for (String penId : penIds) {
				rule += penId + ELEMENT_SEPERATOR;
			}
		}
		return rule.trim();
	}
	
	
	/**
	 * compares this rule to another one by comparing all relevant attributes of the rules
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ZoneAccessRule) {
			ZoneAccessRule otherRule = (ZoneAccessRule) obj;
			return (mode == otherRule.mode && (all == otherRule.all || penIds.equals(otherRule.penIds)));
		} else return false;
	}
	
}
