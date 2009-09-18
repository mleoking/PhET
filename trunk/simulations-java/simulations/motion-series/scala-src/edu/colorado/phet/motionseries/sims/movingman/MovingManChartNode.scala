package edu.colorado.phet.motionseries.sims.movingman

import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.colorado.phet.motionseries.charts.{Graph, MotionSeriesChartNode}

class MovingManChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("position", positionGraph, false) ::
          Graph("velocity", velocityGraph, false) ::
          Graph("acceleration", accelerationGraph, true) :: Nil)
}

class MovingManEnergyChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("velocity", velocityGraph, false) ::
          Graph("kinetic energy", kineticEnergyGraph, false) :: Nil)
}