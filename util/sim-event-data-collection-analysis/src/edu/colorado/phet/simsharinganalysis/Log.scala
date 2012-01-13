// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis

// Copyright 2002-2011, University of Colorado

import java.text.SimpleDateFormat
import java.util.Date
import java.io.File
import collection.mutable.{ArrayBuffer, HashMap}
import collection.immutable.List
import java.lang.String

/**
 * Adds scala-convenient interface for REPL.
 * @author Sam Reid
 */
case class Log(file: File, machine: String, session: String, epoch: Long, entries: List[Entry]) {
  val startMessage = getFirstEntry("system", "simsharingManager", "started")
  val study = startMessage("study")
  val simName = startMessage("name")
  val javaVersion = startMessage("javaVersion")
  val simVersion = startMessage("version")
  val user = startMessage("id")
  val date = new Date(epoch)
  val day = new SimpleDateFormat("MM-dd-yyyy").format(date)
  val osName = startMessage("osName")
  val osVersion = startMessage("osVersion")
  val size = entries.size
  val lastTime = entries(entries.size - 1).time.asInstanceOf[Int] //Time since the beginning of the sim to the last event
  val endEpoch = epoch + lastTime.toLong
  lazy val minutesUsed: Int = ( lastTime / 1000L / 60L ).toInt
  lazy val eventCountData = phet.timeSeries(this, countEvents(_))
  lazy val firstUserEvent = entries.find(log => log.component != "system" && log.component != "window") //Millis
  lazy val userNumber = {
    try {
      user.toInt
    }
    catch {
      case nfe: NumberFormatException => -1;
    }
  }

  //Determine if the sim was running at the specified server time
  def running(time: Long) = time >= epoch && time <= epoch + lastTime

  override def toString = simName + " " + new Date(epoch) + " (" + epoch + "), study = " + study + ", user = " + user + ", events = " + size + ", machine = " + machine + ", session = " + session

  lazy val histogramByObject = {
    val map = new HashMap[String, Int]
    for ( entry: Entry <- entries ) {
      val currentValue = map.getOrElse(entry.component, 0)
      map.put(entry.component, currentValue + 1)
    }
    map
  }

  def matches(criteria: Match) = entries.filter(criteria(_)).size > 0

  def findMatches(matcher: Seq[Match]) = matcher.filter(matches(_)).toList

  def findEvents(criteria: Match): List[Entry] = entries.filter(criteria(_)).toList

  def findEvents(actor: String): List[Entry] = entries.filter(_.component == actor)

  def countEvents(matcher: Seq[Match]) = phet.timeSeries(this, countMatches(matcher, _))

  def contains(actor: String, event: String, pairs: Pair[String, String]*) = entries.find((e: Entry) => e.component == actor && e.action == event && e.hasParameters(e, pairs)).isDefined

  def countEvents(time: Long, matches: Entry => Boolean = (entry: Entry) => true): Int = entries.filter(matches).count(_.time <= time)

  private def matchesEntry(e: Entry): Boolean = entries.find(_.matches(e.component, e.action, e.parameters)).isDefined

  def find(all: List[Entry]) = all.filter(matchesEntry(_))

  private def getFirstEntry(messageType: String, obj: String, action: String): Entry = entries.find(entry => entry.messageType == messageType &&
                                                                                                             entry.component == obj &&
                                                                                                             entry.action == action).getOrElse(null)

  //Find the first pair of entries that matches the specified pairs
  def findFirstEntryIndices(startMatch: Match, endMatch: Match, startIndex: Int): Option[Pair[Int, Int]] = {
    val start = entries.indexWhere(startMatch, startIndex)
    val end = entries.indexWhere(endMatch, start + 1)

    if ( start >= 0 && end >= 0 ) {
      Some((start, end))
    }
    else {
      None
    }
  }

  //Allow overlapping ranges?
  def getEntryRanges(startMatch: Match, endMatch: Match): List[Pair[Int, Int]] = {
    val list = new ArrayBuffer[Pair[Int, Int]]
    var x = findFirstEntryIndices(startMatch, endMatch, 0)
    while ( x.isDefined ) {
      val result: Pair[Int, Int] = x.get
      list += result

      x = findFirstEntryIndices(startMatch, endMatch, result._1 + 1)
    }

    list.toList
  }

  //Choose events occurring just in the specified tab (from the list of available tabs)
  def selectTab(list: List[String], s: String) = {
    if ( list.head == s ) {selectFirstTab(s)}
    else {selectLaterTab(s)}
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
    a.toList
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
    a.toList
  }

  def countMatches(matcher: Seq[Match], until: Long): Int = {
    val earlyEvents = entries.filter(_.time < until)
    matcher.filter(matchItem => earlyEvents.find(entry => entry.time < until && matchItem(entry)).isDefined).size
  }
}