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
 * Protocol handlers implement this interface.
 * @author Erwin Aitenbichler
 */
public interface IMessageHandler
{
  /**
   * Called when a message is sent to a remote peer. The handler has to
   * process the specified message and then pass it on to the next lower
   * handler in the stack. If the operation is successful, the handler
   * returns <code>true</code>. In case of an error, the handler returns
   * <code>false</code>. Note that if <code>false</code> is returned, then
   * a handler higher up in the stack might try to retransmit the message.
   * As a consequence, the same message will be passed to this handler again.
   * 
   * @param msg  the message to send.
   * @return  <code>true</code> if the message could be processed and passed
   * down successfully; <code>false</code> otherwise.
   */
  public boolean down(Message msg);
  /**
   * Called when a message is received from a remote peer. The handler has
   * to process the specified message and then pass it on to the next higher
   * handler in the stack.
   * 
   * @param msg  the message received.
   * @return  <code>true</code> if the message could be processed and passed
   * up successfully; <code>false</code> otherwise.
   */
  public boolean up(Message msg);
}
