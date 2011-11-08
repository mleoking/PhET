// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import java.util.Date
import java.text.SimpleDateFormat
import collection.mutable.HashMap
import scala.collection.JavaConversions._
import edu.colorado.phet.simeventprocessor.{JavaEntry, JavaEventLog}
import edu.colorado.phet.common.phetcommon.util.function.Function1
import java.lang.Boolean

/**
 * Adds scala-convenient interface for REPL.
 * @author Sam Reid
 */
class EventLog(log: JavaEventLog) {
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

  def countEvents(until: Long):Int = log.getNumberOfEvents(until)

  def countMatches(matcher:Seq[Match], until:Long):Int = {
    val earlyEnoughEvents = log.filter(_.timeMilliSec < until)
    matcher.filter( matchItem => earlyEnoughEvents.find( matchItem.matches( _ )).isDefined).size
  }

  override def toString = simName + " " + simVersion + " " + new Date(epoch) + " (" + epoch + "), study = " + study + ", user = " + user + ", events = " + size + ", machineID = " + machine + ", sessionID = " + session

  lazy val histogramByObject = {
    val map = new HashMap[String, Int]
    for ( elm: JavaEntry <- log ) {
      val currentValue = map.getOrElse(elm.actor, 0)
      map.put(elm.actor, currentValue + 1)
    }
    map
  }

  def matches(entry:Match):Boolean = log.filter(entry.matches(_)).size>0

  def findMatches(matcher: Seq[Match]):List[Match]= (for ( m <- matcher if matches(m) ) yield m).toList

  def findEvents(matcher:Match):List[JavaEntry] = log.filter( matcher.matches(_)).toList
  def findEvents(actor:String):List[JavaEntry] = log.filter( ActorRule(actor).matches(_)).toList
}