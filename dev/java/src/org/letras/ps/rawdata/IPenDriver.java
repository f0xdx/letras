/*******************************************************************************
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
 * Department of Computer Science, Technische Universität Darmstadt.
 * Portions created by the Initial Developer are
 * Copyright © 2009-2011 the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 * Felix Heinrichs
 * Niklas Lochschmidt
 * Jannik Jochem
 ******************************************************************************/
package org.letras.ps.rawdata;

/**
 * This interface must be provided by the main class of each pen driver. 
 * Its implementation has to be specified as pen driver main class in the 
 * pen driver manifest. It is this implementation, that will be instantiated 
 * by the PenDriverManager in order to fire up a new pen driver.
 * <P>
 * Implementing classes need to provide a nullary constructor in order to
 * support instantiation. However, it might obtain needed properties in form
 * of a <code>org.mundo.rt.TypedMap</code> and set it parameters accordingly.
 * Refer to the <code>plugin.xml</code> description for details.
 * <P>
 * A driver is usually packaged as a jar-file containing one main class
 * implementing this interface, which is specified as such in the 
 * <code>plugin.xml</code> file. Details can be found in the wiki documentation.
 * If a pen driver needs configuration options (properties), these options
 * might be passed using the same mechanism as a mundo service: by implementing
 * the <code>IConfigure</code> interface. Such options are passed as map inside
 * the driver xml file.
 * <P>
 * The contract for the procedure to fire up a driver is as follows:
 * <ol>
 *  <li> The driver is instantiated using its nullary constructor
 *  <li> If the driver implements the <code>org.mundo.service.IConfigure</code> 
 *  interface and the drivers <code>plugin.xml</code> file provides configuration
 *  data, this data is passed as a <code>TypedMap</code> to the driver (via a call
 *  to the <code>setServiceConfig()</code> method)
 *  <li> The appropriate <code>IPenAdapterFactory</code> is set via a call to the
 *  <code>inject()</code> method
 *  <li> The <code>init()</code> method is called -- from now on the driver will
 *  be expected to work properly
 * </ol>
 * To shutdown a running pen driver, the <code>shutdown()</code> method will be
 * called before deleting any remaining references and waiting for the garbage
 * collector to set in.
 * 
 * @author felix_h
 * @version 0.0.1
 *
 */
public interface IPenDriver {
	
	/**
	 * Injection method invoked by the pen driver manager to set
	 * the currently used pen adapter factory. The contract is that this
	 * method will be called before the <code>init()</code> method will be
	 * called. So upon initialization, the pen driver will already know
	 * the factory it should use.
	 * 
	 * <b>NOTE</b> As of version v0.1.x of this interface, no runtime change
	 * in the used factory is foreseen, so the inject method will be called only
	 * once when the driver is initialized
	 * 
	 * @param factory the pen adapter factory to use for obtaining pen adapters
	 */
	public void inject(IPenAdapterFactory factory);
	
	/**
	 * Called to initialize the pen driver. The contract is that this method will be
	 * called after the pen adapter factory has been injected, to allow the pen
	 * driver to setup its needed classes. If this pen driver also implements the
	 * {@link org.mundo.service.IConfigure} interface, the <code>setServiceConfig()</code>
	 * will also be called prior to this init method, i.e. the implementor will have
	 * the configuration options readily available whenever the init method is called.
	 */
	public void init();
	
	/**
	 * Called to finalize the pen driver. Prior to shutting down the pen driver, this
	 * method will be called in order to allow the pen driver to finalize its used 
	 * resources and shut them down cleanly. After calling this method the framework
	 * will delete the reference to the pen driver.
	 */
	public void shutdown();
}
