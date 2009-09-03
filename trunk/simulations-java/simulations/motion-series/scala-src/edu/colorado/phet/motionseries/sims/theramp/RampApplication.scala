package edu.colorado.phet.motionseries.sims.theramp

import charts.bargraphs.{WorkEnergyChartModel, WorkEnergyChart}
import java.awt.geom.Rectangle2D
import phet.common.phetcommon.application.{PhetApplicationLauncher, Module, PhetApplicationConfig}
import phet.common.piccolophet.{PiccoloPhetApplication}
import graphics.RampCanvas
import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{Color}
import javax.swing._
import model._
import controls.RampControlPanel
import robotmovingcompany.{RobotMovingCompanyGameModel, Result, RobotMovingCompanyCanvas}
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}

import scalacommon.ScalaClock

//TODO: improve inheritance/composition scheme for different applications/modules/canvases/models
class AbstractRampModule(frame: JFrame, clock: ScalaClock, name: String, defaultBeadPosition: Double, pausedOnReset: Boolean,
                         initialAngle: Double) extends Module(name, clock) {
  val rampModel = createRampModel(defaultBeadPosition, pausedOnReset, initialAngle)

  def createRampModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) = new RampModel(defaultBeadPosition, pausedOnReset, initialAngle)

  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel
  val coordinateSystemModel = new AdjustableCoordinateModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListenerByName(if (coordinateSystemModel.fixed) rampModel.coordinateFrameModel.angle = 0)
  private var lastTickTime = System.currentTimeMillis

  //This clock is always running; pausing just pauses the physics
  clock.addClockListener(dt => {
    val paintAndInputTime = System.currentTimeMillis - lastTickTime

    val startTime = System.currentTimeMillis
    rampModel.update(dt)
    RepaintManager.currentManager(getSimulationPanel).paintDirtyRegions()
    val modelTime = System.currentTimeMillis - startTime

    val elapsed = paintAndInputTime + modelTime
    if (elapsed < 25) {
      val toSleep = 25 - elapsed
      //      println("had excess time, sleeping: " + toSleep)
      Thread.sleep(toSleep) //todo: blocks swing event handler thread and paint thread, should run this clock loop in another thread
    }
    lastTickTime = System.currentTimeMillis
  })

  //pause on start/reset, and unpause (and start recording) when the user applies a force
  rampModel.setPaused(true)

  def resetRampModule(): Unit = {
    rampModel.resetAll()
    wordModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
    //pause on startup/reset, and unpause (and start recording) when the user applies a force 
    rampModel.setPaused(true)
    resetAll()
  }

  def resetAll() = {}
}

class BasicRampModule(frame: JFrame, clock: ScalaClock, name: String,
                      coordinateSystemFeaturesEnabled: Boolean, useObjectComboBox: Boolean, showAppliedForceSlider: Boolean,
                      defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double, rampLayoutArea:Rectangle2D)
        extends AbstractRampModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val rampCanvas = new RampCanvas(rampModel, coordinateSystemModel, fbdModel, vectorViewModel, frame,
    !useObjectComboBox, showAppliedForceSlider, initialAngle != 0.0, rampLayoutArea)
  setSimulationPanel(rampCanvas)
  val rampControlPanel = new RampControlPanel(rampModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, rampModel, true, true)
  setControlPanel(rampControlPanel)
  setClockControlPanel(new RecordModelControlPanel(rampModel, rampCanvas, () => new PlaybackSpeedSlider(rampModel), Color.blue, 20))
}

import RampResources._

class IntroRampModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "module.introduction".translate, false, false, true, -6, false, RampDefaults.defaultRampAngle, RampDefaults.defaultRampLayoutArea)

class CoordinatesRampModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "module.coordinates".translate, true, false, true, -6, false, RampDefaults.defaultRampAngle, RampDefaults.defaultRampLayoutArea) {
  coordinateSystemModel.adjustable = true
}

class GraphingModule(frame: JFrame, clock: ScalaClock, name: String, showEnergyGraph: Boolean, rampLayoutArea:Rectangle2D)
        extends BasicRampModule(frame, clock, name, false, true, false, -6, true, RampDefaults.defaultRampAngle, rampLayoutArea) {
  coordinateSystemModel.adjustable = false
  rampCanvas.addScreenNode(new RampChartNode(rampCanvas.transform, rampCanvas, rampModel, showEnergyGraph))
}

class ForceGraphsModule(frame: JFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "module.force-graphs".translate, false, RampDefaults.forceGraphRampLayoutArea)

class WorkEnergyModule(frame: JFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "module.work-energy".translate, true, RampDefaults.forceEnergyGraphRampLayoutArea) {
  val workEnergyChartModel = new WorkEnergyChartModel
  val workEnergyChartButton = new JButton("controls.showWorkEnergyCharts".translate)
  workEnergyChartButton.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = {workEnergyChartModel.visible = true}
  })
  rampControlPanel.addToBody(workEnergyChartButton)
  val workEnergyChart = new WorkEnergyChart(workEnergyChartModel, rampModel, frame)

  override def resetAll() = {super.reset(); workEnergyChartModel.reset()}
}

class RobotMovingCompanyModule(frame: JFrame, clock: ScalaClock)
        extends AbstractRampModule(frame, clock, "module.robotMovingCompany".translate, 5, false, RampDefaults.defaultRampAngle) {
  override def reset() = {
    super.reset()
    rampModel.frictionless = false
  }

  override def resetAll() = {
    super.resetAll()
    rampModel.frictionless = false
  }

  override def createRampModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) = {
    new RampModel(defaultBeadPosition, pausedOnReset, initialAngle) {
      override def updateSegmentLengths() = setSegmentLengths(rampLength, rampLength)
      frictionless = false
    }

  }

  val gameModel = new RobotMovingCompanyGameModel(rampModel, clock)

  gameModel.itemFinishedListeners += ((scalaRampObject, result) => {
    val audioClip = result match {
      case Result(_, true, _, _) => Some("smash0.wav".literal)
      case Result(true, false, _, _) => Some("tintagel/DIAMOND.WAV".literal)
      case Result(false, false, _, _) => Some("tintagel/PERSONAL.WAV".literal)
      case _ => None
    }
    if (!audioClip.isEmpty) RampResources.getAudioClip(audioClip.get).play()
  })

  val canvas = new RobotMovingCompanyCanvas(rampModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, gameModel)

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