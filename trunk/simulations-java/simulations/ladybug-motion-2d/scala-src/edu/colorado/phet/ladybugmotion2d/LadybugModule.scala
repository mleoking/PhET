package edu.colorado.phet.ladybugmotion2d

import canvas.{LadybugNode, LadybugSolidTraceNode, LadybugDotTraceNode, LadybugCanvas}
import controlpanel._
import model.LadybugModel
import model.LadybugMotionModel._
import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.common.phetcommon.model.clock.IClock
import java.awt.Color
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.recordandplayback.gui.{RecordAndPlaybackControlPanel, PlaybackSpeedSlider}

class LadybugModule[ModelType <: LadybugModel](name: String, clock: ScalaClock,
                                               _model: ModelType,
                                               newCanvas: LadybugModule[ModelType] => LadybugCanvas,
                                               newControlPanel: LadybugModule[ModelType] => LadybugControlPanel[ModelType],
                                               createRightControl: (LadybugModule[ModelType]) => PNode) //control used in right side of clock control panel
        extends Module(name, clock) {

  //Auxiliary constructor, used for making a plain vanilla LadybugModule, rather than subclasses
  def this(clock: ScalaClock) = this ("ladybug-module", clock,
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

  clock.addClockListener(model.stepInTime(_))

  setClockControlPanel(new RecordAndPlaybackControlPanel(model, canvas, new RecordAndPlaybackControlPanel.Function{
    def createControl = createRightControl(LadybugModule.this)
  }, LadybugColorSet.position, LadybugDefaults.timelineLengthSeconds))

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