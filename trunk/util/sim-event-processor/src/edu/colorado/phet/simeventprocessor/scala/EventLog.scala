// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import java.util.Date
import java.text.SimpleDateFormat
import collection.mutable.HashMap
import scala.collection.JavaConversions._
import edu.colorado.phet.simeventprocessor.{JavaEntry, JavaEventLog}
import java.lang.Boolean
import edu.colorado.phet.common.phetcommon.simsharing.Parameter

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
  val osName = log.getOSName
  val osVersion = log.getOSVersion

  def selectFirstTab(tab: String) = {
    val a = new JavaEventLog(log.file)

    var recording = true
    for ( entry <- entries ) {
      if ( recording ) {
        a.lines.add(entry)
      }
      if ( entry.matches("tab", "pressed", List(new Parameter("text", tab)).toArray) ) {
        recording = true
      }
      else if ( entry.matches("tab", "pressed") ) {
        recording = false
      }
    }
    new EventLog(a)
  }

  //Only looks for subsequent tabs, have to do something else for first tab
  def selectLaterTab(tab: String) = {
    val a = new JavaEventLog(log.file)

    //Add sim started message to get sim name etc
    a.lines.add(entries(0))

    var recording = false
    for ( entry <- entries ) {
      if ( recording ) {
        a.lines.add(entry)
      }
      if ( entry.matches("tab", "pressed", List(new Parameter("text", tab)).toArray) ) {
        recording = true
      }
      else if ( entry.matches("tab", "pressed") ) {
        recording = false
      }
    }
    new EventLog(a)
  }

  val entries = {
    ( for ( elm <- log ) yield {
      elm
    } ).toList
  }
  lazy val userNumber = {
    try {
      user.toInt
    }
    catch {
      case nfe: NumberFormatException => -1;
    }
  }

  //Millis
  lazy val firstUserEvent = entries.find(log => log.actor != "system" && log.actor != "window")

  def countEvents(until: Long): Int = log.getNumberOfEvents(until)

  def countMatches(matcher: Seq[Match], until: Long): Int = {
    val earlyEnoughEvents = log.filter(_.timeMilliSec < until)
    matcher.filter(matchItem => earlyEnoughEvents.find(matchItem.matches(_)).isDefined).size
  }

  override def toString = simName + " " + new Date(epoch) + " (" + epoch + "), study = " + study + ", user = " + user + ", events = " + size + ", machine = " + machine + ", session = " + session

  lazy val histogramByObject = {
    val map = new HashMap[String, Int]
    for ( elm: JavaEntry <- log ) {
      val currentValue = map.getOrElse(elm.actor, 0)
      map.put(elm.actor, currentValue + 1)
    }
    map
  }

  def matches(entry: Match): Boolean = log.filter(entry.matches(_)).size > 0

  def findMatches(matcher: Seq[Match]): List[Match] = ( for ( m <- matcher if matches(m) ) yield {
    m
  } ).toList

  def findEvents(matcher: Match): List[JavaEntry] = log.filter(matcher.matches(_)).toList

  def findEvents(actor: String): List[JavaEntry] = log.filter(ActorRule(actor).matches(_)).toList

  def foreach[U](f: ( JavaEntry ) => U) {
    for ( elm <- log ) {
      f(elm)
    }
  }

  lazy val eventCountData = phet.timeSeries(this, countEvents(_))

  //  val importantEvents = simLogs.map(log => timeSeries(log, log.countMatches(simEventMap(sim), _)))
  def countEvents(matcher: Seq[Match]) = phet.timeSeries(this, countMatches(matcher, _))

  private def containsParameters(e: JavaEntry, pairs: Seq[Pair[String, String]]): Boolean = {
    for ( p <- pairs ) {
      if ( !e.hasParameter(p._1, p._2) ) {
        return false
      }
    }
    true
  }

  def contains(actor: String, event: String, pairs: Pair[String, String]*) = log.find((e: JavaEntry) => e.actor == actor && e.event == event && containsParameters(e, pairs)).isDefined

}