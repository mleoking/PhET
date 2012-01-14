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
case class Log(file: File, machine: String, session: String, entries: List[Entry]) {
  val startMessage = getFirstEntry("system", "simsharingManager", "started")
  val study = startMessage("study")
  val simName = startMessage("name")
  val javaVersion = startMessage("javaVersion")
  val simVersion = startMessage("version")
  val user = startMessage("id")
  val startTime = startMessage("time").toLong //Time on client machine of last event
  val endTime = entries(entries.size - 1).time //Time on client machine of last event
  val date = new Date(startTime)
  val day = new SimpleDateFormat("MM-dd-yyyy").format(startTime)
  val osName = startMessage("osName")
  val osVersion = startMessage("osVersion")
  val size = entries.size
  lazy val minutesUsed: Double = ( ( endTime - startTime ) / 1000.0 / 60.0 ).toDouble
  lazy val eventCountData = phet.timeSeries(this, countEntries(_))
  lazy val firstUserEvent = entries.find(log => log.component != "system" && log.component != "window") //Millis
  lazy val userNumber = {
    try {
      user.toInt
    }
    catch {
      case nfe: NumberFormatException => -1;
    }
  }

  //Determine if the sim was running at the specified server time. TODO, we need to sync with server clock, all times are client log times
  def running(time: Long) = time >= startTime && time <= endTime + endTime

  override def toString = simName + " " + new Date(startTime) + " (" + startTime + "), study = " + study + ", user = " + user + ", events = " + size + ", machine = " + machine + ", session = " + session

  lazy val histogramByObject = {
    val map = new HashMap[String, Int]
    for ( entry: Entry <- entries ) {
      val currentValue = map.getOrElse(entry.component, 0)
      map.put(entry.component, currentValue + 1)
    }
    map
  }

  def matches(criteria: Match) = entries.filter(criteria(_)).size > 0

  def findEntries(matcher: Seq[Match]) = matcher.filter(matches(_)).toList

  def filter(criteria: Entry => Boolean): List[Entry] = entries.filter(criteria(_)).toList

  lazy val userEntries = filter(e => e.messageType == "user")
  lazy val systemEntries = filter(e => e.messageType == "system")
  lazy val modelEntries = filter(e => e.messageType == "model")

  def findEntries(actor: String): List[Entry] = entries.filter(_.component == actor)

  def countEntries(matcher: Seq[Match]) = phet.timeSeries(this, countMatches(matcher, _))

  def countEntries(time: Long, matches: Entry => Boolean = (entry: Entry) => true): Int = entries.filter(matches).count(_.time <= time)

  def contains(actor: String, event: String, pairs: Pair[String, String]*) = entries.find((e: Entry) => e.component == actor && e.action == event && e.hasParameters(e, pairs)).isDefined

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

  def selectTab(s: String): List[Entry] = selectTab(filter(_.hasParameter("componentType", "tab")).map(_.component).distinct, s)

  //Choose events occurring just in the specified tab (from the list of available tabs)
  def selectTab(list: List[String], s: String): List[Entry] = {
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
      if ( entry.hasParameter("componentType", "tab") && entry.component == tab ) {
        recording = true
      }
      else if ( entry.hasParameter("componentType", "tab") ) {
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
      if ( entry.hasParameter("componentType", "tab") && entry.component == tab ) {
        recording = true
      }
      else if ( entry.hasParameter("componentType", "tab") ) {
        recording = false
      }
    }
    a.toList
  }

  def countMatches(matcher: Seq[Match], until: Long): Int = {
    val earlyEntries = entries.filter(_.time < until)
    matcher.filter(matchItem => earlyEntries.find(entry => entry.time < until && matchItem(entry)).isDefined).size
  }

  //Find out what tab the user was in when one of the entries occurred
  def getTabComponent(entry: Entry): String = {
    //search back until finding an entry that indicates the tab.
    //Start one event previous in case it was a tab change event
    val entryIndex = entries.indexOf(entry)
    val matches = entries.filter(e => entries.indexOf(e) < entryIndex && e.componentType == "tab").map(_.component).toList
    if ( matches.length == 0 ) {
      "First Tab"
    }
    else {
      matches.last
    }
  }
}