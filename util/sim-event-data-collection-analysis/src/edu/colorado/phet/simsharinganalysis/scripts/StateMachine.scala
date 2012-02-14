// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis.{Log, Entry}
import collection.mutable.ArrayBuffer

/**
 * The StateMachine computes the state log for a given Log's entries.  It represents the transitions of the finite state machine.
 * @author Sam Reid
 */
trait StateMachine[T] {
  def initialState(log: Log): T

  def nextState(currentState: T, entry: Entry): T

  //Find the sequence of states of the sim
  //The list will contain one state per event, indicating the state of the sim after the event.
  def getStates(log: Log) = {
    val states = new ArrayBuffer[T]
    var state = initialState(log)
    for ( e <- log.entries ) {
      state = nextState(state, e)
      states += state
    }
    states.toList
  }
}