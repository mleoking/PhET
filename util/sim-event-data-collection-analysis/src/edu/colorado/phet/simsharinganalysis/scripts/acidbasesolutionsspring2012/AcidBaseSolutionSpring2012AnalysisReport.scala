package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

// Copyright 2002-2011, University of Colorado

import edu.colorado.phet.simsharinganalysis._
import org.jfree.data.category.DefaultCategoryDataset
import collection.mutable.ArrayBuffer
import java.io.File
import util.GrowingTable
import java.text.DecimalFormat

//TODO: mouse releases shouldn't count as "clicks"

//Prints the report on all logs within a directory to the console
object RunIt extends App {
  AcidBaseSolutionSpring2012AnalysisReport.report(new File("C:\\Users\\Sam\\Desktop\\kelly-data"), println)
}

object AcidBaseSolutionSpring2012AnalysisReport {

  def getSign(d: Double) = d match {
    case x if x < 0 => -1
    case x if x > 0 => 1
    case x if x == 0 => 0
  }

  //KL: Each time students start dragging should count as one click. But if they change direction during a single drag, I want that to count as more than one click - one for each direction.
  def sliderChangedDirection(log: Log, e: Entry) = {

    val index = log.indexOf(e)
    val previousEvent1 = log.entries.slice(0, index).filter(_.messageType == "user").last
    val previousEvent2 = log.entries.slice(0, index - 1).filter(_.messageType == "user").last
    if ( previousEvent1.action == "drag" && previousEvent2.action == "drag" ) {

      val delta1 = previousEvent2("value").toDouble - previousEvent1("value").toDouble
      val delta2 = previousEvent1("value").toDouble - e("value").toDouble

      val turnDown = getSign(delta1) == 1 && getSign(delta2) == -1
      val turnUp = getSign(delta1) == -1 && getSign(delta2) == 1
      if ( turnDown || turnUp ) true else false
    }
    else {
      false
    }
  }

  def isAcidBaseClick(log: Log, e: Entry) = {
    e match {

      //Starting a slider drag counts as a click
      case Entry(_, "user", _, _, "endDrag", _) => false
      case Entry(_, "user", _, "slider", "startDrag", _) => true
      case Entry(_, "user", _, "slider", "drag", _) => sliderChangedDirection(log, e)
      case Entry(_, "user", _, _, _, _) => true
      case _ => false
    }
  }

  def matchesAny(s: String, list: List[String]) = list.contains(s)

  //Given the current state and an entry, compute the next state
  def nextState(state: SimState, e: Entry) = {
    if ( e.enabled == false ) {
      state
    }
    else if ( e.componentType == "tab" ) {
      state.copy(Globals.tabs.indexOf(e.component))
    }
    else if ( state.selectedTab == 0 ) {
      state.copy(tab0 = state.tab0.next(e))
    }
    else {
      state.copy(tab1 = state.tab1.next(e))
    }
  }

  //Find the sequence of states of the sim
  //The list will contain one state per event, indicating the state of the sim after the event.
  def getStates(log: Log) = {
    val states = new ArrayBuffer[SimState]
    var state = SimState()
    for ( e <- log.entries ) {
      state = nextState(state, e)
      states += state
    }
    states.toList
  }

  def compare(s: String, o: Object) = s == o.toString

  def getTimesBetweenEntries(entries: scala.List[Entry]): List[Long] = {
    val a = entries.tail
    val b = entries.reverse.tail.reverse
    val pairs = b.zip(a)
    pairs.map(p => p._2.time - p._1.time)
  }

  def report(dir: File, writeLine: String => Unit) {

    val logs = phet.load(dir).sortBy(_.startTime)
    writeLine("found: " + logs.length + " logs")
    for ( log <- logs ) {
      writeSingleLogReport(log, writeLine)
      showBarChart(log)
    }
  }

  def countEntriesWithinTime(entries: List[Entry], min: Long, max: Long): Int = entries.filter(e => e.time >= min && e.time < max).length

  def getClickTimeHistogram(log: Log, timePeriod: Pair[Int, String]) = {
    val entries: List[Entry] = log.entries.filter(isAcidBaseClick(log, _)).toList
    val millisPerMinute = 60L * 1000L
    ( ( log.startTime until log.endTime by millisPerMinute ).map(time => ( time - log.startTime ) / millisPerMinute -> countEntriesWithinTime(entries, time, time + millisPerMinute)) ).toMap
  }

  def showBarChart(log: Log) {
    //For entries that count as a "click" for ABS, make a List[Pair[Entry,Entry]]
    val timePeriod = Pair(1000 * 60, "minute")
    val table = getClickTimeHistogram(log, timePeriod)
    //      table.foreach(entry => writeLine("clicks within " + timePeriod._2 + " " + entry._1 + " => " + entry._2))

    phet.barChart("Histogram of clicks", "number of clicks", new DefaultCategoryDataset {
      for ( e <- table.keys.toList.sorted ) {
        addValue(table(e).asInstanceOf[Number], "selected " + timePeriod._2 + " interval", e)
      }
    })
  }

