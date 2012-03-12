// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import edu.colorado.phet.simsharinganalysis.scripts.StateMachine
import edu.colorado.phet.simsharinganalysis.{phet, Entry, Log}
import java.io.File
import java.text.DecimalFormat

/**
 * @author Sam Reid
 */

object RPALAnalysis extends StateMachine[SimState] {
  def toReport(log: Log) = new Report(log)

  /**
   * Consolidate the important information that was needed from a single log
   * of entries.
   */
  class Report(val log: Log) {

    //Uses a state machine to compute the states that the simulation went through.
    val states = getStates(log)

    //For timing, only keep things between first and last user event because sometimes the sim is left on with no activity for an hour or more after student is gone
    val userStates = states.slice(states.indexWhere(_.entry.messageType == "user"), states.lastIndexWhere(_.entry.messageType == "user") + 1)

    //Count the number of transitions that the user made.
    val kitTransitions = log.entries.filter(entry => List("kitSelectionNode.previousButton", "kitSelectionNode.nextButton").contains(entry.component)).length
    val tabTransitions = states.filter(entry => entry.start.tab != entry.end.tab).length
    val viewTransitions = states.filter(entry => entry.start.tab1.view != entry.end.tab1.view).length

    def minutesInTab(tab: Int): Double = userStates.filter(_.start.tab == tab).map(_.time).sum / 1000.0 / 60.0

    def timeInTab1Reaction(reaction: String): Double = userStates.filter(_.start.tab == 1).filter(_.start.tab1.reaction == reaction).map(_.time).sum / 1000.0 / 60.0

    def timeInTab0Sandwich(state: String): Double = userStates.filter(_.start.tab == 0).filter(_.start.tab0.sandwich == state).map(_.time).sum / 1000.0 / 60.0

    override def toString = "General:\n" +
                            "minutes in tab 0: " + minutesInTab(0) + "\n" +
                            "minutes in tab 1: " + minutesInTab(1) + "\n" +
                            "minutes in tab 2: " + minutesInTab(2) + "\n" +
                            "transitions between tabs: " + userStates.count(e => e.start.tab != e.end.tab) + "\n\n" +
                            "Tab 1:\n" +
                            "minutes on cheese sandwich: " + timeInTab0Sandwich("cheese") + "\n" +
                            "minutes on meat & cheese sandwich : " + timeInTab0Sandwich("meatAndCheese") + "\n" +
                            "sandwich transitions: " + userStates.count(e => e.start.tab0.sandwich != e.end.tab0.sandwich) + "\n" +
                            "Tab 2:\n" +
                            "minutes on water: " + timeInTab1Reaction("water") + "\n" +
                            "minutes on ammonia: " + timeInTab1Reaction("ammonia") + "\n" +
                            "minutes on methane: " + timeInTab1Reaction("methane") + "\n" +
                            "reaction transitions: " + userStates.count(e => e.start.tab1.reaction != e.end.tab1.reaction) + "\n" +
                            "Tab 3:\n" +
                            "tab transitions: " + tabTransitions + "\n" +
                            "kit transitions: " + kitTransitions + "\n" +
                            "view transitions: " + viewTransitions
  }

  //Given the current state and an entry, compute the next state
  def initialState(log: Log) = SimState(log.startTime)

  def nextState(state: SimState, e: Entry) = {

    //When the sim gets reset, go back to the first state
    if ( e.messageType == "system" && e.component == "application" && e.action == "exited" ) SimState(e.time)
    else if ( e.enabled == false ) state.copy(time = e.time)
    else if ( e.componentType == "tab" ) state.copy(tab = e.component match {
      case "sandwichShopTab" => 0
      case "realReactionTab" => 1
      case "gameTab" => 2
    }, time = e.time)
    else if ( state.tab == 0 ) state.copy(tab0 = state.tab0.next(e), time = e.time)
    else if ( state.tab == 1 ) state.copy(tab1 = state.tab1.next(e), time = e.time)
    else if ( state.tab == 2 ) state.copy(tab2 = state.tab2.next(e), time = e.time)
    else state.copy(tab1 = state.tab1.next(e), time = e.time)
  }

  def main(args: Array[String]) {

    val logs = phet load new File("C:\\Users\\Sam\\Desktop\\ms-data-revised")
    val reports = logs.map(toReport(_))
    println("filename\t" + "" +
            "time tab 1\t" +
            "time tab 2\t" +
            "time real\t" +
            "time model\t" +
            "tab transitions\t" +
            "kit transitions\t" +
            "molecule transitions\t" +
            "view transitions")

    def format(x: Double) = new DecimalFormat("0.00").format(x)
    reports.foreach(r => println(r.log.file.getName + "\t" +
                                 format(r.minutesInTab(0)) + "\t" +
                                 format(r.minutesInTab(1)) + "\t" +
                                 r.tabTransitions + "\t" +
                                 r.kitTransitions + "\t" +
                                 r.viewTransitions))
  }
}