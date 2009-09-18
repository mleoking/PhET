package edu.colorado.phet.motionseries.sims.theramp

import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
import edu.colorado.phet.motionseries.model.{MotionSeriesModel}
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.charts.{Graph, MotionSeriesChartNode}

class RampForceEnergyChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("forces.parallel-title".translate, forceGraph, false) ::
          Graph("forces.work-energy-title".translate, energyGraph, false) :: Nil)
}

class RampForceChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
  init(Graph("forces.parallel-title".translate, forceGraph, false) :: Nil)
}