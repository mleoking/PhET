package edu.colorado.phet.motionseries.sims.movingman

import charts._
import graphics.MotionSeriesCanvas
import model.MotionSeriesModel

class MovingManChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("position", positionGraph, false) ::
          Graph("velocity", velocityGraph, false) ::
          Graph("acceleration", accelerationGraph, true) :: Nil)
}

class MovingManEnergyChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("velocity", velocityGraph, false) ::
          Graph("kinetic energy", kineticEnergyGraph, false) :: Nil)
}