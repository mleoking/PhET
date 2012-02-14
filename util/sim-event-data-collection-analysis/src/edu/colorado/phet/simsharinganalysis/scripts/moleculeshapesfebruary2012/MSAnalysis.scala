// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.moleculeshapesfebruary2012

import edu.colorado.phet.simsharinganalysis.scripts.StateMachine
import edu.colorado.phet.simsharinganalysis.{phet, Entry, Log}
import java.io.File

/**
 * @author Sam Reid
 */

object MSAnalysis extends StateMachine[SimState] {
  def toReport(log: Log) = {
    val states = getStates(log)
    "num states: " + states.length
  }

  //Given the current state and an entry, compute the next state
  def initialState = SimState()

  def nextState(state: SimState, e: Entry) = {

    //When the sim gets reset, go back to the first state
    if ( e.messageType == "system" && e.component == "application" && e.action == "exited" ) {
      SimState()
    }
    else if ( e.enabled == false ) {
      state
    }
    else if ( e.componentType == "tab" ) {
      val t = if ( e.component == "moleculeShapesTab" ) {
        0
      }
      else {
        1
      }
      state.copy(tab = t)
    }
    else if ( state.tab == 0 ) {
      state.copy(tab0 = state.tab0.next(e))
    }
    else {
      state.copy(tab1 = state.tab1.next(e))
    }
  }

  def main(args: Array[String]) {
    val log = phet parse new File("C:\\Users\\Sam\\Desktop\\em-interviews-iii\\2012_02_06_C1F_MolShap_V10202_ComboBox_EM.txt")
    val states = this getStates log
    for ( i <- 0 until log.size ) {
      println(i + "\t" + log.entries(i).toString.replace('\t', ' ') + "\t" + states(i))
    }
  }
}