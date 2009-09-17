package edu.colorado.phet.motionseries.sims.theramp

import charts.{Graph, MotionSeriesChartNode}
import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
import edu.colorado.phet.motionseries.model.{MotionSeriesModel}
import edu.colorado.phet.motionseries.MotionSeriesResources._

class RampForceEnergyChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("forces.parallel-title".translate, forceGraph, false) ::
          Graph("forces.work-energy-title".translate, energyGraph, false) :: Nil)
}

class RampForceChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("forces.parallel-title".translate, forceGraph, false) :: Nil)
}