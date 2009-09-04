package edu.colorado.phet.motionseries.sims.forcesandmotion

import charts._
import graphics.MotionSeriesCanvas
import model.MotionSeriesModel
import motionseries.MotionSeriesResources._

class ForcesAndMotionChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("forces.parallel-title".translate, forceGraph, false) ::
          Graph("acceleration", accelerationGraph, true) ::
          Graph("velocity", velocityGraph, true) ::
          Graph("position", positionGraph, true) :: Nil)
}