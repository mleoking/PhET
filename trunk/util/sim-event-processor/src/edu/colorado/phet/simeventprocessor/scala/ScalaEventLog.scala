// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import edu.colorado.phet.simeventprocessor.EventLog
import java.util.Date

/**
 * Adds scala-convenient interface for REPL.
 * @author Sam Reid
 */
class ScalaEventLog(log: EventLog) {
  val machineID = log.getMachineID
  val simName = log.getSimName
  val epoch = log.getServerStartTime
  val simVersion = log.getSimVersion
  val study = log.getStudy
  val size = log.size
  val session = log.getSessionID
  val user = log.getID

  override def toString = simName + " " + simVersion + " " + new Date(epoch) + " (" + epoch + "), study = " + study + ", user = " + user + ", events = " + size + ", machineID = " + machineID + ", sessionID = " + session
}