package edu.colorado.phet.motionseries.sims.forcesandmotion

import java.awt.geom.Rectangle2D
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication
import controls.RampControlPanel
import edu.colorado.phet.motionseries.model.{MotionSeriesModel, AdjustableCoordinateModel, FreeBodyDiagramModel, VectorViewModel}
import edu.colorado.phet.motionseries.graphics._
import java.awt.Color
import edu.colorado.phet.scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}
import javax.swing.JFrame
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.MotionSeriesResources._
import theramp.StageContainerArea
import edu.umd.cs.piccolox.pswing.PSwing

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
                                 showBounceControl:Boolean,
                                 modelViewport: Rectangle2D,
                                 stageContainerArea:StageContainerArea)
        extends MotionSeriesModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val canvas = new BasicForcesAndMotionCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame,
    showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, modelViewport,stageContainerArea)
  setSimulationPanel(canvas)
  val controlPanel = new RampControlPanel(motionSeriesModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, motionSeriesModel, false, showFrictionControl,showBounceControl)
  setControlPanel(controlPanel)
  setClockControlPanel(new RecordModelControlPanel(motionSeriesModel, canvas, () => new PlaybackSpeedSlider(motionSeriesModel), Color.blue, 20))
}

class BasicForcesAndMotionCanvas(model: MotionSeriesModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                                 vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                                 rampAngleDraggable: Boolean, modelViewport: Rectangle2D,stageContainerArea:StageContainerArea)
        extends RampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, showObjectSelectionNode, showAppliedForceSlider, rampAngleDraggable, modelViewport,stageContainerArea) {
  override def addHeightAndAngleIndicators() = {}
}

class IntroModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-motion.module.intro.title".translate, false, true, false, true, -6, false, 0.0, true, true,MotionSeriesDefaults.forceMotionViewport,MotionSeriesDefaults.forceMotionArea)

class FrictionModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-motion.module.friction.title".translate, false, false, false, true, -6, false, 0.0, false, true,MotionSeriesDefaults.forceMotionFrictionViewport,MotionSeriesDefaults.forceMotionFrictionArea) {
  val frictionPlayAreaControlPanel = new PSwing(new FrictionPlayAreaControlPanel(motionSeriesModel.bead))
  frictionPlayAreaControlPanel.setOffset(canvas.stage.width / 2 - frictionPlayAreaControlPanel.getFullBounds.getWidth / 2, canvas.stage.height - frictionPlayAreaControlPanel.getFullBounds.getHeight - 2)
  canvas.addStageNode(frictionPlayAreaControlPanel)
  motionSeriesModel.frictionless = false
}

class GraphingModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-motion.module.graphing.title".translate, false, false, true, false, -6, false, 0.0, true, true,MotionSeriesDefaults.forceMotionGraphViewport,MotionSeriesDefaults.forceEnergyGraphArea) {
  coordinateSystemModel.adjustable = false
  canvas.addScreenNode(new ForcesAndMotionChartNode(canvas, motionSeriesModel))
}

class RobotMovingCompany1DModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-motion.module.robot-moving-company.title".translate, false, false, false, false, -6, false, 0.0, true, true,MotionSeriesDefaults.defaultViewport,MotionSeriesDefaults.fullScreenArea)

class ForcesAndMotionApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new FrictionModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
  addModule(new RobotMovingCompany1DModule(getPhetFrame, newClock))
}

object ForcesAndMotionApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "forces-and-motion".literal, classOf[ForcesAndMotionApplication])
}
