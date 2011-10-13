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

package org.mundo.agent;

import java.io.FileInputStream;
import java.io.IOException;

import org.mundo.rt.Blob;
import org.mundo.rt.DoObject;
import org.mundo.rt.GUID;
import org.mundo.rt.Logger;
import org.mundo.rt.Service;
import org.mundo.rt.Session;
import org.mundo.rt.TypedMap;
import org.mundo.rt.Mundo;
import org.mundo.service.DoIServiceManager;
import org.mundo.service.Node;
import org.mundo.service.ServiceManager;

/**
 * A mobile agent is a service that can move autonomously through the network.
 * Concrete agent implementations derive from this class.
 * The agent class supports the following functionalities:
 * <ul>
 * <li><b>Code Migration:</b> A concrete implementation of an agent must be
 * packaged into a plug-in component. When the agent is migrated to a new
 * location, the associated plug-in will be sent over to the new node and
 * be deployed there, dynamically at runtime.</li>
 * <li><b>State Migration:</b> Agents transport their internal state between
 * different nodes by means of object serialization.</li>
 * </ul>
 * @author erwin
 */
public class Agent extends Service implements IMobility
{
  /**
   * Instantiates a new agent. Because agent classes come from plug-ins,
   * they can only be constructed with such reflection-style calls.
   * @param clientSession  specifies the caller-side session for the
   * distributed object that will be returned.
   * @param className  specifies the class name of the agent to create.
   * @return a remote reference (distributed object) to the newly created agent.
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public static DoIMobility newInstance(Session clientSession, String className)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    return newInstance(clientSession, className, "agent");
  }
  /**
   * Instantiates a new agent. Because agent classes come from plug-ins,
   * they can only be constructed with such reflection-style calls.
   * @param clientSession  specifies the caller-side session for the
   * distributed object that will be returned.
   * @param className  specifies the class name of the agent to create.
   * @param instanceName  specifies the service instance name of the
   * agent to create.
   * @return a remote reference (distributed object) to the newly created agent.
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public static DoIMobility newInstance(Session clientSession, String className, String instanceName)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    DoObject dobj = ServiceManager.getInstance().newInstance(className, instanceName, null);
    // FIXME: this is a hack to detach the proxy from the local object
    return new DoIMobility(clientSession.getChannel("lan", dobj._getChannelName()));
//    return new DoIMobility(dobj);
  }
  /**
   * Moves the agent to the specified node.
   * @param nodeName  specifies the node name of the agent's destination.
   */
  public void moveTo(String nodeName)
  {
    moveTo(nodeName, null);
  }
  /**
   * Moves the agent to the specified node and continues the execution
   * with the specified method.
   * @param nodeName  specifies the node name of the agent's destination.
   * @param mtdName  specifies the name of the method of the agent that
   * should be invoked immediately after the agent has been migrated and
   * resumed at the new location.
   */
  public void moveTo(String nodeName, String mtdName)
  {
    if (mtdName==null)
      log.info("moveTo: " + nodeName);
    else
      log.info("moveTo: " + nodeName + " and continue: " + mtdName);
    // already on the right node?
    if (nodeName.equals(Mundo.getNodeName()))
    {
      log.fine("already on target node");
      return;
    }
    Node node = null;
    int i;
    try
    {
      for (i=0; i<10; i++)
      {
        node = Node.getByName(nodeName);
        if (node != null)
          break;
        Thread.sleep(100);
      }
    }
    catch(InterruptedException x) {}
    if (node == null)
    {
      log.warning("unknown node: " + nodeName);
      throw new IllegalArgumentException("unknown node: " + nodeName);
    }
    
    ServiceManager.Plugin plugin = ServiceManager.getInstance().getPlugin(this);
    if (plugin == null)
    {
      log.severe("service to move must come from a plug-in");
      throw new IllegalArgumentException("Service to move must come from a plug-in");
    }

    GUID digest;
    try {
      digest = plugin.getDigest();
    } catch(IOException x) {
      throw new IllegalStateException("get plugin digest failed", x);
    }
    log.fine("digest: " + digest);

//    String pluginFN = plugin.getFilename();
//    Blob code = new Blob();
//    try {
//      FileInputStream fis = new FileInputStream(plugin.getPathname());
//      code.readFrom(fis);
//      fis.close();
//      log.fine("read plugin: " + pluginFN + " (" + code.size() + " bytes)");
//    } catch(IOException x) {
//      log.severe("can't read plugin: " + pluginFN);
//      throw new IllegalStateException("can't read plugin", x);
//    }
    
    DoIServiceManager remoteSvcMan = node.getServiceManager(ServiceManager.getInstance().getSession());
//    System.out.println(remoteSvcMan);
//    try {
//      remoteSvcMan.uploadFile(pluginFN, code);
//    } catch(IOException x) {
//      if (!x.getMessage().contains("file already exists"))
//      {
//        log.warning("remote node did not accept code upload");
//        throw new IllegalStateException("remote node did not accept code upload", x);
//      }
//    }
    
    TypedMap state = new TypedMap();
    try {
      suspend(state);
    } catch(Exception x) {
      log.exception(x);
      throw new IllegalStateException("error suspending service", x);
    }
    Mundo.unregisterMovedService(this);

    state.putGUID("sourceNodeId", Mundo.getNodeId());
    state.putString("pluginName", plugin.getFilename());
    state.putGUID("pluginDigest", digest);
    if (mtdName != null)
    {
      TypedMap call = new TypedMap();
      call.putString("method", mtdName);
      state.put("postResume", call);
    }
    
    try {
      remoteSvcMan.resumeService(state);
    } catch(Exception x) {
      log.exception(x);
      throw new IllegalStateException("error resuming service", x);
    }

    // Check if the agent is trying to move itself
    String cn = getClass().getName();
//    System.out.println("this class: " + cn);
    StackTraceElement[] st = Thread.currentThread().getStackTrace();
    for (i=1; i<st.length; i++)
    {
      if (st[i].getClassName().equals(cn))
        throw new MobilityException("moving self");
    }
  }

  private Logger log = Logger.getLogger("agent");
}
