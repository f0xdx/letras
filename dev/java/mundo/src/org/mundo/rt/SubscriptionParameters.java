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

package org.mundo.rt;

/**
 * <p><code>SubscriptionParameters</code> is an abstract base class. Message routing
 * services that support subscription parameters come with parameter classes
 * derived from <code>SubscriptionParameters</code>.</p>
 *
 * <p>Subscription parameters may support e.g. additional message filters or
 * quality of service parameters.</p>
 *
 * @see Subscriber
 * @author Erwin Aitenbichler
 */
public abstract class SubscriptionParameters
{
  /**
   * Returns whether the specified publisher is relevant. A publisher is relevant
   * iff there exists a subscriber that is covered by the advertisement described
   * by the publisher.
   * @throws UnsupportedOperationException  if the responsible messaging provider
   * does not support this operation.
   */
  public boolean isRelevant(Publisher p)
  {
    throw new UnsupportedOperationException();
  }
}
