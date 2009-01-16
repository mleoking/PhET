package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor
import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.common.phetcommon.application.PhetApplication
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import edu.umd.cs.piccolo.nodes.PText
import edu.umd.cs.piccolo.PNode
import java.awt.Color
import java.awt.Font
import javax.swing.JLabel

object Ladybug2DApplication {
  class ScalaModelElement(element: Double => Unit) extends ClockAdapter {
    override def simulationTimeChanged(clockEvent: ClockEvent) = {
      element(clockEvent.getSimulationTimeChange)
    }
  }

  def main(args: Array[String]) = {
    println("started")

    val clock = new ScalaClock(30, 1)
    new PhetApplicationLauncher().launchSim(
      new PhetApplicationConfig(args, "moving-man", "ladybug-2d"),
      new ApplicationConstructor() {
        override def getApplication(a: PhetApplicationConfig): PhetApplication = new PhetApplication(a) {
          addModule(new LadybugModule(clock))
        }
      })
    println("finished")
  }
}