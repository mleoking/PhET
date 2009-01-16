package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter
import _root_.edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock
import _root_.edu.colorado.phet.common.phetcommon.model.clock.ClockEvent

class ScalaClock(delay: Int, dt: Double) extends ConstantDtClock(delay, dt) {
  def addClockListener(exp: Double => Unit) {
    super.addClockListener(new ClockAdapter() {
      override def simulationTimeChanged(clockEvent: ClockEvent) = exp(clockEvent.getSimulationTimeChange)
    })
  }
}