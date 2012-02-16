// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.moleculeshapesfebruary2012

import edu.colorado.phet.simsharinganalysis.scripts.StateMachine
import edu.colorado.phet.simsharinganalysis.{phet, Entry, Log}
import java.io.File
import java.text.DecimalFormat

/**
 * @author Sam Reid
 */
object MSAnalysis extends StateMachine[SimState] {
  def toReport(log: Log) = new Report(log)

  //create a list of pairs (1,2) (2,3) ...
  def pairs[T](x: List[T]) = {
    val a = x.tail
    val b = a.reverse.tail.reverse
    ( b zip a ).toList
  }

  class Report(val log: Log) {
    val states = getStates(log)

    //For timing, only keep things between first and last user event because sometimes the sim is left on with no activity for an hour or more after student is gone
    val userStates = states.slice(states.indexWhere(_.entry.messageType == "user"), states.lastIndexWhere(_.entry.messageType == "user") + 1)

    val kitTransitions = log.entries.filter(entry => List("kitSelectionNode.previousButton", "kitSelectionNode.nextButton").contains(entry.component)).length
    val tabTransitions = states.filter(entry => entry.start.tab != entry.end.tab).length
    val moleculeTransitions = states.filter(entry => entry.start.tab1.molecule != entry.end.tab1.molecule).length
    val viewTransitions = states.filter(entry => entry.start.tab1.view != entry.end.tab1.view).length

    def minutesInTab(tab: Int): Double = userStates.filter(_.start.tab == tab).map(_.time).sum / 1000.0 / 60.0

    def timeInTab1State(state: String): Double = userStates.filter(_.start.tab == 1).filter(_.start.tab1.view == state).map(_.time).sum / 1000.0 / 60.0

    override def toString = "minutes in tab 0: " + minutesInTab(0) + "\n" +
                            "minutes in tab 1: " + minutesInTab(1) + "\n" +
                            "minutes on real in tab 1: " + timeInTab1State("real") + "\n" +
                            "minutes on model in tab 1: " + timeInTab1State("model") + "\n" +
                            "tab transitions: " + tabTransitions + "\n" +
                            "kit transitions: " + kitTransitions + "\n" +
                            "molecule transitions: " + moleculeTransitions + "\n" +
                            "view transitions: " + viewTransitions
  }

  //Given the current state and an entry, compute the next state
  def initialState(log: Log) = SimState(log.startTime)

  def nextState(state: SimState, e: Entry) = {

    //When the sim gets reset, go back to the first state
    if ( e.messageType == "system" && e.component == "application" && e.action == "exited" ) SimState(e.time)
    else if ( e.enabled == false ) state.copy(time = e.time)
    else if ( e.componentType == "tab" ) state.copy(tab = if ( e.component == "moleculeShapesTab" ) 0 else 1, time = e.time)
    else if ( state.tab == 0 ) state.copy(tab0 = state.tab0.next(e), time = e.time)
    else if ( state.tab == 1 ) state.copy(tab1 = state.tab1.next(e), time = e.time)
    else state.copy(tab1 = state.tab1.next(e), time = e.time)
  }

  def main(args: Array[String]) {

    val logs = phet load new File("C:\\Users\\Sam\\Desktop\\ms-data")
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
                                 format(r.timeInTab1State("real")) + "\t" +
                                 format(r.timeInTab1State("model")) + "\t" +
                                 r.tabTransitions + "\t" +
                                 r.kitTransitions + "\t" +
                                 r.moleculeTransitions + "\t" +
                                 r.viewTransitions))
  }
}