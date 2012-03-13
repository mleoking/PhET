// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis

import java.util.HashMap
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager

// Copyright 2002-2011, University of Colorado
case class Entry(time: Long, //Time on the client computer when message was created
                 messageType: String,
                 component: String,
                 componentType: String,
                 action: String,
                 parameters: Map[String, String]) {

  //By default things are enabled, unless there is a flag that says otherwise, see #3218
  lazy val enabled = if ( parameters.contains("enabled") ) parameters("enabled").toBoolean else true

  lazy val parametersToString = parameters.keys.map(key => "" + key + " = " + parameters(key)).toList.mkString(SimSharingManager.DELIMITER)

  lazy val toPlainText = ( time :: messageType :: component :: componentType :: action :: parametersToString :: Nil ).mkString(SimSharingManager.DELIMITER)

  override lazy val toString = toPlainText

  lazy val interactive = if ( parameters.contains("interactive") ) parameters("interactive") else null

  def matches(component: String, action: String): Boolean = matches(component, action, Map())

  //Checks for a match for actor, event and optional params
  def matches(component: String, action: String, params: Map[String, String]): Boolean = {
    for ( key <- params.keys ) {
      if ( !hasParameter(key, params(key)) ) {
        return false
      }
    }
    this.component == component && this.action == action
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