package edu.colorado.phet.simeventprocessor.scala

import edu.colorado.phet.simeventprocessor.JavaEntry

/**
 * @author Sam Reid
 */

trait Match {
  def matches(entry: JavaEntry): Boolean
}