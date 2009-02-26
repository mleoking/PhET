package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel
import aphidmaze.AphidMazeModule
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
import javax.swing.{UIManager, JLabel}
import model.ScalaClock

object ScalaApplication {
  def main(args: Array[String], project: String, simulation: String, modules: Module*) = {
    val pac = new PhetApplicationConfig(args, project, simulation)
    pac.setLookAndFeel(new PhetLookAndFeel() {protected override def getLookAndFeelClassName = UIManager.getCrossPlatformLookAndFeelClassName})

    new PhetApplicationLauncher().launchSim(
      pac,
      new ApplicationConstructor() {
        override def getApplication(config: PhetApplicationConfig) = new PhetApplication(config) {
          modules.foreach(addModule(_))
        }
      })
  }
}