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
 * The Basic Communication Layer (BCL) is the most basic communication
 * component. BCL providers implement this interface.
 *
 * @author Erwin Aitenbichler
 */
public interface IBCLProvider
{
  /**
   * Sends a message to a channel.
   * @param c        the channel to send the message to.
   * @param msg      the message to send.
   * @param exclude  an optional <code>Session</code> to be excluded
   *                 from the set of receivers.
   */
  public void send(Channel c, Message msg, Session exclude);

  /**
   * The signal interface of the BCL.
   *
   * @author Erwin Aitenbichler
   */
  public interface ISignal
  {
    /**
     * Raised when a new subscriber is added.
     * @param s  the subscriber object.
     */
    public void subscriberAdded(Subscriber s);
    
    /**
     * Raised when a subscriber is removed.
     * @param s  the subscriber object.
     */
    public void subscriberRemoved(Subscriber s);
    
    /**
     * Raised when a new publisher is added.
     * @param p  the publisher object.
     */
    public void publisherAdded(Publisher p);
    
    /**
     * Raised when a publisher is removed.
     * @param p  the publisher object.
     */
    public void publisherRemoved(Publisher p);
  }
}
