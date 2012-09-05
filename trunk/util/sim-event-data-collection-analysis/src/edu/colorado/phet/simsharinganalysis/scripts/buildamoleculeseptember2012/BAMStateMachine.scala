package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

import edu.colorado.phet.simsharinganalysis.scripts.StateMachine
import edu.colorado.phet.simsharinganalysis.{Log, Entry}

/**
 * @author Sam Reid
 */
class BAMStateMachine extends StateMachine[SimState] {
  def initialState(log: Log) = SimState(0)

  def nextState(currentState: SimState, entry: Entry) = SimState(1)
}
