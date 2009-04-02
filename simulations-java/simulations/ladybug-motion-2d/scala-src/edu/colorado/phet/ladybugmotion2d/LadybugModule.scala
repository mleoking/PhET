package edu.colorado.phet.ladybug2d

import canvas.{LadybugNode, LadybugSolidTraceNode, LadybugDotTraceNode, LadybugCanvas}
import controlpanel._
import model.LadybugModel
import model.LadybugMotionModel._

import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.common.phetcommon.model.clock.IClock
import java.awt.Color
import scalacommon.record.{PlaybackSpeedSlider, RecordModelControlPanel}
import scalacommon.ScalaClock
import umd.cs.piccolo.PNode

class LadybugModule[ModelType <: LadybugModel](clock: ScalaClock,
                                               _model: ModelType,
                                               newCanvas: LadybugModule[ModelType] => LadybugCanvas,
                                               newControlPanel: LadybugModule[ModelType] => LadybugControlPanel[ModelType],
                                               createRightControl: (LadybugModule[ModelType]) => PNode) //control used in right side of clock control panel
        extends Module("my module", clock) {

  //Auxiliary constructor, used for making a plain vanilla LadybugModule, rather than subclasses
  def this(clock: ScalaClock) = this (clock,
    (new LadybugModel).asInstanceOf[ModelType], //todo: why does compiler require cast here?
    (m: LadybugModule[ModelType]) => new LadybugCanvas(m.model, m.vectorVisibilityModel, m.pathVisibilityModel, 20, 20),
    (m: LadybugModule[ModelType]) => new LadybugControlPanel[ModelType](m),
    (m: LadybugModule[ModelType]) => new PlaybackSpeedSlider(m.model)
    )

  val model = _model
  val vectorVisibilityModel = new VectorVisibilityModel
  val pathVisibilityModel = new PathVisibilityModel
  val canvas = newCanvas(this)
  val controlPanel = newControlPanel(this)

  setSimulationPanel(canvas)

  setControlPanel(controlPanel)

  clock.addClockListener(model.update(_))

  setClockControlPanel(new RecordModelControlPanel(model, canvas, () => {createRightControl(this)}, LadybugColorSet.position, LadybugDefaults.timelineLengthSeconds))

  def getLadybugMotionModel = model.getLadybugMotionModel()

  def clearTrace() = canvas.clearTrace()

  def setMotionManual() = model.getLadybugMotionModel().motion = MANUAL //todo encapsulate

  def setLadybugDraggable(draggable: Boolean) = canvas.setLadybugDraggable(draggable)

  def resetAll() = {
    model.resetAll()
    vectorVisibilityModel.resetAll()
    pathVisibilityModel.resetAll()
    controlPanel.resetAll()
  }
}