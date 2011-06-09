package edu.colorado.phet.forcelawlab

import java.lang.Math._
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import java.awt.Color
import java.text.{DecimalFormat, FieldPosition}
import edu.colorado.phet.scalacommon.swing.MyRadioButton
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationLauncher, PhetApplicationConfig, Module}
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication
import javax.swing.border.TitledBorder

class GravityForceLabApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new ForceLawsModule(new ScalaClock(30, 30 / 1000.0)))
}

object GravityForceLabApplication extends App {
  new PhetApplicationLauncher().launchSim(args, "force-law-lab", "gravity-force-lab", classOf[GravityForceLabApplication])
}