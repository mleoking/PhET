package edu.colorado.phet.motionseries.sims.forcesandmotion

import charts._
import graphics.MotionSeriesCanvas
import model.RampModel
import phet.common.motion.graphs._
import phet.motionseries.RampResources
import RampResources._

class ForcesAndMotionChartNode(canvas: MotionSeriesCanvas, model: RampModel)
        extends MotionSeriesChartNode(canvas, model) {
  val graphs = Array(new MinimizableControlGraph("forces.parallel-title".translate, forceGraph)
    //    ,new MinimizableControlGraph("forces.work-energy-title".translate, workEnergyGraph))
    , new MinimizableControlGraph("acceleration", accelerationGraph, true)
    , new MinimizableControlGraph("velocity", velocityGraph, true)
    , new MinimizableControlGraph("position", positionGraph, true)
    )

  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(graphs))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
    setAlignedLayout()
  }

  addChild(graphSetNode)
  updateLayout()
}