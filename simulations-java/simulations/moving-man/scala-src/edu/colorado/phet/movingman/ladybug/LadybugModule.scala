package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.application.Module
import _root_.edu.colorado.phet.common.phetcommon.model.clock.IClock
import java.awt.Color

class LadybugModule(clock: ScalaClock) extends Module("my module", clock) {
  val model = new LadybugModel
  val canvas = new LadybugCanvas
  private val vectorVisibilityModel = new VectorVisibilityModel
  private val pathVisibilityModel = new PathVisibilityModel
  private val ladybugMotionModel = new LadybugMotionModel
  setSimulationPanel(canvas)

  canvas setBackground new Color(200, 255, 240)
  clock.addClockListener(model.update(_))
  clock.addClockListener(ladybugMotionModel.update(_, model))
  canvas.addNode(new LadybugNode(model.ladybug, canvas.transform, vectorVisibilityModel))
  val solidTrace = new LadybugSolidTraceNode(model, canvas.transform, () => pathVisibilityModel.lineVisible, pathVisibilityModel)
  canvas.addNode(solidTrace)
  val dotTrace = new LadybugDotTraceNode(model, canvas.transform, () => pathVisibilityModel.dotsVisible, pathVisibilityModel)
  canvas.addNode(dotTrace)
  setControlPanel(new LadybugControlPanel(this))

  setClockControlPanel(new LadybugClockControlPanel)

  def getVectorVisibilityModel = vectorVisibilityModel

  def getPathVisibilityModel = pathVisibilityModel

  def getLadybugMotionModel = ladybugMotionModel

  def clearTrace = {
    solidTrace.clearTrace
    dotTrace.clearTrace
  }

  def setMotionManual() = ladybugMotionModel.motion = LadybugMotionModel.MANUAL
}