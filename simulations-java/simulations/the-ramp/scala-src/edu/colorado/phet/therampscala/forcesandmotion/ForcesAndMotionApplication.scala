package edu.colorado.phet.therampscala.forcesandmotion

import common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import common.piccolophet.PiccoloPhetApplication
import controls.RampControlPanel
import model.{RampModel, CoordinateSystemModel, FreeBodyDiagramModel, VectorViewModel}



import graphics._


import java.awt.Color
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}

import javax.swing.JFrame
import scalacommon.ScalaClock
import RampResources._

class BasicForcesAndMotionModule(frame: JFrame,
                                 clock: ScalaClock,
                                 name: String,
                                 coordinateSystemFeaturesEnabled: Boolean,
                                 useObjectComboBox: Boolean,
                                 defaultBeadPosition: Double,
                                 pausedOnReset: Boolean,
                                 initialAngle: Double,
                                 modelOffsetY: Double)
        extends AbstractRampModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val rampCanvas = new BasicForcesAndMotionCanvas(rampModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, !useObjectComboBox, initialAngle != 0.0, modelOffsetY)
  setSimulationPanel(rampCanvas)
  val rampControlPanel = new RampControlPanel(rampModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, rampModel, false)
  setControlPanel(rampControlPanel)
  setClockControlPanel(new RecordModelControlPanel(rampModel, rampCanvas, () => new PlaybackSpeedSlider(rampModel), Color.blue, 20))
}

class BasicForcesAndMotionCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                                 vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean,
                                 rampAngleDraggable: Boolean, modelOffsetY: Double)
        extends RampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, showObjectSelectionNode, rampAngleDraggable, modelOffsetY) {
  override def addHeightAndAngleIndicators() = {}
}

class IntroModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.intro.title".translate, false, false, -6, false, 0.0, 0.0)
class FrictionModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.friction.title".translate, false, false, -6, false, 0.0, 0.0)
class GraphingModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.graphing.title".translate, false, false, -6, false, 0.0, 0.0)
class RobotMovingCompany1DModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.robot-moving-company.title".translate, false, false, -6, false, 0.0, 0.0)

class ForcesAndMotionApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(RampDefaults.DELAY, RampDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new FrictionModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
  addModule(new RobotMovingCompany1DModule(getPhetFrame, newClock))
}

object ForcesAndMotionApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp".literal, "forces-and-friction".literal, classOf[ForcesAndMotionApplication])
}
