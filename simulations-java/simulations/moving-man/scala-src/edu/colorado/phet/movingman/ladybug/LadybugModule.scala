package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.common.phetcommon.model.clock.IClock
import java.awt.Color

class LadybugModule(clock: ScalaClock) extends Module("my module", clock) {
  val model = new LadybugModel
  val canvas = new LadybugCanvas
  private val vectorVisibilityModel = new VectorVisibilityModel
  private val pathVisibilityModel = new PathVisibilityModel

  setSimulationPanel(canvas)

  canvas setBackground new Color(200, 255, 240)
  clock.addClockListener(model.update(_))

  canvas.addNode(new LadybugNode(model, model.ladybug, canvas.transform, vectorVisibilityModel))
  val solidTrace = new LadybugSolidTraceNode(model, canvas.transform, () => pathVisibilityModel.lineVisible, pathVisibilityModel)
  canvas.addNode(solidTrace)
  val dotTrace = new LadybugDotTraceNode(model, canvas.transform, () => pathVisibilityModel.dotsVisible, pathVisibilityModel)
  canvas.addNode(dotTrace)
  val controlPanel = new LadybugControlPanel(this)
  setControlPanel(controlPanel)

  setClockControlPanel(new LadybugClockControlPanel(this))

  def getVectorVisibilityModel = vectorVisibilityModel

  def getPathVisibilityModel = pathVisibilityModel

  def getLadybugMotionModel = model.getLadybugMotionModel()

  def clearTrace = {
    solidTrace.clearTrace
    dotTrace.clearTrace
  }

  def setMotionManual() = model.getLadybugMotionModel().motion = LadybugMotionModel.MANUAL //todo encapsulate

  def resetAll() = {
    model.resetAll()
    vectorVisibilityModel.resetAll()
    pathVisibilityModel.resetAll()
    controlPanel.resetAll()
  }
}