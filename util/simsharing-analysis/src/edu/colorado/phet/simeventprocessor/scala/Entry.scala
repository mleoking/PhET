// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

case class Entry(time: Long, //Time since sim started in millisec
                 actor: String,
                 event: String,
                 parameters: Map[String, String]) {

  def matches(actor: String, event: String, params: Map[String, String]): Boolean = {
    for ( key <- params.keys ) {
      if ( !hasParameter(key, params(key)) ) {
        return false
      }
    }
    this.actor == actor && this.event == event
  }

  def matches(_actor: String, params: Map[String, String]): Boolean = {
    for ( param <- params ) {
      if ( !hasParameter(param._1, param._2) ) {
        return false
      }
    }
    actor == _actor
  }

  def apply(key: String): Option[String] = {
    if ( parameters.contains(key) ) {
      Some(parameters(key))
    }
    else {
      None
    }
  }

  def brief = actor + " " + event + ( if ( ( actor == "button node" ) ) {": " + apply("actionCommand")}
  else {""} )

  def hasParameter(key: String, value: String): Boolean = {
    for ( parameter <- parameters ) {
      if ( ( parameter._1 == key ) && ( parameter._2 == value ) ) {
        return true
      }
    }
    false
  }
}