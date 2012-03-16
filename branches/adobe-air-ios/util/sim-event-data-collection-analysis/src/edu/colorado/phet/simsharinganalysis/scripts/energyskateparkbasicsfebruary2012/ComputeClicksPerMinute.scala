// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.energyskateparkbasicsfebruary2012

/**
 * @author Sam Reid
 */
object ComputeClicksPerMinute extends App {
  for ( key <- SplitClasses.classMap.keys ) {
    println("key = " + key + ", value = " + SplitClasses.classMap(key).length)
    val logs = SplitClasses.classMap(key)
    val userEvents = logs.map(log => log.entries.filter(_.messageType == ( "user" )).length)
    println("num user events: " + userEvents)
  }
}