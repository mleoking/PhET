package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

// Copyright 2002-2011, University of Colorado

import edu.colorado.phet.simsharinganalysis._
import collection.mutable.ArrayBuffer
import util.GrowingTable
import java.text.DecimalFormat

//TODO: mouse releases shouldn't count as "clicks"

object AcidBaseReport {
  def toReport(log: Log) = new AcidBaseReport(log)

  def isAcidBaseClick(log: Log, e: Entry) = {
    e match {

      //Starting a slider drag counts as a click
      case Entry(_, "user", _, _, "endDrag", _) => false
      case Entry(_, "user", _, "slider", "startDrag", _) => trueUnlessPreviousLineWasAStartDrag(log, e)
      case Entry(_, "user", _, "slider", "drag", _) => sliderChangedDirection(log, e)
      case Entry(_, "user", _, _, _, _) => true
      case _ => false
    }
  }

  //4 of the log files have duplicate "startDrag" lines within milliseconds of each other.  Filter them out so they don't count as clicks.
  def trueUnlessPreviousLineWasAStartDrag(log: Log, e: Entry) = {
    val index = log indexOf e
    val previousUserEvent = log.entries.slice(0, index).filter(_.messageType == "user").last
    if ( previousUserEvent.action == "startDrag" ) false else true
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

  def getSign(d: Double) = d match {
    case x if x < 0 => -1
    case x if x > 0 => 1
    case x if x == 0 => 0
  }
}

case class StateTransition(start: SimState, entry: Entry, end: SimState) {
  def used(component: String): Boolean = entry.messageType == "user" && entry.component == component

  def used(radioButton: String, icon: String): Boolean = used(radioButton) || used(icon)

  val dunkedPHMeter = entry.component == "phMeter" && entry.hasParameter("isInSolution", "true") && entry.action == "drag"
  val dunkedPHPaper = entry.component == "phPaper" && entry.hasParameter("isInSolution", "true") && entry.action == "drag"
  val completedCircuit = entry.hasParameter("isCircuitCompleted", "true")

  def usedAcidControlOn2ndTab(control: String) = start.tab1.acid && start.selectedTab == 1 && entry.messageType == "user" && entry.component == control

  def usedBaseControlOn2ndTab(control: String) = !start.tab1.acid && start.selectedTab == 1 && entry.messageType == "user" && entry.component == control
}

class AcidBaseReport(val log: Log) {

  def matchesAny(s: String, list: List[String]) = list.contains(s)

