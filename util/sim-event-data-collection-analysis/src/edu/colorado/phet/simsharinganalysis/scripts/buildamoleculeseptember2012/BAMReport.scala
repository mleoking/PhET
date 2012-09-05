package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

import edu.colorado.phet.simsharinganalysis.Log

/**
 * @author Sam Reid
 */
class BAMReport(log: Log) {
  def millisecondsToMinutes(milliseconds: Long) = milliseconds / 60000.0

  val states = new BAMStateMachine().getStates(log)
  val timeSimOpenInMinutes = millisecondsToMinutes(states.last.end.time - states.head.start.time)

  def reportText = "Previous state: " + states.last.start + "\n" +
                   "Entry: " + states.last.entry + "\n" +
                   "Current State: " + states.last.end + "\n" +
                   "Time sim open: " + timeSimOpenInMinutes + "\n" +
                   "start time: " + states.head.start.time + "\n" +
                   "end time: " + states.last.end.time
}