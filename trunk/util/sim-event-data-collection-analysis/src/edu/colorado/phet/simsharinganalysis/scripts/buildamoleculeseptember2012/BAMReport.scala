package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

import edu.colorado.phet.simsharinganalysis.Log

/**
 * @author Sam Reid
 */
class BAMReport(log: Log) {
  val states = new BAMStateMachine().getStates(log)

  def reportText = "Previous state: " + states.last.start + "\nEntry: " + states.last.entry + "\nCurrent State: " + states.last.end
}