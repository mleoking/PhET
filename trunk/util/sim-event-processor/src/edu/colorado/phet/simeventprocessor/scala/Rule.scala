package edu.colorado.phet.simeventprocessor.scala

import edu.colorado.phet.simeventprocessor.JavaEntry
import edu.colorado.phet.common.phetcommon.simsharing.Parameter

/**
 * @author Sam Reid
 */

case class Rule(actor: String, action: String, pair: Pair[String, String]*) extends Match {
  def matches(entry: JavaEntry) = entry.matches(actor, action, pair.map(s => new Parameter(s._1, s._2)).toArray)
}