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
 * <p>An utility class for synchronization. Instances of
 * <code>IntegerMonitor</code> encapsulate an <code>int</code>
 * and allow thread-safe access to it.</p>
 *
 * <b>Example:</b>
 *
 * <pre>IntegerMonitor m=new IntegerMonitor();</pre>
 *
 * <b>Worker thread(s):</b>
 * <pre>
 * ...
 * m.add(1);
 * // use some resource, cleanup must not run in parallel
 * m.add(-1);
 * ...</pre>
 *
 * <b>Cleanup thread:</b>
 * <pre>
 * ...
 * synchronized(m)
 * {
 *   // synchronized(m) will prevent worker threads from executing
 *   // m.add() in parallel and workers can't pass the m.add(1)
 *   // statement while this synchronized-block is active.
 *   if (m.get()==0)
 *   {
 *     // cleanup resources, no worker thread must run in parallel
 *   }
 * }
 * ...</pre>
 *
 * @author Erwin Aitenbichler
 */
public class IntegerMonitor
{
  /**
   * Initializes an <code>IntegerMonitor</code> with the value 0.
   */
  public IntegerMonitor()
  {
    value=0;
  }
  /**
   * Initializes an <code>IntegerMonitor</code> with the specified value.
   * @param v  integer value.
   */
  public IntegerMonitor(int v)
  {
    value=v;
  }
  /**
   * Returns the stored value.
   * @return  integer value.
   */
  public synchronized int get()
  {
    return value;
  }
  /**
   * Changes the value.
   * @param v  integer value.
   * @return  the new value.
   */
  public synchronized int set(int v)
  {
    value=v;
    return value;
  }
  /**
   * Adds the specified value to the stored value.
   * @param v  integer value.
   * @return  the new value.
   */
  public synchronized int add(int v)
  {
    value+=v;
    return value;
  }
  private int value;
}
