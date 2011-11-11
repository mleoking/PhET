// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala


/**
 * @author Sam Reid
 */

case class Rule(actor: String, event: String, params: Map[String, String]) extends Match {

  def matches(entry: Entry) = entry.matches(actor, event, params)

  override def toString = actor + "\t" + event + "\t" + {
    ( for ( elm <- params ) yield {
      elm._1 + " = " + elm._2
    } ).mkString("\t")
  }
}

object Rule {
  def apply(actor: String, event: String, params: Pair[String, String]*): Rule = Rule(actor, event, phet.toMap(params: _*))
}

case class ActorRule(actor: String, params: Map[String, String]) extends Match {
  def matches(entry: Entry) = entry.matches(actor, params)

  override def toString = actor + "\t" + "<any>" + "\t" + {
    ( for ( elm <- params ) yield {
      elm._1 + " = " + elm._2
    } ).mkString("\t")
  }
}


trait Match {
  def matches(entry: Entry): Boolean
}