package edu.colorado.phet.therampscala

import common.phetcommon.application.{PhetApplicationLauncher, Module, PhetApplicationConfig}
import graphics.RampCanvas
import model._
import controls.RampControlPanel
import robotmovingcompany.RobotMovingCompanyCanvas
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}

import java.awt.Color
import javax.swing.JFrame

import common.piccolophet.PiccoloPhetApplication
import scalacommon.ScalaClock

class AbstractRampModule(frame: JFrame, clock: ScalaClock, name: String) extends Module(name, clock) {
  val model = new RampModel
  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel
  val coordinateSystemModel = new CoordinateSystemModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListenerByName(if (coordinateSystemModel.fixed) model.coordinateFrameModel.angle = 0)
  def resetRampModule(): Unit = {
    model.resetAll()
    wordModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
  }
}
class RampModule(frame: JFrame, clock: ScalaClock) extends AbstractRampModule(frame, clock, "The Ramp") {
  val canvas = new RampCanvas(model, coordinateSystemModel, fbdModel, vectorViewModel, frame)
  setSimulationPanel(canvas)
  setControlPanel(new RampControlPanel(model, wordModel, fbdModel, coordinateSystemModel, vectorViewModel, resetRampModule))
  setClockControlPanel(new RecordModelControlPanel(model, canvas, () => new PlaybackSpeedSlider(model), Color.blue, 20))
  clock.addClockListener(model.update(_))
}

class RobotMovingCompanyModule(frame: JFrame, clock: ScalaClock) extends AbstractRampModule(frame, clock, "Robot Moving Company") {
  val gameModel = new robotmovingcompany.RobotMovingCompanyGameModel(model, clock) //todo: fix this with imports
  val canvas = new RobotMovingCompanyCanvas(model, coordinateSystemModel, fbdModel, vectorViewModel, frame, gameModel)

  setSimulationPanel(canvas)
  setLogoPanelVisible(false)
}

class RampApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new RampModule(getPhetFrame, new ScalaClock(30, RampDefaults.DT_DEFAULT)))
  addModule(new RobotMovingCompanyModule(getPhetFrame, new ScalaClock(30, RampDefaults.DT_DEFAULT)))
}

class RobotMovingCompanyApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new RobotMovingCompanyModule(getPhetFrame, new ScalaClock(30, RampDefaults.DT_DEFAULT)))
}

//Current IntelliJ plugin has trouble finding main for classes with a companion object, so we use a different name 
object RampApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp", classOf[RampApplication])
}

object RobotMovingCompanyApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp", "robot-moving-company", classOf[RobotMovingCompanyApplication])
}