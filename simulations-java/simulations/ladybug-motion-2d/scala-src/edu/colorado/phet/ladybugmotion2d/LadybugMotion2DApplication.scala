package edu.colorado.phet.ladybugmotion2d

import model.LadybugModel
import edu.colorado.phet.scalacommon.{ScalaClock, ScalaApplicationLauncher}

object LadybugMotion2DApplication {
  def main(args: Array[String]) {
    ScalaApplicationLauncher.launchApplication(args, "ladybug-motion-2d", "ladybug-motion-2d",
                                               () => new LadybugModule[LadybugModel](new ScalaClock(30, LadybugDefaults.defaultDT)))
  }
}