package edu.colorado.phet.therampscala

import charts.RampChartNode
import common.phetcommon.application.{PhetApplicationLauncher, Module, PhetApplicationConfig}
import common.piccolophet.{PiccoloPhetApplication}
import graphics.RampCanvas
import java.awt.{Color}
import javax.swing.{JFrame}
import model._
import controls.RampControlPanel
import robotmovingcompany.{RobotMovingCompanyGameModel, Result, RobotMovingCompanyCanvas}
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}

import scalacommon.ScalaClock

class AbstractRampModule(frame: JFrame, clock: ScalaClock, name: String, defaultBeadPosition: Double) extends Module(name, clock) {
  val model = new RampModel(defaultBeadPosition)
  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel
  val coordinateSystemModel = new CoordinateSystemModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListenerByName(if (coordinateSystemModel.fixed) model.coordinateFrameModel.angle = 0)
  clock.addClockListener(model.update(_))
  def resetRampModule(): Unit = {
    model.resetAll()
    wordModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
  }
}

class BasicRampModule(frame: JFrame, clock: ScalaClock, name: String,
                      coordinateSystemFeaturesEnabled: Boolean, useObjectComboBox: Boolean, defaultBeadPosition: Double)
        extends AbstractRampModule(frame, clock, name, defaultBeadPosition) {
  val canvas = new RampCanvas(model, coordinateSystemModel, fbdModel, vectorViewModel, frame, !useObjectComboBox)
  setSimulationPanel(canvas)
  setControlPanel(new RampControlPanel(model, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, model))
  setClockControlPanel(new RecordModelControlPanel(model, canvas, () => new PlaybackSpeedSlider(model), Color.blue, 20))
}

class IntroRampModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "Intro", false, false, 5)

class CoordinatesRampModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "Coordinates", true, false, 5) {
  coordinateSystemModel.adjustable = true
}

class ForceGraphsModule(frame: JFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "Force Graphs", false)

class GraphingModule(frame: JFrame, clock: ScalaClock, name: String, showEnergyGraph: Boolean) extends BasicRampModule(frame, clock, name, false, true, -6) {
  coordinateSystemModel.adjustable = false
  canvas.addNodeAfter(canvas.earthNode, new RampChartNode(canvas.transform, canvas, model, showEnergyGraph))

  //pause on startup, and unpause (and start recording) when the user interacts with the sim 
  model.setPaused(true)
  var didUnpause = false
  model.bead.addListenerByName {
    if (!didUnpause) {
      model.setPaused(false)
      didUnpause = true
    }
  }
}

class WorkEnergyModule(frame: JFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "Work-Energy", true)

class RobotMovingCompanyModule(frame: JFrame, clock: ScalaClock) extends AbstractRampModule(frame, clock, "Robot Moving Company", 5) {
  val gameModel = new RobotMovingCompanyGameModel(model, clock)

  gameModel.itemFinishedListeners += ((scalaRampObject, result) => {
    val audioClip = result match {
      case Result(_, true, _, _) => Some("smash0.wav")
      case Result(true, false, _, _) => Some("tintagel/DIAMOND.WAV")
      case Result(false, false, _, _) => Some("tintagel/PERSONAL.WAV")
      case _ => None
    }
    if (!audioClip.isEmpty) RampResources.getAudioClip(audioClip.get).play()
  })

  val canvas = new RobotMovingCompanyCanvas(model, coordinateSystemModel, fbdModel, vectorViewModel, frame, gameModel)

  setSimulationPanel(canvas)
  setLogoPanelVisible(false)
}

class RampApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(RampDefaults.DELAY, RampDefaults.DT_DEFAULT)
  //todo: add back all modules
    addModule(new IntroRampModule(getPhetFrame, newClock))
    addModule(new CoordinatesRampModule(getPhetFrame, newClock))
  addModule(new ForceGraphsModule(getPhetFrame, newClock))
    addModule(new WorkEnergyModule(getPhetFrame, newClock))
    addModule(new RobotMovingCompanyModule(getPhetFrame, newClock))
}

class RobotMovingCompanyApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new RobotMovingCompanyModule(getPhetFrame, new ScalaClock(RampDefaults.DELAY, RampDefaults.DT_DEFAULT)))
}

//Current IntelliJ plugin has trouble finding main for classes with a companion object, so we use a different name 
object RampApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp", classOf[RampApplication])
}

object RobotMovingCompanyApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp", "robot-moving-company", classOf[RobotMovingCompanyApplication])
}