package edu.colorado.phet.movingman.ladybug

import model.{LadybugModel, ScalaClock}
import scalacommon.ScalaApplicationLauncher

object Ladybug2DApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "moving-man", "ladybug-2d", () => new LadybugModule[LadybugModel](new ScalaClock(30, 30 / 1000.0)))
  }
}