  def writeSingleLogReport(log: Log, writeLine: String => Unit) {
    writeLine("Session: " + log.session)

    val clicks: List[Entry] = log.entries.filter(isAcidBaseClick(log, _)).toList

    if ( clicks.length > 0 ) {
      //Show how long the sim was open.  Format so that RealTimeAnalysis isn't too jumpy
      writeLine("Time sim open (min):\t" + new DecimalFormat("0.00").format(log.minutesUsed)) //NOTE: this is the time from first to last event
      writeLine("First click to last click (min):\t" + new DecimalFormat("0.00").format(( clicks.last.time - clicks.head.time ) / 1000.0 / 60.0))
    }
    writeLine("Number of clicks:\t" + clicks.length)

    val timePeriod = Pair(1000 * 60, "minute")
    val table = getClickTimeHistogram(log, timePeriod)
    val mapped = table.keys.toList.sorted.map(e => e + " -> " + table(e)).mkString(", ")
    writeLine("Clicks per min:\t" + mapped)

    import scala.collection.JavaConversions._
    writeLine("Number of events on interactive components:\t" + log.entries.filter(_.messageType == "user").filter(_.interactive != false).length)
    val usedComponents = log.entries.map(_.component).distinct.filter(ABSSimSharing.interactive.map(_.toString).toList.contains(_)).sorted
    writeLine("Type used:\t" + usedComponents)

    val allComponents = ABSSimSharing.interactive.toList.map(_.toString).toList
    writeLine("Type not used:\t" + allComponents.filter(e => !usedComponents.contains(e)).sorted)

    val usedStringBaseRadioButton = log.entries.filter(_.messageType == "user").filter(_.component == "strongBaseRadioButton").length > 0
    val usedWeakBaseRadioButton = log.entries.filter(_.messageType == "user").filter(_.component == "weakBaseRadioButton").length > 0
    val selectBase = usedStringBaseRadioButton || usedWeakBaseRadioButton
    writeLine("Select base:  \t" + selectBase)
    val usedShowSolventCheckBox = log.userEntries.filter(_.component == "showSolventCheckBox").length > 0
    writeLine("Show solvent:  \t" + usedShowSolventCheckBox)
    writeLine("Dunk phMeter:  \t" + !log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").isEmpty)
    writeLine("Dunk phPaper: \t" + !log.filter(_.component == "phPaper").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").isEmpty)
    writeLine("Complete circuit:\t" + !log.userEntries.filter(e => e.hasParameter("isCircuitCompleted", "true")).isEmpty)

    val states = getStates(log)

    val solutionTable = new GrowingTable
    val viewTable = new GrowingTable
    val testTable = new GrowingTable
    val entryIndices = 0 until log.entries.length - 1
    val timeBetweenEntries = getTimesBetweenEntries(log.entries)
    for ( i <- entryIndices ) {
      val time = timeBetweenEntries(i)
      solutionTable.add(states(i).displayedSolution, time)
      viewTable.add(states(i).displayedView, time)
      testTable.add(states(i).displayedTest, time)
    }

    writeLine("Time on solutions (min):\t" + solutionTable.toMinuteMap)
    writeLine("Time on views (min):\t" + viewTable.toMinuteMap)
    writeLine("Time on tests (min):\t" + testTable.toMinuteMap)

    val a = states.tail
    val b = states.reverse.tail.reverse
    val statePairs = b.zip(a)
    val numTabTransitions = statePairs.map(pair => if ( pair._2.selectedTab != pair._1.selectedTab ) 1 else 0).sum

    //If the conductivity test is selected, the analysis tool should not count it as a view transition
    def sameTab(a: SimState, b: SimState) = a.selectedTab == b.selectedTab
    def sameTest(a: SimState, b: SimState) = a.displayedTest == b.displayedTest
    val numViewTransitions = statePairs.map(pair => if ( pair._2.displayedView != pair._1.displayedView && sameTab(pair._1, pair._2) && sameTest(pair._1, pair._2) ) 1 else 0).sum
    val numSolutionTransitions = statePairs.map(pair => if ( pair._2.displayedSolution != pair._1.displayedSolution && sameTab(pair._1, pair._2) ) 1 else 0).sum
    val numTestTransitions = statePairs.map(pair => if ( pair._2.displayedTest != pair._1.displayedTest && sameTab(pair._1, pair._2) ) 1 else 0).sum
    writeLine("Num tab transitions:\t" + numTabTransitions)
    writeLine("Num solution transitions:\t" + numSolutionTransitions)
    writeLine("Num view transitions:\t" + numViewTransitions)
    writeLine("Num test transitions:\t" + numTestTransitions)

    val nonInteractiveEvents = log.entries.filter(entry => entry.messageType == "user" && entry.interactive == "false")
    val typeUsed = nonInteractiveEvents.map(_.component).distinct
    writeLine("Number of events on non-interactive components:\t" + nonInteractiveEvents.length)
    writeLine("Type used:\t" + typeUsed.sorted)
    writeLine("Type not used:\t" + ABSSimSharing.nonInteractive.toList.map(_.toString).filterNot(typeUsed.contains(_)).sorted)

    writeLine("State: " + states.last)
    writeLine("")
  }
}