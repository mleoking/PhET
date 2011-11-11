// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import java.text.SimpleDateFormat
import java.util.Date
import java.io.File
import collection.mutable.{ArrayBuffer, HashMap}

/**
 * Adds scala-convenient interface for REPL.
 * @author Sam Reid
 */
case class Log(file: File, machine: String, session: String, epoch: Long, entries: List[Entry]) {

  val startMessage = getFirstEntry("system", "started")
  val study = startMessage("study")
  val simName = startMessage("name")
  val simVersion = startMessage("version")
  val user = startMessage("id")
  val startDate = new java.util.Date(epoch)
  val day = new SimpleDateFormat("MM-dd-yyyy").format(startDate)
  val osName = startMessage("osName")
  val osVersion = startMessage("osVersion")
  val size = entries.size
  val lastTime = entries(entries.size - 1).time.asInstanceOf[Int]
  lazy val minutesUsed: Int = lastTime / 1000 / 60
  lazy val eventCountData = phet.timeSeries(this, countEvents(_))
  lazy val firstUserEvent = entries.find(log => log.actor != "system" && log.actor != "window") //Millis
  lazy val userNumber = {
    try {
      user.toInt
    }
    catch {
      case nfe: NumberFormatException => -1;
    }
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

  def matches(criteria: Match) = entries.filter(criteria(_)).size > 0

  def findMatches(matcher: Seq[Match]) = matcher.filter(matches(_)).toList

  def findEvents(criteria: Match): List[Entry] = entries.filter(criteria(_)).toList

  def findEvents(actor: String): List[Entry] = entries.filter(ActorRule(actor, Map()).apply(_)).toList

  def countEvents(matcher: Seq[Match]) = phet.timeSeries(this, countMatches(matcher, _))

  def contains(actor: String, event: String, pairs: Pair[String, String]*) = entries.find((e: Entry) => e.actor == actor && e.event == event && e.hasParameters(e, pairs)).isDefined

  //TODO: also remove first window activated
  def getWithoutSystemEvents: Log = removeItems(_.actor.toLowerCase.startsWith("system"))

  def removeItems(rule: Entry => Boolean): Log = copy(entries = entries.filterNot(rule))

  def countEvents(time: Long, matches: Entry => Boolean = (entry: Entry) => true): Int = entries.filter(matches).count(_.time <= time)

  def keepItems(matches: Entry => Boolean): Log = copy(entries = entries.filter(matches))

  private def matchesEntry(e: Entry): Boolean = entries.find(_.matches(e.actor, e.event, e.parameters)).isDefined

  def find(all: List[Entry]) = all.filter(matchesEntry(_))

  private def getFirstEntry(actor: String, event: String): Entry = entries.find(entry => entry.actor == actor && entry.event == event).getOrElse(null)

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

  def countMatches(matcher: Seq[Match], until: Long): Int = {
    val earlyEvents = entries.filter(_.time < until)
    matcher.filter(matchItem => earlyEvents.find(entry => entry.time < until && matchItem(entry)).isDefined).size
  }
}