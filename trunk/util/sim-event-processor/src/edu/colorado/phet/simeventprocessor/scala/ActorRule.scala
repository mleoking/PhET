package edu.colorado.phet.simeventprocessor.scala

import edu.colorado.phet.simeventprocessor.JavaEntry
import edu.colorado.phet.common.phetcommon.simsharing.Parameter

/**
 * @author Sam Reid
 */

case class ActorRule(actor: String, pair: Pair[String, String]*) extends Match {
  def matches(entry: JavaEntry) = entry.matches(actor, pair.map(s => new Parameter(s._1, s._2)).toArray)

  override def toString = actor + "\t" + "<any>"+ "\t" + {
    ( for ( elm <- pair ) yield {
      elm._1 + " = " + elm._2
    } ).mkString("\t")
  }
}