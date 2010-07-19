package edu.colorado.phet.therampscala

import charts.bargraphs.{WorkEnergyChartModel, WorkEnergyChart}
import charts.RampChartNode
import common.phetcommon.application.{PhetApplicationLauncher, Module, PhetApplicationConfig}
import common.piccolophet.{PiccoloPhetApplication}
import graphics.RampCanvas
import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{Color}
import javax.swing.{JButton, JFrame}
import model._
import controls.RampControlPanel
import robotmovingcompany.{RobotMovingCompanyGameModel, Result, RobotMovingCompanyCanvas}
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}

import scalacommon.ScalaClock

class AbstractRampModule(frame: JFrame, clock: ScalaClock, name: String, defaultBeadPosition: Double, pausedOnReset: Boolean,
        initialAngle:Double) extends Module(name, clock) {
  val model = new RampModel(defaultBeadPosition, pausedOnReset,initialAngle)
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
    resetAll()
  }

  def resetAll() = {}
}

class BasicRampModule(frame: JFrame, clock: ScalaClock, name: String,
                      coordinateSystemFeaturesEnabled: Boolean, useObjectComboBox: Boolean,
                      defaultBeadPosition: Double, pausedOnReset: Boolean,initialAngle:Double)
        extends AbstractRampModule(frame, clock, name, defaultBeadPosition, pausedOnReset,initialAngle) {
  val canvas = new RampCanvas(model, coordinateSystemModel, fbdModel, vectorViewModel, frame, !useObjectComboBox,initialAngle != 0.0)
  setSimulationPanel(canvas)
  val rampControlPanel = new RampControlPanel(model, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, model)
  setControlPanel(rampControlPanel)
  setClockControlPanel(new RecordModelControlPanel(model, canvas, () => new PlaybackSpeedSlider(model), Color.blue, 20))
}

import RampResources._
class IntroRampModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "module.introduction".translate, false, false, -6, false,RampDefaults.defaultRampAngle)

class CoordinatesRampModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "module.coordinates".translate, true, false, -6, false,RampDefaults.defaultRampAngle) {
  coordinateSystemModel.adjustable = true
}

class ForceGraphsModule(frame: JFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "module.force-graphs".translate, false)

class GraphingModule(frame: JFrame, clock: ScalaClock, name: String, showEnergyGraph: Boolean) extends BasicRampModule(frame, clock, name, false, true, -6, true,RampDefaults.defaultRampAngle) {
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
  override def resetAll() = {
    super.resetAll()
    didUnpause = false
  }
}

class WorkEnergyModule(frame: JFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "module.work-energy".translate, true) {
  val workEnergyChartModel = new WorkEnergyChartModel
  val jButton = new JButton("controls.showWorkEnergyCharts".translate)
  jButton.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = {workEnergyChartModel.visible = true}
  })
  rampControlPanel.addToBody(jButton)
  val chart = new WorkEnergyChart(workEnergyChartModel, model, frame)

  override def resetAll() = {super.reset(); workEnergyChartModel.reset()}
}

class RobotMovingCompanyModule(frame: JFrame, clock: ScalaClock) extends AbstractRampModule(frame, clock, "module.robotMovingCompany".translate, 5, false,RampDefaults.defaultRampAngle) {
  val gameModel = new RobotMovingCompanyGameModel(model, clock)

  gameModel.itemFinishedListeners += ((scalaRampObject, result) => {
    val audioClip = result match {
      case Result(_, true, _, _) => Some("smash0.wav".literal)
      case Result(true, false, _, _) => Some("tintagel/DIAMOND.WAV".literal)
      case Result(false, false, _, _) => Some("tintagel/PERSONAL.WAV".literal)
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
  addModule(new IntroRampModule(getPhetFrame, newClock))
  addModule(new CoordinatesRampModule(getPhetFrame, newClock))
  addModule(new ForceGraphsModule(getPhetFrame, newClock))
  addModule(new WorkEnergyModule(getPhetFrame, newClock))
  addModule(new RobotMovingCompanyModule(getPhetFrame, newClock))
}
class RampWorkEnergyApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(RampDefaults.DELAY, RampDefaults.DT_DEFAULT)
  addModule(new WorkEnergyModule(getPhetFrame, newClock))
}

class RobotMovingCompanyApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new RobotMovingCompanyModule(getPhetFrame, new ScalaClock(RampDefaults.DELAY, RampDefaults.DT_DEFAULT)))
}

//Current IntelliJ plugin has trouble finding main for classes with a companion object, so we use a different name 
object RampApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp".literal, classOf[RampApplication])
}
object RampWorkEnergyApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp".literal, classOf[RampWorkEnergyApplication])
}

object RobotMovingCompanyApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp".literal, "robot-moving-company".literal, classOf[RobotMovingCompanyApplication])
}

