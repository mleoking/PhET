package edu.colorado.phet.therampscala.forcesandmotion

import common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import common.piccolophet.PiccoloPhetApplication
import controls.RampControlPanel
import java.awt.event.{ComponentEvent, ComponentAdapter}

import model.{RampModel, AdjustableCoordinateModel, FreeBodyDiagramModel, VectorViewModel}
import graphics._
import java.awt.Color
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}
import javax.swing.JFrame
import scalacommon.ScalaClock
import RampResources._
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
                                 modelOffsetY: Double,
                                 showFrictionControl: Boolean)
        extends AbstractRampModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val canvas = new BasicForcesAndMotionCanvas(rampModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, modelOffsetY)
  setSimulationPanel(canvas)
  val controlPanel = new RampControlPanel(rampModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, rampModel, false, showFrictionControl)
  setControlPanel(controlPanel)
  setClockControlPanel(new RecordModelControlPanel(rampModel, canvas, () => new PlaybackSpeedSlider(rampModel), Color.blue, 20))
}

class BasicForcesAndMotionCanvas(model: RampModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                                 vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                                 rampAngleDraggable: Boolean, modelOffsetY: Double)
        extends RampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, showObjectSelectionNode, showAppliedForceSlider, rampAngleDraggable, modelOffsetY) {
  override def addHeightAndAngleIndicators() = {}
}

class IntroModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.intro.title".translate, false, true, false, true, -6, false, 0.0, 0.0,true)
class FrictionModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.friction.title".translate, false, false, false, true, -6, false, 0.0, 0.0,false) {
  val frictionPlayAreaControlPanel = new PSwing(new FrictionPlayAreaControlPanel(rampModel.bead))
  canvas.addScreenChild(frictionPlayAreaControlPanel)
  def updateFrictionControl() = {
    val s = canvas.getScale * 1.5
    //todo: not sure if this is the best layout strategy here
    frictionPlayAreaControlPanel.setScale(if (s > 0) s else 1.0)
    frictionPlayAreaControlPanel.setOffset(canvas.getWidth / 2 - frictionPlayAreaControlPanel.getFullBounds.getWidth / 2,
      canvas.getHeight - frictionPlayAreaControlPanel.getFullBounds.getHeight)
  }

  canvas.addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = updateFrictionControl()
  })

  updateFrictionControl()
  rampModel.frictionless = false
}
class GraphingModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.graphing.title".translate, false, false, true, false, -6, false, 0.0, 0.0,true){
  coordinateSystemModel.adjustable = false
  canvas.addNodeAfter(canvas.earthNode, new ForcesAndMotionChartNode(canvas.transform, canvas, rampModel))
}
class RobotMovingCompany1DModule(frame: JFrame, clock: ScalaClock) extends BasicForcesAndMotionModule(frame, clock, "forces-and-friction.module.robot-moving-company.title".translate, false, false, false, false, -6, false, 0.0, 0.0,true)

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
