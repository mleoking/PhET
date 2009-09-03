package edu.colorado.phet.motionseries.sims.movingman

import charts._
import graphics.MotionSeriesCanvas
import model.RampModel
import phet.common.motion.graphs._

class MovingManChartNode(canvas: MotionSeriesCanvas, model: RampModel) extends MotionSeriesChartNode(canvas, model) {
  val graphs = Array(
    new MinimizableControlGraph("position", positionGraph, false),
    new MinimizableControlGraph("velocity", velocityGraph, false),
    new MinimizableControlGraph("acceleration", accelerationGraph, true) )

  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(graphs))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
    setAlignedLayout()
  }

  addChild(graphSetNode)
  updateLayout()
}