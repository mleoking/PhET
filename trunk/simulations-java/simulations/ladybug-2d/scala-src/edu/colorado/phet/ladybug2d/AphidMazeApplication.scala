package edu.colorado.phet.ladybug2d

import aphidmaze.AphidMazeModule
import scalacommon.{ScalaApplicationLauncher, ScalaClock}

//aphid maze is a tab in ladybug 2d, just an application during development to facilitate deployment and testing
object AphidMazeApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "ladybug-2d", "aphid-maze", () => new AphidMazeModule(new ScalaClock(30, 30 / 1000.0)))
  }
}