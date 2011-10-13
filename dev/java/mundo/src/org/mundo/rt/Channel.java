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

import org.mundo.rt.Session;
// FIXME: there should not be package dependencies from rt->net
import org.mundo.net.ProtocolCoordinator;

import java.util.Vector;

/**
 * <p>A <em>channel</em> is the object a client uses to specify the target of
 * messages it produces and the source of messages it consumes. Instances
 * of <code>Channel</code> are either used like addresses to identify
 * channels or encapsulate Session-specific channel objects.</p>
 *
 * <p>Zones limit the scope of message distribution. Currently, only the
 * following two zones are supported:
 * <ul>
 *   <li><code>"rt"</code> - The scope is limited to the local runtime environment.
 *       Messages are not forwarded by message routing services to remote
 *       processes.
 *   <li><code>"lan"</code> - Messages are distributed to all nodes in the
 *       overlay network.
 * </ul>
 *
 * @author Erwin Aitenbichler
 */
public class Channel
{
  /**
   * Initializes the <code>Channel</code> object with a zone and a channel
   * name. The object is not bound to a specific <code>Session</code> and
   * can be used to address a channel.
   * @param zone  the zone name.
   * @param name  the channel name.
   */
  public Channel(String zone, String name)
  {
    this.zone=zone;
    this.name=name;
  }

  /**
   * Initializes the <code>Channel</code> object with a zone and a channel
   * name that is bound to the specified session.
   */
  Channel(Session session, String zone, String name)
  {
    this.session=session;
    this.zone=zone;
    this.name=name;
    stack=ProtocolCoordinator.getInstance().getDefaultStack();
  }

  /**
   * Returns a hash code for this object.
   * @return   a hash code value for this object.
   */
  public int hashCode()
  {
    return name.hashCode();
  }

  /**
   * Compares this <code>Channel</code> to the specified object. The result
   * is true if and only if the argument is not null and is a <code>Channel</code>
   * object that contains the same channel name.
   * @param o   the object to compare this <code>Channel</code> against.
   * @return    <code>true</code> if the <code>Channel</code>s are equal;
   *            <code>false</code> otherwise.
   */
  public boolean equals(Object o)
  {
    try
    {
      return name.equals(((Channel)o).name);
//    return zone.equals(((Channel)o).zone) && name.equals(((Channel)o).name);
    }
    catch(Exception x)
    {
    }
    return false;
  }

  /**
   * Tests if this subscription covers the specified advertisement. The
   * result is true if the channel names match or the channel name in the
   * subscription is null. In this case, the subscription covers all
   * advertisements.
   * @param c   the <code>Channel</code> object representing the advertisement
   * @return    <code>true</code> if the subscription covers the advertisement;
   *            <code>false</code> otherwise.
   */
  public boolean covers(Channel c)
  {
//    if (!(zone.equals("rt") || zone.equals(c.zone)))
//      return false;
    if (name.equals("*"))
      return true;
    return name.equals(c.name);
  }

  /**
   * Returns the channel name.
   * @return   the channel name contained in a <code>String</code>.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the zone name.
   * @return   the zone name contained in a <code>String</code>.
   */
  public String getZone()
  {
    return zone;
  }

  /**
   * Returns the session the <code>Channel</code> object is bound to.
   * @return   the reference to the <code>Session</code> object to which
   *           this <code>Channel</code> object is bound, or <code>null</code>
   *           if the <code>Channel</code> is not bound to a <code>Session</code>.
   */
  public Session getSession()
  {
    return session;
  }

  /**
   * Binds this <code>Channel</code> object to the specified <code>Session</code>.
   * @param s  the reference to the <code>Session</code> object to bind
   *           this <code>Channel</code> object to.
   */
  public void setSession(Session s)
  {
    session=s;
  }
  /**
   * Returns the protocol stack for this channel. The returned object must not
   * be changed by the caller.
   */
  public ProtocolStack getStack()
  {
    return stack;
  }
  /**
   * Sets the protocol stack for this channel.
   */
  public void setStack(ProtocolStack s)
  {
    stack=s;
  }
  /**
   * Sets the protocol stack for this channel.
   */
  public void setStack(String name)
  {
    stack = ProtocolCoordinator.getInstance().getStack(name);
  }

  /**
   * Returns a string representation of the object.
   * @return  zoneName + ":" + channelName
   */
  public String toString()
  {
    return zone+":"+name;
  }

  private Session session;
  private String name;
  private String zone;
  private ProtocolStack stack;
}
