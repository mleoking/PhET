// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis

import java.util.HashMap

// Copyright 2002-2011, University of Colorado
case class Entry(time: Long, //Time on the client computer when message was created
                 messageType: String,
                 component: String,
                 action: String,
                 parameters: Map[String, String]) {

  //Checks for a match for actor, event and optional params
  def matches(actor: String, event: String, params: Map[String, String]): Boolean = {
    for ( key <- params.keys ) {
      if ( !hasParameter(key, params(key)) ) {
        return false
      }
    }
    this.component == actor && this.action == event
  }

  lazy val parametersToHashMap = {
    val h = new HashMap[String, String]
    for ( k <- parameters.keys ) {
      h.put(k, parameters(k))
    }
    h
  }

  //Checks for a match for actor (event omitted) and optional params
  def matches(actor: String, params: Map[String, String]): Boolean = {
    for ( param <- params ) {
      if ( !hasParameter(param._1, param._2) ) {
        return false
      }
    }
    this.component == actor
  }

  //Get the specified parameter value, if it exists, otherwise "?"
  def apply(key: String): String = {
    if ( parameters.contains(key) ) {
      parameters(key)
    }
    else {
      "?"
    }
  }

  def hasParameter(key: String, value: String): Boolean = parameters.contains(key) && parameters(key) == value

  def hasParameters(e: Entry, pairs: Seq[Pair[String, String]]): Boolean = {
    for ( p <- pairs ) {
      if ( !hasParameter(p._1, p._2) ) {
        return false
      }
    }
    true
  }
}