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
import java.awt.Color
import java.awt.Font
import javax.swing.JLabel

object MyTest {
  def main(args: Array[String]) = {
    println("started")
    class ScalaModelElement(element: Double => Unit) extends ClockAdapter {
      override def simulationTimeChanged(clockEvent: ClockEvent) = {
        element(clockEvent.getSimulationTimeChange)
      }
    }
    class ScalaModule extends Module("my module", new ConstantDtClock(30, 1)) {
      val canvas = new PhetPCanvas
      setSimulationPanel(canvas)

      val ptext = new PText("hello")
      ptext setFont new Font("Lucida Sans", Font.BOLD, 30)
      ptext.setOffset(300, 200)
      canvas addScreenChild ptext
      canvas setBackground new Color(200, 255, 240)
      getClock.addClockListener(new ScalaModelElement((dt: Double) => ptext.translate(1, 0)))
      getClock.addClockListener(new ScalaModelElement((dt: Double) => ptext.translate(0, 2)))
    }

    new PhetApplicationLauncher().launchSim(
      new PhetApplicationConfig(args, "moving-man"),
      new ApplicationConstructor() {
        override def getApplication(a: PhetApplicationConfig): PhetApplication = new PhetApplication(a) {
          addModule(new ScalaModule)
        }
      })
    println("finished")
  }
}