// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis.{Log, Entry}
import collection.mutable.ArrayBuffer

/**
 * The StateMachine computes the state log for a given Log's entries.  It represents the transitions of the finite state machine.
 * @author Sam Reid
 */
trait StateMachine[T <: {def time : Long}] {
  def initialState(log: Log): T

  def nextState(currentState: T, entry: Entry): T

  //create a list of pairs (1,2) (2,3) ...
  def pairs(x: List[T]) = {
    val a = x.tail
    val b = a.reverse.tail.reverse
    ( b zip a ).toList
  }

  //Find the sequence of states of the sim
  //The list will contain one state per event, indicating the state of the sim after the event.
  def getStates(log: Log) = {
    val states = new ArrayBuffer[StateEntry[T]]
    var start = initialState(log)
    for ( e <- log.entries ) {
      val end = nextState(start, e)
      states += StateEntry(start, e, end)
      start = end
    }
    states.toList
  }
}