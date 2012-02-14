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
  def toReport(log: Log) = {
    val states = getStates(log)
    "num states: " + states.length

    val a = states.tail
    val b = states.reverse.tail.reverse
    val statePairs = b.zip(a)

    val kitTransitions = log.entries.filter(entry => entry.component == "kitSelectionNode.previousButton" || entry.component == "kitSelectionNode.nextButton").length
    val numTabTransitions = statePairs.filter(pair => pair._1.tab != pair._2.tab).length
    val numMoleculeTransitions = statePairs.filter(pair => pair._1.tab == pair._2.tab).filter(pair => pair._1.kit != pair._2.kit).length
    val numViewTransitions = statePairs.filter(pair => pair._1.tab == pair._2.tab).filter(pair => pair._1.kit != pair._2.kit).length

    def minutesInTab(tab: Int): Double = statePairs.filter(_._1.tab == tab).map(pair => pair._2.time - pair._1.time).sum / 1000.0 / 60.0
    def timeInTab1State(state: String): Double = statePairs.filter(_._1.tab == 1).filter(_._1.tab1.view == state).map(pair => pair._2.time - pair._1.time).sum / 1000.0 / 60.0

    new Report(minutesInTab(0), minutesInTab(1), timeInTab1State("real"), timeInTab1State("model"), numTabTransitions, kitTransitions, numMoleculeTransitions, numViewTransitions)
  }

  case class Report(minutesInTab0: Double, minutesInTab1: Double,
                    minutesOnRealInTab1: Double, minutesOnModelInTab1: Double,
                    numTabTransitions: Int, numKitTransitions: Int, numMoleculeTransitions: Int, numViewTransitions: Int) {
    override def toString = "minutes in tab 0: " + minutesInTab0 + "\n" +
                            "minutes in tab 1: " + minutesInTab1 + "\n" +
                            "minutes on real in tab 1: " + minutesOnRealInTab1 + "\n" +
                            "minutes on model in tab 1: " + minutesOnModelInTab1 + "\n" +
                            "tab transitions: " + numTabTransitions + "\n" +
                            "kit transitions: " + numKitTransitions + "\n" +
                            "molecule transitions: " + numMoleculeTransitions + "\n" +
                            "view transitions: " + numViewTransitions
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
    val log = phet parse new File("C:\\Users\\Sam\\Desktop\\em-interviews-iii\\2012_02_06_C1F_MolShap_V10202_ComboBox_EM.txt")
    val states = this getStates log
    for ( i <- 0 until log.size ) {
      println(i + "\t" + log.entries(i).toString.replace('\t', ' ') + "\t" + states(i))
    }

    val report = toReport(log)
    println(report)
    val format = new DecimalFormat("0.00")
    println("time in tab 0: " + format.format(report.minutesInTab0) + " minutes")
    println("time in tab 1: " + format.format(report.minutesInTab1) + " minutes")
    println("time in tab 1 and real: " + format.format(report.minutesOnRealInTab1) + " minutes")
    println("time in tab 1 and model: " + format.format(report.minutesOnModelInTab1) + " minutes")
  }
}