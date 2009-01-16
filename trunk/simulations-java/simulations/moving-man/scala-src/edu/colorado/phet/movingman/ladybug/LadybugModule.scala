package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.application.Module
import _root_.edu.colorado.phet.common.phetcommon.model.clock.IClock
import java.awt.Color

class LadybugModule(clock: ScalaClock) extends Module("my module", clock) {
  val model = new LadybugModel
  val canvas = new LadybugCanvas
  private val vectorVisibilityModel = new VectorVisibilityModel
  private val pathVisibilityModel = new PathVisibilityModel
  setSimulationPanel(canvas)

  canvas setBackground new Color(200, 255, 240)
  clock.addClockListener(model.update(_))
  canvas.addNode(new LadybugNode(model.ladybug, canvas.transform, vectorVisibilityModel))
  canvas.addNode(new LadybugSolidTraceNode(model, canvas.transform, () => pathVisibilityModel.lineVisible, pathVisibilityModel))
  canvas.addNode(new LadybugDotTraceNode(model, canvas.transform, () => pathVisibilityModel.dotsVisible, pathVisibilityModel))
  setControlPanel(new LadybugControlPanel(this))

  setClockControlPanel(new LadybugClockControlPanel)

  def getVectorVisibilityModel = vectorVisibilityModel

  def getPathVisibilityModel = pathVisibilityModel
}