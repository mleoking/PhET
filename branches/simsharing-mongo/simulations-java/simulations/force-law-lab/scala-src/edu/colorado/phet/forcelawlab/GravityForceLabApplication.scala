// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab

import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationLauncher, PhetApplicationConfig}
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication

class GravityForceLabApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new ForceLawsModule(new ScalaClock(30, 30 / 1000.0)))
}

object GravityForceLabApplication {
  def main(args: Array[String]) {
    new PhetApplicationLauncher().launchSim(args, "force-law-lab", "gravity-force-lab", classOf[GravityForceLabApplication])
  }
}