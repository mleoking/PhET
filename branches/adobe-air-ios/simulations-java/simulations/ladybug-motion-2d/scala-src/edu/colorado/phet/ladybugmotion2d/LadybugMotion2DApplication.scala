package edu.colorado.phet.ladybugmotion2d

import model.LadybugModel
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.ladybugmotion2d.LadybugDefaults.defaultDT
import edu.colorado.phet.scalacommon.ScalaApplicationLauncher.launchApplication

object LadybugMotion2DApplication {
  def main(args: Array[String]) {
    launchApplication(args, "ladybug-motion-2d", "ladybug-motion-2d", () => new LadybugModule[LadybugModel](new ScalaClock(30, defaultDT)))
  }
}