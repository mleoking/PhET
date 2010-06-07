package edu.colorado.phet.motionseries.sims.movingman

import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.colorado.phet.motionseries.charts.{Graph, MotionSeriesChartNode}

//todo: il8n
class MovingManChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("position", positionGraph(true), false) ::
          Graph("velocity", velocityGraph(true), false) ::
          Graph("acceleration", accelerationGraph(true), true) :: Nil)
}

class MovingManEnergyChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("velocity", velocityGraph(true), false) ::
          Graph("kinetic energy", kineticEnergyGraph, false) :: Nil)
}