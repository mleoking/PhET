// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.moleculeshapesfebruary2012

import collection.mutable.ArrayBuffer
import edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012.SimState
import edu.colorado.phet.simsharinganalysis.{Entry, Log}

/**
 * @author Sam Reid
 */

object MSFeb2012Analysis {
  def toReport(log: Log) = {
    val states = getStates(log)
    "num states: " + states.length
  }

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
  def getStates(log: Log) = {
    val states = new ArrayBuffer[SimState]
    var state = SimState()
    for ( e <- log.entries ) {
      state = nextState(state, e)
      states += state
    }
    states.toList
  }
}