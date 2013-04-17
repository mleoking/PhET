// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.util

/**
 * Utility class for passing to incremental reporters.
 * @author Sam Reid
 */
class MyStringBuffer {
  private var buffer = ""

  def println(s: String) {
    buffer = buffer + s + "\n"
  }

  override def toString = buffer
}