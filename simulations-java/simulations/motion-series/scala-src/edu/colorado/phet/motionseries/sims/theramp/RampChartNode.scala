package edu.colorado.phet.motionseries.sims.theramp

import charts.{MotionSeriesChartNode}
import graphics.MotionSeriesCanvas
import phet.common.motion.graphs._
import model.{RampModel}
import RampResources._

class RampForceEnergyChartNode(canvas: MotionSeriesCanvas, model: RampModel) extends MotionSeriesChartNode(canvas, model) {
  val parallelForcesString = "forces.parallel-title".translate
  val graphs = Array(new MinimizableControlGraph(parallelForcesString, forceGraph),
    new MinimizableControlGraph("forces.work-energy-title".translate, energyGraph))
  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(graphs))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
    setAlignedLayout()
  }

  addChild(graphSetNode)
  updateLayout()
}

class RampForceChartNode(canvas: MotionSeriesCanvas, model: RampModel) extends MotionSeriesChartNode(canvas, model) {
  val parallelForcesString = "forces.parallel-title".translate
  val graphs = Array(new MinimizableControlGraph(parallelForcesString, forceGraph))
  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(graphs))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
    setAlignedLayout()
  }

  addChild(graphSetNode)
  updateLayout()
}