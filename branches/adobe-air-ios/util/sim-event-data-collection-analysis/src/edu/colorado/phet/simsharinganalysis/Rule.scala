// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis

// Copyright 2002-2011, University of Colorado
/**
 * @author Sam Reid
 */

case class Rule(actor: String, event: String, params: Map[String, String]) extends Match {

  def apply(entry: Entry) = entry.matches(actor, event, params)

  override def toString() = actor + "\t" + event + "\t" + ( for ( elm <- params ) yield {elm._1 + " = " + elm._2} ).mkString("\t")
}

case class LastEntryRule(log: Log) extends Match {
  def apply(v1: Entry) = log.entries.last == v1
}

object Rule {
  def apply(actor: String, event: String, params: Pair[String, String]*): Rule = Rule(actor, event, phet.toMap(params: _*))
}

case class ActorRule(actor: String, params: Map[String, String]) extends Match {
  def apply(entry: Entry) = entry.matches(actor, params)

  override def toString() = actor + "\t" + "<any>" + "\t" + ( for ( elm <- params ) yield {elm._1 + " = " + elm._2} ).mkString("\t")
}

trait Match extends ( Entry => Boolean )

class Or(a: Match, b: Match) extends Match {
  def apply(v1: Entry) = a(v1) || b(v1)
}