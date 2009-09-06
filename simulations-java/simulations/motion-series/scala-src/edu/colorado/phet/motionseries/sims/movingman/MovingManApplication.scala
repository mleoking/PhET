package edu.colorado.phet.motionseries.sims.movingman

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import model._
import java.awt.geom.Rectangle2D
import phet.common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import phet.common.piccolophet.PiccoloPhetApplication
import graphics._
import java.awt.Color
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}
import javax.swing.JFrame
import scalacommon.ScalaClock
import motionseries.MotionSeriesResources._

class BasicMovingManModule(frame: JFrame,
                           clock: ScalaClock,
                           name: String,
                           coordinateSystemFeaturesEnabled: Boolean,
                           showObjectSelectionNode: Boolean,
                           useObjectComboBox: Boolean, //todo: unused
                           showAppliedForceSlider: Boolean,
                           defaultBeadPosition: Double,
                           pausedOnReset: Boolean,
                           initialAngle: Double,
                           showFrictionControl: Boolean,
                           rampLayoutArea: Rectangle2D)
        extends MotionSeriesModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val canvas = new MovingManCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, rampLayoutArea)
  setSimulationPanel(canvas)
  //  val controlPanel = new RampControlPanel(motionSeriesModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
  //    resetRampModule, coordinateSystemFeaturesEnabled, false, motionSeriesModel, false, showFrictionControl)
  //  setControlPanel(controlPanel)
  setClockControlPanel(new RecordModelControlPanel(motionSeriesModel, canvas, () => new PlaybackSpeedSlider(motionSeriesModel), Color.blue, 20))
  motionSeriesModel.selectedObject = MotionSeriesDefaults.movingMan
  vectorViewModel.xyComponentsVisible = false
  vectorViewModel.originalVectors = false
  vectorViewModel.parallelComponents = false
}

class MovingManCanvas(model: MotionSeriesModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                      vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                      rampAngleDraggable: Boolean, rampLayoutArea: Rectangle2D)
        extends RampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, showObjectSelectionNode, showAppliedForceSlider, rampAngleDraggable, rampLayoutArea) {
  override def addHeightAndAngleIndicators() = {}

  override def createBeadNode(b: Bead, t: ModelViewTransform2D, s: String, listener: () => Unit) = new PositionDragBeadNode(b, t, "moving-man/moving-man-standing.gif", "moving-man/moving-man-left.gif", listener, this)

  playAreaVectorNode.addVector(new PlayAreaAdapter(model.bead.velocityVector, MotionSeriesDefaults.PLAY_AREA_VELOCITY_VECTOR_SCALE),
    new TailLocationInPlayArea(model.bead, vectorViewModel, 0.5, model.bead.velocityVector))

  playAreaVectorNode.addVector(new PlayAreaAdapter(model.bead.accelerationVector, MotionSeriesDefaults.PLAY_AREA_ACCELERATION_VECTOR_SCALE),
    new TailLocationInPlayArea(model.bead, vectorViewModel, 3, model.bead.accelerationVector))
}

class IntroModule(frame: JFrame, clock: ScalaClock)
        extends BasicMovingManModule(frame, clock, "moving-man.module.intro.title".translate, false, false, false, false,
          -6, false, 0.0, true, MotionSeriesDefaults.movingManIntroViewport)

class GraphingModule(frame: JFrame, clock: ScalaClock)
        extends BasicMovingManModule(frame, clock, "moving-man.module.graphing.title".translate, false, false, true, false,
          -6, false, 0.0, true, MotionSeriesDefaults.forceMotionViewport) {
  coordinateSystemModel.adjustable = false
  canvas.addScreenNode(new MovingManChartNode(canvas, motionSeriesModel))
}

class EnergyModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "moving-man.module.energy.title".translate, false, false, true, false, -6, false, 0.0, true, MotionSeriesDefaults.forceMotionViewport) {
  coordinateSystemModel.adjustable = false
  canvas.addScreenNode(new MovingManEnergyChartNode(canvas, motionSeriesModel))
}

class MovingManGameModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "moving-man.module.game.title".translate, false, false, false, false, -6, false, 0.0, true, MotionSeriesDefaults.forceMotionViewport)

class MovingManApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
  addModule(new EnergyModule(getPhetFrame, newClock))
  addModule(new MovingManGameModule(getPhetFrame, newClock))
}

object MovingManApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "moving-man".literal, classOf[MovingManApplication])
}
