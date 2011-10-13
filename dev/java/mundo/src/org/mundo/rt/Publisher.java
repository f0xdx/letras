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

import org.mundo.rt.Channel;

/**
 * The <code>Publisher</code> class represents an advertisement and is used
 * to send messages to a specific channel. Instances of <code>Publisher</code>
 * can be created via <code>Session</code> objects.
 * @see Session
 * @author Erwin Aitenbichler
 */
public class Publisher
{
  Publisher(Channel c)
  {
    channel=c;
  }
  /**
   * Sends a message to the channel.
   * @param msg  the message to send.
   */
  public void send(Message msg)
  {
    msg.setStack(channel.getStack());
    Mundo.bcl.send(channel, msg, localLoopback ? null : channel.getSession(), this);
  }
  /**
   * Returns the session object this <code>Publisher</code> belongs to.
   * @return  the session object.
   */
  public Session getSession()
  {
    return channel.getSession();
  }
  /**
   * Returns the channel this <code>Publisher</code> publishes to.
   * @return  the channel object.
   */
  public Channel getChannel()
  {
    return channel;
  }
  /**
   * Enables or disables local loopback. If local loopback is enabled, subscribers
   * in the same session may receive the published messages. By default, local
   * loopback is disabled.
   * @param b  <code>true</code> to enable local loopback,
   *           or <code>false</code> to disable local loopback.
   */
  public void enableLocalLoopback(boolean b)
  {
    localLoopback=b;
  }
  /**
   * Returns whether local loopback is enabled.
   * @return  <code>true</code> if local loopback is enabled,
   *          or <code>false</code> otherwise.
   */
  public boolean getLocalLoopback()
  {
    return localLoopback;
  }
  /**
   * Sets subscription parameters.
   * @param p  the subscription parameters.
   */
  public void setParam(SubscriptionParameters p)
  {
    param=p;
  }
  /**
   * Returns the subscription parameters.
   * @return  the subscription parameters.
   */
  public SubscriptionParameters getParam()
  {
    return param;
  }
  /**
   * Returns whether this publisher is relevant. A publisher is relevant if there
   * exists a subscriber that is covered by the advertisement described by this
   * publisher.
   * @throws UnsupportedOperationException  if the responsible messaging provider
   * does not support this operation.
   */
  public boolean isRelevant()
  {
//    if (param==null)
//      throw new UnsupportedOperationException("SubscriptionParameters missing");
//    return param.isRelevant(this);
    // FIXME!!!
    return true;
  }
  /**
   * Sets the sender object. The sole use of this concept is to associate
   * user data with this publisher object. If a ClientStub is connected to
   * this publisher, the sender object is set to the ClientStub.
   */
  public void setSender(Object obj)
  {
    sender=obj;
  }
  /**
   * Returns the sender object.
   */
  public Object getSender()
  {
    return sender;
  }
  public String toString()
  {
    return channel.toString();
  }

  private Channel channel;
  private boolean localLoopback;
  private SubscriptionParameters param;
  private Object sender;
}
