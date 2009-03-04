package edu.colorado.phet.scalacommon

import common.phetcommon.model.clock.{ConstantDtClock, ClockEvent, ClockAdapter}

class ScalaClock(delay: Int, dt: Double) extends ConstantDtClock(delay, dt) {
  def addClockListener(exp: Double => Unit) {
    super.addClockListener(new ClockAdapter() {
      override def simulationTimeChanged(clockEvent: ClockEvent) = exp(clockEvent.getSimulationTimeChange)
    })
  }
}