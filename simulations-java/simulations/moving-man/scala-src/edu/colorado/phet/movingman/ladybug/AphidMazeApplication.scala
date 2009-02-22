package edu.colorado.phet.movingman.ladybug

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
import javax.swing.JLabel
import model.ScalaClock

//aphid maze is a tab in ladybug 2d, just an application during development to facilitate deployment and testing
object AphidMazeApplication {
  def main(args: Array[String]) = {
    ScalaApplication.main(args, "moving-man", "ladybug-2d", new AphidMazeModule(new ScalaClock(30, 30 / 1000.0)))
  }
}