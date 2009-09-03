package edu.colorado.phet.motionseries.sims.forcesandmotion

import java.awt.geom.Rectangle2D
import phet.common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import phet.common.piccolophet.PiccoloPhetApplication
import controls.RampControlPanel
import model.{MotionSeriesModel, AdjustableCoordinateModel, FreeBodyDiagramModel, VectorViewModel}
import graphics._
import java.awt.Color
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}
import javax.swing.JFrame
import scalacommon.ScalaClock
import motionseries.MotionSeriesResources._
import umd.cs.piccolox.pswing.PSwing

class BasicForcesAndMotionModule(frame: JFrame,
                                 clock: ScalaClock,
                                 name: String,
                                 coordinateSystemFeaturesEnabled: Boolean,
                                 showObjectSelectionNode: Boolean,
                                 useObjectComboBox: Boolean,
                                 showAppliedForceSlider: Boolean,
                                 defaultBeadPosition: Double,
                                 pausedOnReset: Boolean,
                                 initialAngle: Double,
                                 showFrictionControl: Boolean,
                                 modelViewport: Rectangle2D)
        extends MotionSeriesModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val canvas = new BasicForcesAndMotionCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame,
    showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, modelViewport)
  setSimulationPanel(canvas)
  val controlPanel = new RampControlPanel(motionSeriesModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, motionSeriesModel, false, showFrictionControl)
  setControlPanel(controlPanel)
  setClockControlPanel(new RecordModelControlPanel(motionSeriesModel, canvas, () => new PlaybackSpeedSlider(motionSeriesModel), Color.blue, 20))
}

class BasicForcesAndMotionCanvas(model: MotionSeriesModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                                 vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                                 rampAngleDraggable: Boolean, modelViewport: Rectangle2D)
        extends RampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, showObjectSelectionNode, showAppliedForceSlider, rampAngleDraggable, modelViewport) {
  override def addHeightAndAngleIndicators() = {}
}

class IntroModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.intro.title".translate, false, true, false, true, -6, false, 0.0, true, MotionSeriesDefaults.forceMotionViewport)

class FrictionModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.friction.title".translate, false, false, false, true, -6, false, 0.0, false, MotionSeriesDefaults.frictionViewport) {
  val frictionPlayAreaControlPanel = new PSwing(new FrictionPlayAreaControlPanel(motionSeriesModel.bead))
  frictionPlayAreaControlPanel.setOffset(canvas.stage.width / 2 - frictionPlayAreaControlPanel.getFullBounds.getWidth / 2, canvas.stage.height - frictionPlayAreaControlPanel.getFullBounds.getHeight - 2)
  canvas.addStageNode(frictionPlayAreaControlPanel)
  motionSeriesModel.frictionless = false
}

class GraphingModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.graphing.title".translate, false, false, true, false, -6, false, 0.0, true, MotionSeriesDefaults.forceEnergyGraphViewport) {
  coordinateSystemModel.adjustable = false
  canvas.addScreenNode(new ForcesAndMotionChartNode(canvas, motionSeriesModel))
}

class RobotMovingCompany1DModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.robot-moving-company.title".translate, false, false, false, false, -6, false, 0.0, true, MotionSeriesDefaults.defaultViewport)

class ForcesAndMotionApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new FrictionModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
  addModule(new RobotMovingCompany1DModule(getPhetFrame, newClock))
}

object ForcesAndMotionApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "forces-and-friction".literal, classOf[ForcesAndMotionApplication])
}
