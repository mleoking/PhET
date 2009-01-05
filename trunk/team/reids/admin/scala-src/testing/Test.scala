/*
Example of how to use phetcommon from scala.
 */

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
import javax.swing.JLabel

object MyTest {
  def main(args: Array[String]) = {
    println("started")

    class Rotator(node: PNode) extends ClockAdapter {
      override def simulationTimeChanged(clockEvent: ClockEvent) = {
        node.rotateInPlace(3.14 / 64)
      }
    }
    class ScalaModule extends Module("my module", new ConstantDtClock(30, 1)) {
      val canvas = new PhetPCanvas
      setSimulationPanel(canvas)

      val ptext = new PText("hello")
      ptext.setOffset(300, 200)
      canvas addScreenChild ptext

      getClock.addClockListener(new Rotator(ptext))
    }

    val config = new PhetApplicationConfig(args, "moving-man")
    object ScalaApp extends PhetApplication(config) {
      addModule(new ScalaModule)
    }
    object ScalaAppConstructor extends ApplicationConstructor {
      override def getApplication(a: PhetApplicationConfig): PhetApplication = ScalaApp
    }

    new PhetApplicationLauncher().launchSim(config, ScalaAppConstructor)
    println("finished")
  }
}