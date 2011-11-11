// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import java.text.SimpleDateFormat
import java.util.{ArrayList, Date}
import java.io.File
import collection.mutable.{ArrayBuffer, HashMap}

/**
 * Adds scala-convenient interface for REPL.
 * @author Sam Reid
 */
case class Log(file: File, machine: String, session: String, epoch: Long, entries: List[Entry]) {

  val startMessage = getFirstEntry("system", "started")
  val study = startMessage.apply("study").get
  val simName = startMessage.apply("name").get
  val simVersion = startMessage.apply("version").get
  val user = startMessage.apply("id").getOrElse("None")
  val startDate = new java.util.Date(epoch)
  val day = new SimpleDateFormat("MM-dd-yyyy").format(startDate)
  val lastTime = getLastTime
  val osName = startMessage.apply("osName").getOrElse("?")
  val osVersion = startMessage.apply("osVersion").getOrElse("?")
  val size = entries.size
  lazy val eventCountData = phet.timeSeries(this, countEvents(_))
  lazy val userNumber = {
    try {
      user.toInt
    }
    catch {
      case nfe: NumberFormatException => -1;
    }
  }
  lazy val minutesUsed: Int = getLastTime / 1000 / 60
  lazy val brief: String = startMessage.apply("name") + " " + startMessage.apply("version").get + " startTime = " +
                           new Date(epoch) + ", epoch = " + epoch + ", study = " + startMessage.apply("study") +
                           ", userID = " + startMessage.apply("id") + ", events = " + size + ", timeUsed = " + minutesUsed + " minutes, machineID = " + machine + ", sessionID = " + session

  override def toString = simName + " " + new Date(epoch) + " (" + epoch + "), study = " + study + ", user = " + user + ", events = " + size + ", machine = " + machine + ", session = " + session

  lazy val histogramByObject = {
    val map = new HashMap[String, Int]
    for ( entry: Entry <- entries ) {
      val currentValue = map.getOrElse(entry.actor, 0)
      map.put(entry.actor, currentValue + 1)
    }
    map
  }

  //Millis
  lazy val firstUserEvent = entries.find(log => log.actor != "system" && log.actor != "window")

  //TODO: also remove first window activated
  def getWithoutSystemEvents: Log = removeItems(_.actor.toLowerCase.startsWith("system"))

  def removeItems(rule: Entry => Boolean): Log = copy(entries = entries.filterNot(rule))

  def getLastTime = entries(entries.size - 1).time.asInstanceOf[Int]

  def getNumberOfEvents(time: Long, matches: Entry => Boolean = (entry: Entry) => true): Int = entries.filter(matches).count(_.time <= time)

  def keepItems(matches: Entry => Boolean): Log = copy(entries = entries.filter(matches))

  def getNumberOfEvents(time: Long, eventsOfInterest: ArrayList[Entry]): Int = {
    val log: Log = removeItems(new Function1[Entry, Boolean] {
      def apply(entry: Entry): Boolean = {
        entry.time > time
      }
    })
    val user: Log = log.getWithoutSystemEvents
    var count: Int = 0
    import scala.collection.JavaConversions._
    for ( eventOfInterest <- eventsOfInterest ) {
      if ( user.containsMatch(eventOfInterest) ) {
        ( {count += 1; count} )
      }
    }
    count
  }

  private def containsMatch(entry: Entry): Boolean = {
    for ( line <- entries ) {
      if ( line.matches(entry.actor, entry.event, entry.parameters) ) {
        true
      }
    }
    false
  }

  def find(eventsOfInterest: List[Entry]) = {
    val array = new ArrayBuffer[Entry]
    for ( entry <- eventsOfInterest ) {
      if ( containsMatch(entry) ) {
        array += entry
      }
    }
    array.toList
  }

  private def getFirstEntry(actor: String, event: String): Entry = {
    for ( line <- entries ) {
      if ( line.matches(actor, event, Map()) ) {
        return line
      }
    }
    null
  }

  def selectFirstTab(tab: String) = {
    val a = new ArrayBuffer[Entry]

    var recording = true
    for ( entry <- entries ) {
      if ( recording ) {
        a += entry
      }
      if ( entry.matches("tab", "pressed", Map("text" -> tab)) ) {
        recording = true
      }
      else if ( entry.matches("tab", "pressed", Map()) ) {
        recording = false
      }
    }
    copy(entries = a.toList)
  }

  //Only looks for subsequent tabs, have to do something else for first tab
  def selectLaterTab(tab: String) = {
    val a = new ArrayBuffer[Entry]

    //Add sim started message to get sim name etc
    a += entries(0)

    var recording = false
    for ( entry <- entries ) {
      if ( recording ) {
        a += entry
      }
      if ( entry.matches("tab", "pressed", Map("text" -> tab)) ) {
        recording = true
      }
      else if ( entry.matches("tab", "pressed", Map()) ) {
        recording = false
      }
    }
    copy(entries = a.toList)
  }

  //TODO: Remove duplicate
  def countEvents(until: Long): Int = getNumberOfEvents(until)

  def countMatches(matcher: Seq[Match], until: Long): Int = {
    val earlyEnoughEvents = entries.filter(_.time < until)
    matcher.filter(matchItem => earlyEnoughEvents.find(matchItem(_)).isDefined).size
  }

  def matches(criteria: Match) = entries.filter(criteria(_)).size > 0

  def findMatches(matcher: Seq[Match]) = matcher.filter(matches(_)).toList

  def findEvents(criteria: Match): List[Entry] = entries.filter(criteria(_)).toList

  def findEvents(actor: String): List[Entry] = entries.filter(ActorRule(actor, Map()).apply(_)).toList

  def countEvents(matcher: Seq[Match]) = phet.timeSeries(this, countMatches(matcher, _))

  private def containsParameters(e: Entry, pairs: Seq[Pair[String, String]]): Boolean = {
    for ( p <- pairs ) {
      if ( !e.hasParameter(p._1, p._2) ) {
        return false
      }
    }
    true
  }

  def contains(actor: String, event: String, pairs: Pair[String, String]*) = entries.find((e: Entry) => e.actor == actor && e.event == event && containsParameters(e, pairs)).isDefined
}