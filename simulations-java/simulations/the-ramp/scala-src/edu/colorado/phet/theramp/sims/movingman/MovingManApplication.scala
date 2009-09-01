package edu.colorado.phet.theramp.sims.movingman

import common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import common.piccolophet.PiccoloPhetApplication
import controls.RampControlPanel
import forcesandmotion.{ForcesAndMotionApplication, ForcesAndMotionChartNode}

import model.{RampModel, AdjustableCoordinateModel, FreeBodyDiagramModel, VectorViewModel}
import graphics._
import java.awt.Color
import phet.theramp.RampResources
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}
import javax.swing.JFrame
import scalacommon.ScalaClock
import RampResources._
import theramp.{AbstractRampModule, RampDefaults}

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
                           showFrictionControl: Boolean)
        extends AbstractRampModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val canvas = new BasicMovingManCanvas(rampModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, modelOffsetY)
  setSimulationPanel(canvas)
  val controlPanel = new RampControlPanel(rampModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, rampModel, false, showFrictionControl)
  setControlPanel(controlPanel)
  setClockControlPanel(new RecordModelControlPanel(rampModel, canvas, () => new PlaybackSpeedSlider(rampModel), Color.blue, 20))
}

class BasicMovingManCanvas(model: RampModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                           vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                           rampAngleDraggable: Boolean, modelOffsetY: Double)
        extends RampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, showObjectSelectionNode, showAppliedForceSlider, rampAngleDraggable, modelOffsetY) {
  override def addHeightAndAngleIndicators() = {}
}

class IntroModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "forces-and-friction.module.intro.title".translate, false, true, false, true, -6, false, 0.0, 0.0, true)
class GraphingModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "forces-and-friction.module.graphing.title".translate, false, false, true, false, -6, false, 0.0, 0.0, true) {
  coordinateSystemModel.adjustable = false
  canvas.addNodeAfter(canvas.earthNode, new ForcesAndMotionChartNode(canvas.transform, canvas, rampModel))
}
class RobotMovingCompany1DModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "forces-and-friction.module.robot-moving-company.title".translate, false, false, false, false, -6, false, 0.0, 0.0, true)

class MovingManApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(RampDefaults.DELAY, RampDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
  addModule(new RobotMovingCompany1DModule(getPhetFrame, newClock))
}

object MovingManApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp".literal, "forces-and-friction".literal, classOf[MovingManApplication])
}
