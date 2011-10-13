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

package org.mundo.util;

import java.io.File;

/**
 * An IDirectoryMonitor is the interface for monitoring directory for changes.
 * 
 * @author AHa
 */
public interface IDirectoryMonitor {

  /**
   * The IDirectoryModification interface defines the signals any class
   * implementing the IDirectoryMonitor interface may emit.
   */
  public interface IDirectoryModification {

    /**
     * The fileAdded method is called whenever a new filename is added to a
     * directory. Note that this may also be the case if a file is renamed.
     * 
     * @param directory the directory which the added file is in
     * @param file the file that was added
     */
    public void fileAdded(File directory, File file);

    /**
     * The fileRemoved method is called whenever a filename was removed from a
     * directory. Note that this may also be the case if a file is renamed.
     * 
     * @param directory the directory which the removed file was in
     * @param file the file that was removed - the file itself does not exist
     *              anymore
     */
    public void fileRemoved(File directory, File file);

    /**
     * The fileModified method is called whenever the lastModified() date of a
     * file has changed.
     * 
     * @param directory the directory which the modified file was in
     * @param file the file that was modified
     */
    public void fileModified(File directory, File file);

  }

}