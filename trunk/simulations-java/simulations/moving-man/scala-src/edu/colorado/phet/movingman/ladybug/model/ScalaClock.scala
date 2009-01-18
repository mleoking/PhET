package edu.colorado.phet.movingman.ladybug.model

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent

class ScalaClock(delay: Int, dt: Double) extends ConstantDtClock(delay, dt) {
  def addClockListener(exp: Double => Unit) {
    super.addClockListener(new ClockAdapter() {
      override def simulationTimeChanged(clockEvent: ClockEvent) = exp(clockEvent.getSimulationTimeChange)
    })
  }
}