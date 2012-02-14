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
    val numTabTransitions = statePairs.map(pair => if ( pair._2.tab != pair._1.tab ) 1 else 0).sum
    println("num tab transitions: " + numTabTransitions)
    def minutesInTab(tab: Int): Double = statePairs.filter(_._1.tab == tab).map(pair => pair._2.time - pair._1.time).sum / 1000.0 / 60.0
    def timeInTab1State(state: String): Double = statePairs.filter(_._1.tab == 1).filter(_._1.view == state).map(pair => pair._2.time - pair._1.time).sum / 1000.0 / 60.0
    val format = new DecimalFormat("0.00")
    println("time in tab 0: " + format.format(minutesInTab(0)) + " minutes")
    println("time in tab 1: " + format.format(minutesInTab(1)) + " minutes")
    println("time in tab 1 and real: " + format.format(minutesInTab(1)) + " minutes")

    new Report(minutesInTab(0), minutesInTab(1), timeInTab1State("real"), timeInTab1State("model"))
  }

  case class Report(minutesInTab0: Double, minutesInTab1: Double, minutesOnRealInTab1: Double, minutesOnModelInTab1: Double) {
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

    println(toReport(log))
  }
}