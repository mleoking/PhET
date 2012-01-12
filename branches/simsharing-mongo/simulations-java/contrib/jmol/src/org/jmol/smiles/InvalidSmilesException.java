/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-05-11 15:14:03 -0700 (Tue, 11 May 2010) $
 * $Revision: 13069 $
 *
 * Copyright (C) 2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.smiles;

/**
 * Exception thrown for invalid SMILES String
 */
public class InvalidSmilesException extends Exception {

  private static String lastError;

  public static String getLastError() {
    return lastError;
  }
  
  public static void setLastError(String message) {
    lastError = message;
  }
  
  /**
   * Constructs a <code>InvalideSmilesException</code> without any detail.
   */
  public InvalidSmilesException() {
    super();
  }

  /**
   * Constructs a <code>InvalidSmilesException</code> with a detail message.
   * 
   * @param message The detail message.
   */
  public InvalidSmilesException(String message) {
    super(message);
    lastError = message;
    printStackTrace();
  }

  /**
   * Contructs a <code>InvalidSmilesException</code> with the specified cause and
   * a detail message of <tt>(cause == null ? null : cause.toString())</tt>
   * (which typically contains the class and detail message of <tt>cause</tt>).
   * 
   * @param cause The cause.
   */
  public InvalidSmilesException(Throwable cause) {
    super(cause);
    lastError = cause.getMessage();
  }

  /**
   * Construcst a <code>InvalidSmilesException</code> with the specified detail
   * message and cause.
   * 
   * @param message The detail message.
   * @param cause The cause.
   */
  public InvalidSmilesException(String message, Throwable cause) {
    super(message, cause);
    lastError = message + "\n" + cause.getCause();
  }
}
