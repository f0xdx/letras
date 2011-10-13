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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AccessRuleTest {

	private ZoneAccessRule rule;
	
	@Before
	public void setUp() {
		rule = new ZoneAccessRule();
	}
	
	 @Test
	 public void testDefaultAllowRule() {
		 assertTrue(rule.hasAccess("pen1"));
		 assertEquals("ALLOW:*", rule.ruleAsString());
	 }
	 
	 @Test
	 public void testAllowRule1() {
		 rule.allowPen("pen1");
		 assertTrue(rule.hasAccess("pen1"));
		 assertEquals("ALLOW:*", rule.ruleAsString());
	 }
	 
	 @Test
	 public void testAllowRule2() {
		 rule.allowAll();
		 assertTrue(rule.hasAccess("pen1"));
		 assertEquals("ALLOW:*", rule.ruleAsString());
	 }
	 
	 @Test
	 public void testAllowRule3() {
		 rule.denyAll();
		 rule.allowPen("pen1");
		 assertTrue(rule.hasAccess("pen1"));
		 assertFalse(rule.hasAccess("pen2"));
		 assertEquals("ALLOW:pen1", rule.ruleAsString());
	 }
	 
	 
	 @Test
	 public void testDenyRule1() {
		 rule.denyPen("pen1");
		 assertFalse(rule.hasAccess("pen1"));
		 assertTrue(rule.hasAccess("pen2"));
		 assertEquals( "DENY:pen1", rule.ruleAsString());
	 } 
	 
	 @Test
	 public void testDenyRule2() {
		 rule.denyPen("pen1");
		 rule.denyPen("pen2");
		 assertFalse(rule.hasAccess("pen1"));
		 assertFalse(rule.hasAccess("pen2"));
		 assertEquals("DENY:pen2 pen1", rule.ruleAsString());
	 } 
	 
	 @Test
	 public void testDenyRule3() {
		 rule.denyAll();
		 assertFalse(rule.hasAccess("pen1"));
		 assertEquals("DENY:*", rule.ruleAsString());
	 } 
	 
	 @Test
	 public void testAllowDenyRule1() {
		 rule.allowPen("pen1");
		 rule.denyAll();
		 rule.allowPen("pen2");
		 assertFalse(rule.hasAccess("pen1"));
		 assertTrue(rule.hasAccess("pen2"));
		 assertEquals("ALLOW:pen2", rule.ruleAsString());
	 }
	 
	 @Test
	 public void testAllowDenyRule2() {
		 rule.denyPen("pen1");
		 rule.allowAll();
		 rule.denyPen("pen2");
		 assertTrue(rule.hasAccess("pen1"));
		 assertFalse(rule.hasAccess("pen2"));
		 assertEquals("DENY:pen2", rule.ruleAsString());
	 }
	 
	 @Test
	 public void testAllowDenyRule3() {
		 rule.denyPen("pen1");
		 rule.allowPen("pen1");
		 assertTrue(rule.hasAccess("pen1"));
		 assertTrue(rule.hasAccess("pen2"));
		 assertEquals("ALLOW:*", rule.ruleAsString());
	 }
	 
	 @Test
	 public void testAllowDenyRule4() {
		 rule.denyAll();
		 rule.allowPen("pen1");
		 rule.denyPen("pen1");
		 assertFalse(rule.hasAccess("pen1"));
		 assertFalse(rule.hasAccess("pen2"));
		 assertEquals("DENY:*", rule.ruleAsString());
	 }
	 
	 

}
