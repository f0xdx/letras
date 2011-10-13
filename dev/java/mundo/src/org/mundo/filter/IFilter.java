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
 * Stefan Radomski
 */

package org.mundo.filter;

/**
 * Active object filter classes implement this interface.
 * 
 * This interface used to only require _getFilter() to return a TypedMapFilter.
 * We keep the _getFilter() for legacy support but the more general approach is
 * to implement matches (legacy code matches, when the TypedMapFilter matches)
 * and merge, to combine to filters with OR.
 * 
 * In a perfect world and if we want to break backward compatibality, a
 * TypedMapFilter would just implement IFilter.
 */
public interface IFilter extends IFilterConstants
{
  /**
   * Generates a passive filter map from this active object filter.
   */
  public TypedMapFilter _getFilter() throws Exception;

  /**
   * Match an object against this filter
   * 
   * @param obj The object to check.
   * @return Whether or not the object matches the filter.
   * @throws Exception Most likely UnsupportedOperationExceptions
   */
  public boolean matches(Object obj) throws Exception;

  /**
   * Merge this filter with a given one and return the result. Leave this instance unchanged.
   * 
   * @param other Another filter to combine to this one using boolean OR.
   * @return A new IFilter which is true if either this or the other filter are true.
   */
  public IFilter merge(IFilter other);
}
