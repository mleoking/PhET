package edu.colorado.phet.movingman.ladybug

import canvas.{LadybugNode, LadybugSolidTraceNode, LadybugDotTraceNode, LadybugCanvas}
import model.{LadybugModel, ScalaClock}
import model.LadybugMotionModel._

import controlpanel.{LadybugClockControlPanel, PathVisibilityModel, VectorVisibilityModel, LadybugControlPanel}
import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.common.phetcommon.model.clock.IClock
import java.awt.Color

class LadybugModule(clock: ScalaClock) extends Module("my module", clock) {
  val model = new LadybugModel
  private val vectorVisibilityModel = new VectorVisibilityModel
  private val pathVisibilityModel = new PathVisibilityModel

  val canvas = new LadybugCanvas(model, vectorVisibilityModel, pathVisibilityModel)

  setSimulationPanel(canvas)

  clock.addClockListener(model.update(_))

  val controlPanel = new LadybugControlPanel(this)
  setControlPanel(controlPanel)

  setClockControlPanel(new LadybugClockControlPanel(this))

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