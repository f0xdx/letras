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
 * <p>Instances of <code>Subscriber</code> encapsulate subscriptions.
 * A subscription expresses the interest of a client in certain
 * messages. <code>Subscriber</code> objects are created via
 * <code>Session</code>s. In order to receive messages, an
 * application must perform the following steps:</p>
 *
 * <ul>
 *   <li>Create a <code>Subscriber</code> object via a <code>Session</code>.</li>
 *   <li>Call <code>setReceiver</code> to register the receiver callback.</li>
 *   <li>Call <code>setParam</code> to set subscription parameters,
 *       if applicable.</li>
 *   <li>Enable the subscription.</li>
 * </ul>
 * 
 * @see Session
 * @author Erwin Aitenbichler
 */
public class Subscriber
{
  /**
   * Initializes a subscriber with the specified channel.
   * @param c  the channel to subscribe to.
   */
  Subscriber(Channel c)
  {
    channel=c;
    enabled=true;
  }
  /**
   * Returns the channel this subscriber is subscribed to.
   * @return  the channel object.
   */
  public Channel getChannel()
  {
    return channel;
  }
  /**
   * Returns the session object this <code>Subscriber</code> belongs to.
   * @return  the session object.
   */
  public Session getSession()
  {
    return channel.getSession();
  }
  /**
   * Sets the receiver callback. After the subscription has been enabled,
   * the callback will be called each time a message is received from the
   * associated channel.
   * @param r  a receiver object that implements <code>IReceiver</code>.
   */
  public void setReceiver(IReceiver r)
  {
    receiver=r;
  }
  /**
   * Returns the receiver callback of the subscriber.
   * @return  the receiver callback.
   */
  public IReceiver getReceiver()
  {
    return receiver;
  }
  /**
   * Enables the subscriber.
   */
  public void enable()
  {
    Mundo.bcl.subscribe(this);
  }
  /**
   * Marks subscriber for deletion.
   */
  void disable()
  {
    enabled=false;
  }
  /**
   * Returns if the subscriber is enabled.
   * @return  <code>true</code> if the subscriber is enabled;
   *          <code>false</code> otherwise.
   */
  boolean isEnabled()
  {
    return enabled;
  }
  /**
   * Unsubscribes from the associated channel.
   */
  public void unsubscribe()
  {
    getChannel().getSession().unsubscribe(this);
  }
  /**
   * Sets subscription parameters.
   * <code>setParam</code> must be called before the subscription is enabled.
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
   * Forwards a received message to the registered callback.
   * @param msg  the message to forward.
   * @param ctx  the message context.
   */
  void dispatch(Message msg, MessageContext ctx)
  {
    if (receiver!=null)
    {
      // Protect the dispatching thread from unhandled runtime exceptions.
      try
      {
        receiver.received(msg, ctx);
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
    }
  }
  public String toString()
  {
    return channel.toString();
  }

  private boolean enabled;
  private Channel channel;
  private IReceiver receiver;
  private SubscriptionParameters param;
}
