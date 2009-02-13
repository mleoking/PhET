package edu.colorado.phet.movingman.ladybug

import canvas.LadybugCanvas
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
import java.util.Date
import javax.swing.JLabel
import model.{LadybugModel, ScalaClock}

object Ladybug2DApplication {
  def main(args: Array[String]) = {
    new PhetApplicationLauncher().launchSim(
      new PhetApplicationConfig(args, "moving-man", "ladybug-2d"),
      new ApplicationConstructor() {
        override def getApplication(config: PhetApplicationConfig) = new PhetApplication(config) {
          addModule(new LadybugModule[LadybugModel](new ScalaClock(30, 30 / 1000.0)))
        }
      })
  }
}