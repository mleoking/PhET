package edu.colorado.phet.movingman.ladybug

import canvas.{LadybugNode, LadybugSolidTraceNode, LadybugDotTraceNode, LadybugCanvas}
import controlpanel._
import model.LadybugModel
import model.LadybugMotionModel._

import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.common.phetcommon.model.clock.IClock
import java.awt.Color
import scalacommon.ScalaClock
import umd.cs.piccolo.PNode

class LadybugModule[ModelType <: LadybugModel](clock: ScalaClock,
                                              newModel: () => ModelType,
                                              newCanvas: LadybugModule[ModelType] => LadybugCanvas,
                                              newControlPanel: LadybugModule[ModelType] => LadybugControlPanel[ModelType],
                                              createRightControl: (LadybugModule[ModelType]) => PNode)
        extends Module("my module", clock) {
  def this(clock: ScalaClock) = this (clock,
    () => (new LadybugModel).asInstanceOf[ModelType],
    (m: LadybugModule[ModelType]) => new LadybugCanvas(m.model, m.getVectorVisibilityModel, m.getPathVisibilityModel, 20, 20),
    (m: LadybugModule[ModelType]) => new LadybugControlPanel[ModelType](m),
    (m: LadybugModule[ModelType]) => new PlaybackSpeedSlider(m.model)
    )

  final val model = newModel()
  private val vectorVisibilityModel = new VectorVisibilityModel
  private val pathVisibilityModel = new PathVisibilityModel

  val canvas = newCanvas(this)

  setSimulationPanel(canvas)

  clock.addClockListener(model.update(_))

  val controlPanel = newControlPanel(this)
  setControlPanel(controlPanel)

  setClockControlPanel(new LadybugClockControlPanel(this, () => {createRightControl(this)}))

  def getVectorVisibilityModel = vectorVisibilityModel

  def getPathVisibilityModel = pathVisibilityModel

  def getLadybugMotionModel = model.getLadybugMotionModel()

  def clearTrace() = canvas.clearTrace()

  def setMotionManual() = model.getLadybugMotionModel().motion = MANUAL //todo encapsulate

  def resetAll() = {
    model.resetAll()
    vectorVisibilityModel.resetAll()
    pathVisibilityModel.resetAll()
    controlPanel.resetAll()
  }

  def setLadybugDraggable(draggable: Boolean) = canvas.setLadybugDraggable(draggable)
}