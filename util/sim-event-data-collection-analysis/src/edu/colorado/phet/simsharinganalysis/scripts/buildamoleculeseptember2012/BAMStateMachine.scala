package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

import edu.colorado.phet.simsharinganalysis.scripts.StateMachine
import edu.colorado.phet.simsharinganalysis.{Log, Entry}

/**
 * @author Sam Reid
 */
class BAMStateMachine extends StateMachine[SimState] {

  val tabs = List("makeMoleculesTab", "collectMultipleTab", "largerMoleculesTab")

  //Given the current state and an entry, compute the next state
  def initialState(log: Log) = SimState(log.startTime, 0)

  def nextState(state: SimState, e: Entry) = {

    //When the sim gets reset, go back to the first state
    if ( e.messageType == "system" && e.component == "application" && e.action == "exited" ) SimState(e.time, 0)
    else if ( e.enabled == false ) state.copy(time = e.time)
    else if ( e.componentType == "tab" ) state.copy(tab = tabs.indexOf(e.component), time = e.time)
    //    else if ( state.tab == 0 ) state.copy(tab0 = state.tab0.next(e), time = e.time)
    //    else if ( state.tab == 1 ) state.copy(tab1 = state.tab1.next(e), time = e.time)
    //    else state.copy(tab1 = state.tab1.next(e), time = e.time)
    else state.copy(time = e.time)
  }
}
