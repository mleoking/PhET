package edu.colorado.phet.motionseries.sims.forcesandmotion

import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.charts.{Graph, MotionSeriesChartNode}

class ForcesAndMotionChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("forces.parallel-title".translate, forceGraph(false), false) ::
          Graph("acceleration", accelerationGraph(false), true) ::
          Graph("velocity", velocityGraph(false), true) ::
          Graph("position", positionGraph(false), true) :: Nil)
}