  //Given the current state and an entry, compute the next state
  def nextState(state: SimState, e: Entry) = {

    //When the sim gets reset, go back to the first state
    if ( e.messageType == "system" && e.component == "application" && e.action == "exited" ) {
      SimState()
    }
    else if ( e.enabled == false ) {
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
  def getStates(log: Log) = SimState() :: getStatesWithTransitions(log).map(_.end)

  //Find the sequence of states of the sim
  //The list will contain one state per event, indicating the state of the sim after the event.
  def getStatesWithTransitions(log: Log) = {
    val states = new ArrayBuffer[StateTransition]
    var state = SimState()
    for ( e <- log.entries ) {
      val orig = state
      state = nextState(state, e)
      states += StateTransition(orig, e, state)
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

  def countEntriesWithinTime(entries: List[Entry], min: Long, max: Long): Int = entries.filter(e => e.time >= min && e.time < max).length

  def getClickTimeHistogram(log: Log, startTime: Log => Long = _.startTime) = {
    val entries: List[Entry] = log.entries.filter(AcidBaseReport.isAcidBaseClick(log, _)).toList
    val millisPerMinute = 60L * 1000L
    ( ( startTime(log) until log.endTime by millisPerMinute ).map(time => ( time - startTime(log) ) / millisPerMinute -> countEntriesWithinTime(entries, time, time + millisPerMinute)) ).toMap
  }

  var reportText = ""

  def writeLine(s: String) {
    reportText = reportText + s + "\n"
  }

  writeLine("Session: " + log.session)

  val session = log.session

  val filename = log.file.getName

  val clicks: List[Entry] = log.entries.filter(AcidBaseReport.isAcidBaseClick(log, _)).toList

  if ( clicks.length > 0 ) {
    //Show how long the sim was open.  Format so that RealTimeAnalysis isn't too jumpy
    writeLine("Time sim open (min):\t" + new DecimalFormat("0.00").format(log.minutesUsed)) //NOTE: this is the time from first to last event
    writeLine("First click to last click (min):\t" + new DecimalFormat("0.00").format(( clicks.last.time - clicks.head.time ) / 1000.0 / 60.0))
  }
  writeLine("Number of clicks:\t" + clicks.length)

  val timePeriod = Pair(1000 * 60, "minute")
  val table = getClickTimeHistogram(log)
  val mapped = table.keys.toList.sorted.map(e => e + " -> " + table(e)).mkString(", ")
  writeLine("Clicks per min:\t" + mapped)

  import scala.collection.JavaConversions._

  writeLine("Number of events on interactive components:\t" + log.entries.filter(_.messageType == "user").filter(_.interactive != false).length)
  val usedComponents = log.entries.map(_.component).distinct.filter(ABSSimSharing.interactive.map(_.toString).toList.contains(_)).sorted
  writeLine("Type used:\t" + usedComponents)

  val allComponents = ABSSimSharing.interactive.toList.map(_.toString).toList
  writeLine("Type not used:\t" + allComponents.filter(e => !usedComponents.contains(e)).sorted)

  //for a given delta of time elapsed into the sim, output as a string with MM:SS
  def toMinutes(t: Long) = {
    val seconds = t / 1000.0
    val minutes = seconds / 60.0
    new DecimalFormat("0.00").format(minutes)
  }

  //Create a string for a list of entries indicating if has elements or not (true or false), and the times the event occurred.  The time should be the number of minutes into the sim run.
  def indicator(entries: List[Entry]) = {

    //- use times in minutes since the *first click*
    ( entries.length > 0 ).toString + "\t" + ( if ( entries.length > 0 ) entries.map(entry => toMinutes(entry.time - clicks.head.time)).mkString("\t") else "" )
  }

  val timesUsedStrongOrWeakBaseRadioButton = log.entries.filter(entry => entry.messageType == "user" && ( entry.component == "strongBaseRadioButton" || entry.component == "weakBaseRadioButton" || entry.component == "baseRadioButton" ))
  writeLine("Select base:  \t" + indicator(timesUsedStrongOrWeakBaseRadioButton))
  writeLine("Show solvent:  \t" + indicator(log.userEntries.filter(_.component == "showSolventCheckBox")))
  writeLine("Dunk phMeter:  \t" + indicator(log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag")))
  writeLine("Dunk phPaper: \t" + indicator(log.filter(_.component == "phPaper").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag")))
  writeLine("Complete circuit:\t" + indicator(log.userEntries.filter(e => e.hasParameter("isCircuitCompleted", "true"))))

  val statesWithTransitions = getStatesWithTransitions(log)
  val states = SimState() :: statesWithTransitions.map(_.end)
  //  println("entries: "+log.entries.length+", states = "+states.length)

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

  val statePairs = statesWithTransitions.map(s => (s.start, s.end))
  val numTabTransitions = statePairs.map(pair => if ( pair._2.selectedTab != pair._1.selectedTab ) 1 else 0).sum

  //If the conductivity test is selected, the analysis tool should not count it as a view transition
  def sameTab(p: Pair[SimState, SimState]) = p._1.selectedTab == p._2.selectedTab

  def sameTest(p: Pair[SimState, SimState]) = p._1.displayedTest == p._2.displayedTest

  //KL: Pressing reset all should not count as a solution transition, but it can affect the time on solutions.  I presume this pattern should hold true for "test" and "view" as well as "solution"
  def wasReset(p: Pair[SimState, SimState]) = {

    val found = statesWithTransitions.find(x => x.start == p._1 && x.end == p._2).get
    found.entry.component == "resetAllConfirmationDialogYesButton"
    //    //Use reference equality to lookup index
    //    val indexA = states.indexWhere(p._1 eq _)
    //    val indexB = states.indexWhere(p._2 eq _)
    //    val intermediateEntry = log.entries(indexB)
    //
    //    //      println("indexA= " + indexA + ", indexB = " + indexB + ", a = " + p._1 + ", b = " + p._2 + ", intermediateEntry = " + intermediateEntry)

  }

  val clicksPerMinute = getClickTimeHistogram(log)
  val clicksPerMinuteBasedOnFirstClick = getClickTimeHistogram(log, element => if ( clicks.isEmpty ) log.startTime else clicks.head.time)

  val numViewTransitions = statePairs.map(pair => if ( pair._2.displayedView != pair._1.displayedView && sameTab(pair) && sameTest(pair) && !wasReset(pair) ) 1 else 0).sum
  val numSolutionTransitions = statePairs.map(pair => if ( pair._2.displayedSolution != pair._1.displayedSolution && sameTab(pair) && !wasReset(pair) ) 1 else 0).sum
  val numTestTransitions = statePairs.map(pair => if ( pair._2.displayedTest != pair._1.displayedTest && sameTab(pair) && !wasReset(pair) ) 1 else 0).sum
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

  val usedStringBaseRadioButton = log.entries.filter(_.messageType == "user").filter(_.component == "strongBaseRadioButton").length > 0
  val usedWeakBaseRadioButton = log.entries.filter(_.messageType == "user").filter(_.component == "weakBaseRadioButton").length > 0
  val usedBaseRadioButton = log.entries.filter(_.messageType == "user").filter(_.component == "baseRadioButton").length > 0
  val selectedBase = usedStringBaseRadioButton || usedWeakBaseRadioButton || usedBaseRadioButton
  val showedSolvent = log.userEntries.filter(_.component == "showSolventCheckBox").length > 0
  val dunkedPHMeter = !log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").isEmpty
  val dunkedPHPaper = !log.filter(_.component == "phPaper").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").isEmpty
  val completedCircuit = !log.userEntries.filter(e => e.hasParameter("isCircuitCompleted", "true")).isEmpty

  val timeSimOpenMin = log.minutesUsed
  val firstClickToLastClick = if ( clicks.length == 0 ) 0 else ( clicks.last.time - clicks.head.time ) / 1000.0 / 60.0
  val numberOfClicks = clicks.length

  val movedProbe = used("conductivityTester.negativeProbe") || used("conductivityTester.positiveProbe")

  def used(component: String): Boolean = log.entries.filter(_.messageType == "user").filter(_.component == component).length > 0

  def entryIndicesWhenUsed(component: String) = log.entries.zipWithIndex.filter(_._1.messageType == "user").filter(_._1.component == component).map(_._2).toList

  def used(radioButton: String, icon: String): Boolean = used(radioButton) || used(icon)

  //See if they used any of the specified elements
  def used(elm: List[String]): Boolean = elm.find(used).isDefined

  def neverUsed(component: String): Boolean = log.entries.filter(_.messageType == "user").filter(_.component == component).length == 0

  def neverUsed(radioButton: String, icon: String): Boolean = neverUsed(radioButton) && neverUsed(icon)

  //Fixed a bug in the following 6 functions that was checking for usage of a component any time in the log, not just at the appropriate time
  def usedAcidControlOn2ndTab(control: String) =
    statesWithTransitions.find(s => s.start.tab1.acid && s.start.selectedTab == 1 && s.entry.messageType == "user" && s.entry.component == control).isDefined

  def usedBaseControlOn2ndTab(control: String) =
    statesWithTransitions.find(s => !s.start.tab1.acid && s.start.selectedTab == 1 && s.entry.messageType == "user" && s.entry.component == control).isDefined

  val everUsedAcidSolutionControl = {
    statesWithTransitions.find(s => s.start.tab1.acid && s.start.selectedTab == 1 && s.entry.messageType == "user" && List("weakStrengthControl", "weakRadioButton", "strongRadioButton", "concentrationControl").contains(s.entry.component)).isDefined
  }
  val everUsedBaseSolutionControl = {
    statesWithTransitions.find(s => !s.start.tab1.acid && s.start.selectedTab == 1 && s.entry.messageType == "user" && List("weakStrengthControl", "weakRadioButton", "strongRadioButton", "concentrationControl").contains(s.entry.component)).isDefined
  }
  val neverUsedAcidSolutionControls = !everUsedAcidSolutionControl
  val neverUsedBaseSolutionControls = !everUsedBaseSolutionControl

  val baseIndices = entryIndicesWhenUsed("baseRadioButton")
  val acidIndices = entryIndicesWhenUsed("acidRadioButton")
}