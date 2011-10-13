/*
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
 * Department of Computer Science, Darmstadt University of Technology.
 * Portions created by the Initial Developer are
 * Copyright (C) 2001-2008 the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Erwin Aitenbichler
 */

package org.mundo.filter;

/**
 * Defines constants for filters.
 */
public interface IFilterConstants
{
  /**
   * The test is always successful.
   */
  public static final int OP_IGNORE        = 0;
  /**
   * Tests if the attribute value is equal to the compare value.
   */
  public static final int OP_EQUAL         = 1;
  /**
   * Tests if the attribute value is greater than the compare value.
   */
  public static final int OP_GREATER       = 2;
  /**
   * Tests if the attribute value is less than the compare value.
   */
  public static final int OP_LESS          = 3;
  /**
   * Tests if the attribute string starts with the string defined in the filter.
   */
  public static final int OP_STARTS        = 0x10;
  /**
   * Tests if the attribute string ends with the string defined in the filter.
   */
  public static final int OP_ENDS          = 0x11;
  /**
   * Tests if the attribute string contains the string defined in the filter.
   */
  public static final int OP_CONTAINS      = 0x12;
  /**
   * Tests if the compare value, which is a filter object, covers the attribute value.
   */
  public static final int OP_FILTER        = 0x20;
  /**
   * This modifier can be combined with other operators to inverse the
   * test condition.
   */
  public static final int OP_NOT           = 0x1000;
  /**
   * This modifier can be combined with other operators to make
   * string comparisons case-insensitive.
   */
  public static final int OP_IGNORE_CASE   = 0x2000;
  /**
   * Tests if the attribute value is not equal to the compare value.
   * Identical to <code>OP_NOT | OP_EQUAL</code>.
   */
  public static final int OP_NOT_EQUAL     = 0x1001;
  /**
   * Tests if the attribute value is less or equal to the compare value.
   * Identical to <code>OP_NOT | OP_GREATER</code>.
   */
  public static final int OP_LESS_EQUAL    = 0x1002;
  /**
   * Tests if the attribute value is greater or equal to the compare value.
   * Identical to <code>OP_NOT | OP_LESS</code>.
   */
  public static final int OP_GREATER_EQUAL = 0x1003;
  /**
   * Keeps the operator part.
   */
  public static final int MASK_OP          = 0x0fff;
  /**
   * Keeps the modifier part.
   */
  public static final int MASK_MOD         = 0xf000;
}
