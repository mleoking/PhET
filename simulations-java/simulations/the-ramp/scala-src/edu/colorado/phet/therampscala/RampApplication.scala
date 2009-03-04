package edu.colorado.phet.therampscala

import movingman.ladybug.LadybugModule
import scalacommon.{ScalaApplicationLauncher, ScalaClock}
import movingman.ladybug.model.LadybugModel
class RampApplication{
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "moving-man", "ladybug-2d", () => new LadybugModule[LadybugModel](new ScalaClock(30, 30 / 1000.0)))
  }
}