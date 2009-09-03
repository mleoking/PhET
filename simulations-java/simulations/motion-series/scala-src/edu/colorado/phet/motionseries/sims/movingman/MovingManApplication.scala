package edu.colorado.phet.motionseries.sims.movingman

import java.awt.geom.Rectangle2D
import phet.common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import phet.common.piccolophet.PiccoloPhetApplication
import controls.RampControlPanel
import forcesandmotion.{ForcesAndMotionChartNode}

import model.{RampModel, AdjustableCoordinateModel, FreeBodyDiagramModel, VectorViewModel}
import graphics._
import java.awt.Color
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}
import javax.swing.JFrame
import scalacommon.ScalaClock
import RampResources._
import theramp.{RampDefaults, AbstractRampModule}

class BasicMovingManModule(frame: JFrame,
                           clock: ScalaClock,
                           name: String,
                           coordinateSystemFeaturesEnabled: Boolean,
                           showObjectSelectionNode: Boolean,
                           useObjectComboBox: Boolean,
                           showAppliedForceSlider: Boolean,
                           defaultBeadPosition: Double,
                           pausedOnReset: Boolean,
                           initialAngle: Double,
                           modelOffsetY: Double,
                           showFrictionControl: Boolean,
                           rampLayoutArea:Rectangle2D)
        extends AbstractRampModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val canvas = new BasicMovingManCanvas(rampModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, modelOffsetY,rampLayoutArea)
  setSimulationPanel(canvas)
  val controlPanel = new RampControlPanel(rampModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, rampModel, false, showFrictionControl)
  setControlPanel(controlPanel)
  setClockControlPanel(new RecordModelControlPanel(rampModel, canvas, () => new PlaybackSpeedSlider(rampModel), Color.blue, 20))
}

class BasicMovingManCanvas(model: RampModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                           vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                           rampAngleDraggable: Boolean, modelOffsetY: Double,rampLayoutArea:Rectangle2D)
        extends RampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, showObjectSelectionNode, showAppliedForceSlider, rampAngleDraggable, modelOffsetY,rampLayoutArea) {
  override def addHeightAndAngleIndicators() = {}
}

class IntroModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "moving-man.module.intro.title".translate, false, true, false, true, -6, false, 0.0, 0.0, true,RampDefaults.defaultRampLayoutArea)

class GraphingModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "moving-man.module.graphing.title".translate, false, false, true, false, -6, false, 0.0, 0.0, true,RampDefaults.defaultRampLayoutArea) {
  coordinateSystemModel.adjustable = false
  canvas.addScreenNode(new ForcesAndMotionChartNode(canvas.transform, canvas, rampModel))
}

class MovingManGameModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "moving-man.module.game.title".translate, false, false, false, false, -6, false, 0.0, 0.0, true,RampDefaults.defaultRampLayoutArea)

class MovingManApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(RampDefaults.DELAY, RampDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
  addModule(new MovingManGameModule(getPhetFrame, newClock))
}

object MovingManApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "moving-man".literal, classOf[MovingManApplication])
}
