// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import java.util.Date
import java.text.SimpleDateFormat
import collection.mutable.HashMap
import scala.collection.JavaConversions._
import edu.colorado.phet.simeventprocessor.{JavaEntry, EventLog}

/**
 * Adds scala-convenient interface for REPL.
 * @author Sam Reid
 */
class ScalaEventLog(log: EventLog) {
  val machine = log.getMachineID
  val simName = log.getSimName
  val epoch = log.getServerStartTime
  val simVersion = log.getSimVersion
  val study = log.getStudy
  val size = log.size
  val session = log.getSessionID
  val user = log.getID
  val startDate = new Date(epoch)
  val day = new SimpleDateFormat("MM-dd-yyyy").format(startDate)
  val lastTime = log.getLastTime

  def countEvents(until: Long) = log.getNumberOfEvents(until)

  override def toString = simName + " " + simVersion + " " + new Date(epoch) + " (" + epoch + "), study = " + study + ", user = " + user + ", events = " + size + ", machineID = " + machine + ", sessionID = " + session

  lazy val histogramByObject = {
    val map = new HashMap[String, Int]
    for ( elm: JavaEntry <- log ) {
      val currentValue = map.getOrElse(elm.actor, 0)
      map.put(elm.actor, currentValue + 1)
    }
    map
  }

  //  def find(eventsOfInterest: ScalaEventLog): ScalaEventLog = {
  //    val list: ScalaEventLog = new ScalaEventLog
  //    for ( entry <- eventsOfInterest ) {
  //      if ( containsMatch(entry) ) {
  //        list.add(entry)
  //      }
  //    }
  //    list
  //  }
}