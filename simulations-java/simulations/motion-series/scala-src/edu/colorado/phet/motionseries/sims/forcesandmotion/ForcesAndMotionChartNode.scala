package edu.colorado.phet.motionseries.sims.forcesandmotion

import charts._
import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.colorado.phet.motionseries.MotionSeriesResources._

class ForcesAndMotionChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("forces.parallel-title".translate, forceGraph(false), false) ::
          Graph("acceleration", accelerationGraph, true) ::
          Graph("velocity", velocityGraph, true) ::
          Graph("position", positionGraph, true) :: Nil)
}