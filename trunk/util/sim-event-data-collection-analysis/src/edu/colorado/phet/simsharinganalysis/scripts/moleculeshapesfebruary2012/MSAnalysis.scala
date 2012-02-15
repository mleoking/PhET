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

  class Report(log: Log) {
    val states = getStates(log)
    "num states: " + states.length

    val a = states.tail
    val b = states.reverse.tail.reverse
    val statePairs = b.zip(a)

    val numKitTransitions = log.entries.filter(entry => entry.component == "kitSelectionNode.previousButton" || entry.component == "kitSelectionNode.nextButton").length
    val numTabTransitions = statePairs.filter(pair => pair._1.tab != pair._2.tab).length
    val numMoleculeTransitions = statePairs.filter(pair => pair._1.tab == pair._2.tab).filter(pair => pair._1.kit != pair._2.kit).length
    val numViewTransitions = statePairs.filter(pair => pair._1.tab == pair._2.tab).filter(pair => pair._1.kit != pair._2.kit).length

    def minutesInTab(tab: Int): Double = statePairs.filter(_._1.tab == tab).map(pair => pair._2.time - pair._1.time).sum / 1000.0 / 60.0

    def timeInTab1State(state: String): Double = statePairs.filter(_._1.tab == 1).filter(_._1.tab1.view == state).map(pair => pair._2.time - pair._1.time).sum / 1000.0 / 60.0

    override def toString = "minutes in tab 0: " + minutesInTab(0) + "\n" +
                            "minutes in tab 1: " + minutesInTab(1) + "\n" +
                            "minutes on real in tab 1: " + timeInTab1State("real") + "\n" +
                            "minutes on model in tab 1: " + timeInTab1State("model") + "\n" +
                            "tab transitions: " + numTabTransitions + "\n" +
                            "kit transitions: " + numKitTransitions + "\n" +
                            "molecule transitions: " + numMoleculeTransitions + "\n" +
                            "view transitions: " + numViewTransitions
  }

  case class ReportX(minutesInTab0: Double, minutesInTab1: Double,
                     minutesOnRealInTab1: Double, minutesOnModelInTab1: Double,
                     numTabTransitions: Int, numKitTransitions: Int, numMoleculeTransitions: Int, numViewTransitions: Int) {

  }

  //Given the current state and an entry, compute the next state
  def initialState(log: Log) = SimState(log.startTime)

  def nextState(state: SimState, e: Entry) = {

    //When the sim gets reset, go back to the first state
    if ( e.messageType == "system" && e.component == "application" && e.action == "exited" ) SimState(e.time)
    else if ( e.enabled == false ) state.copy(time = e.time)
    else if ( e.componentType == "tab" ) state.copy(tab = if ( e.component == "moleculeShapesTab" ) 0 else 1, time = e.time)
    else if ( state.tab == 0 ) state.copy(tab0 = state.tab0.next(e), time = e.time)
    else state.copy(tab1 = state.tab1.next(e), time = e.time)
  }

  def main(args: Array[String]) {

    val logs = phet load new File("C:\\Users\\Sam\\Desktop\\ms-data")
    val reports = logs.map(toReport(_))
    println("time tab 1\t" +
            "time tab 2\t" +
            "time real\t" +
            "time model\t" +
            "tab transitions\t" +
            "kit transitions\t" +
            "molecule transitions\t" +
            "view transitions")

    def format(x: Double) = new DecimalFormat("0.00").format(x)
    reports.foreach(r => println(format(r.minutesInTab(0)) + "\t" +
                                 format(r.minutesInTab(1)) + "\t" +
                                 format(r.timeInTab1State("real")) + "\t" +
                                 format(r.timeInTab1State("model")) + "\t" +
                                 r.numTabTransitions + "\t" +
                                 r.numKitTransitions + "\t" +
                                 r.numMoleculeTransitions + "\t" +
                                 r.numViewTransitions))
  }
}