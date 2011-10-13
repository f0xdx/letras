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

/**
 * A SyntaxErrorException is thrown if a document has syntax errors in it. It
 * features the standard Exception constructurs.
 * 
 * @author AHa
 */
public class SyntaxErrorException extends Exception {

  private static final long serialVersionUID = 8706768466993184473L;

  public SyntaxErrorException() {
    super();
  }

  public SyntaxErrorException(String description) {
    super(description);
  }

  public SyntaxErrorException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public SyntaxErrorException(Throwable arg0) {
    super(arg0);
  }
}