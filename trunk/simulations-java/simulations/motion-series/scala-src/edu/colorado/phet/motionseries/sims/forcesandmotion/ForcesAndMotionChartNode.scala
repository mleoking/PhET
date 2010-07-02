//package edu.colorado.phet.motionseries.sims.forcesandmotion
//
//import edu.colorado.phet.motionseries.graphics.MotionSeriesCanvas
//import edu.colorado.phet.motionseries.model.MotionSeriesModel
//import edu.colorado.phet.motionseries.MotionSeriesResources._
//import edu.colorado.phet.motionseries.charts.{Graph, MotionSeriesChartNode}
//
//class ForcesAndMotionChartNode(canvas: MotionSeriesCanvas, model: MotionSeriesModel) extends MotionSeriesChartNode(canvas, model) {
//  init(Graph("forces.parallel-title".translate, forceGraph(false).toMinimizableControlChart, false) ::
//          Graph("properties.acceleration".translate, accelerationGraph(false).toMinimizableControlChart, true) ::
//          Graph("properties.velocity".translate, velocityGraph(false).toMinimizableControlChart, true) ::
//          Graph("properties.position".translate, positionGraph(false).toMinimizableControlChart, true) :: Nil)
//}