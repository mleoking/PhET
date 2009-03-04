package edu.colorado.phet.therampscala

import common.phetcommon.application.Module
import common.phetcommon.model.BaseModel
import common.piccolophet.PhetPCanvas
import movingman.ladybug.LadybugModule
import scalacommon.{ScalaApplicationLauncher, ScalaClock}
import movingman.ladybug.model.LadybugModel

class RampModel
class RampModule(clock: ScalaClock) extends Module("Ramp", clock) {
  val model = new RampModel
  setSimulationPanel(new PhetPCanvas)
  setModel(new BaseModel)
}
object RampApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "the-ramp", "the-ramp", () => new RampModule(new ScalaClock(30, 30 / 1000.0)))
  }
}