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
case class Log(file: File, machineID: String, sessionID: String, serverTime: Long, entries: List[Entry]) {

  //TODO: also remove first window activated
  def getWithoutSystemEvents: Log = removeItems(_.actor.toLowerCase.startsWith("system"))

  def removeItems(rule: Entry => Boolean): Log = copy(entries = entries.filterNot(rule))

  def getLastTime = entries(entries.size - 1).time.asInstanceOf[Int]

  def getNumberOfEvents(time: Long, matches: Entry => Boolean = (entry: Entry) => true): Int = entries.filter(matches).count(_.time <= time)

  def keepItems(matches: Entry => Boolean): Log = copy(entries = entries.filter(matches))

  def size = entries.size

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

  def find(eventsOfInterest: ArrayList[Entry]): ArrayList[Entry] = {
    var list: ArrayList[Entry] = new ArrayList[Entry]
    import scala.collection.JavaConversions._
    for ( entry <- eventsOfInterest ) {
      if ( containsMatch(entry) ) {
        list.add(entry)
      }
    }
    list
  }

  def getServerStartTime: Long = {
    serverTime
  }

  def getOSName: String = {
    startMessage.apply("osName").getOrElse("?")
  }

  def getOSVersion: String = {
    startMessage.apply("osVersion").getOrElse("?")
  }

  def brief: String = startMessage.apply("name") + " " + startMessage.apply("version").get + " startTime = " +
                      new Date(serverTime) + ", epoch = " + serverTime + ", study = " + startMessage.apply("study") +
                      ", userID = " + startMessage.apply("id") + ", events = " + size + ", timeUsed = " + minutesUsed + " minutes, machineID = " + machineID + ", sessionID = " + sessionID

  def minutesUsed: Int = getLastTime / 1000 / 60

  def startMessage = getFirstEntry("system", "started")

  private def getFirstEntry(actor: String, event: String): Entry = {
    for ( line <- entries ) {
      if ( line.matches(actor, event, Map()) ) {
        return line
      }
    }
    null
  }

  def getID: String = startMessage.apply("id").getOrElse("None")

  def getSimName: String = startMessage.apply("name").get

  def getSimVersion: String = startMessage.apply("version").get

  def getMachineID: String = machineID

  def getSessionID: String = sessionID

  def getStudy: String = startMessage.apply("study").get

  val machine = getMachineID
  val simName = getSimName
  val epoch = getServerStartTime
  val simVersion = getSimVersion
  val study = getStudy
  val session = getSessionID
  val user = getID
  val startDate = new java.util.Date(epoch)
  val day = new SimpleDateFormat("MM-dd-yyyy").format(startDate)
  val lastTime = getLastTime
  val osName = getOSName
  val osVersion = getOSVersion

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

  //TODO: Remove duplicate
  def countEvents(until: Long): Int = getNumberOfEvents(until)

  def countMatches(matcher: Seq[Match], until: Long): Int = {
    val earlyEnoughEvents = entries.filter(_.time < until)
    matcher.filter(matchItem => earlyEnoughEvents.find(matchItem.matches(_)).isDefined).size
  }

  override def toString = simName + " " + new Date(epoch) + " (" + epoch + "), study = " + study + ", user = " + user + ", events = " + size + ", machine = " + machine + ", session = " + session

  lazy val histogramByObject = {
    val map = new HashMap[String, Int]
    for ( entry: Entry <- entries ) {
      val currentValue = map.getOrElse(entry.actor, 0)
      map.put(entry.actor, currentValue + 1)
    }
    map
  }

  def matches(entry: Match): Boolean = entries.filter(entry.matches(_)).size > 0

  def findMatches(matcher: Seq[Match]): List[Match] = ( for ( m <- matcher if matches(m) ) yield {
    m
  } ).toList

  def findEvents(matcher: Match): List[Entry] = entries.filter(matcher.matches(_)).toList

  def findEvents(actor: String): List[Entry] = entries.filter(ActorRule(actor, Map()).matches(_)).toList

  lazy val eventCountData = phet.timeSeries(this, countEvents(_))

  //  val importantEvents = simLogs.map(log => timeSeries(log, log.countMatches(simEventMap(sim), _)))
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