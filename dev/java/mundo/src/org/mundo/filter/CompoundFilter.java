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
 * Stefan Radomski
 */

package org.mundo.filter;

import java.util.Map;
import java.util.Vector;

import org.mundo.rt.TypedContainer;
import org.mundo.xml.xqparser.SimpleNode;

public abstract class CompoundFilter implements IFilter {

  /**
   * The binding of variables to xpath expressions
   */
  protected Map<String, SimpleNode> variables;

	@Override
	public TypedMapFilter _getFilter() throws Exception {
		throw new UnsupportedOperationException("Can not map a compound filter to a TypedMapFilter");
	}

  @Override
  public IFilter merge(IFilter other) {
    CompoundAndFilter caf = new CompoundAndFilter();
    caf.add(this);
    caf.add(other);
    return caf;
  }

  /**
   * Return the subtrees matching the parsed xpath expression.
   *
   * @param xpath The AST from the xpath expression
   * @param tc The TypedContainer to examine.
   * @return The set of subtrees matching the xpath.
   */
  public static Vector<TypedContainer> getMatchingTrees(SimpleNode xpath, TypedContainer tc) {
    return new Vector<TypedContainer>();
  }
}
