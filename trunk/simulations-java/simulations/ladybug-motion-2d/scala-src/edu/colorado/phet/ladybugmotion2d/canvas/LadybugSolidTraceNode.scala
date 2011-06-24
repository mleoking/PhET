//package edu.colorado.phet.ladybugmotion2d.canvas
//
//import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
//import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
//import java.awt.geom.{GeneralPath, Point2D}
//import java.awt.BasicStroke
//import edu.colorado.phet.ladybugmotion2d.model.{LadybugState, LadybugModel}
//import edu.colorado.phet.scalacommon.util.Observable
//import edu.colorado.phet.ladybugmotion2d.LadybugColorSet
//import edu.colorado.phet.recordandplayback.model.DataPoint
//
//class LadybugSolidTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: Observable) extends LadybugTraceNode(model, transform, shouldBeVisible, observable) {
//  val path = new PhetPPath(new BasicStroke(4), LadybugColorSet.position)
//  addChild(path)
//
//  update()
//
//  def update() {
//    val p = new GeneralPath
//    implicit def historyToPoint(dataPoint: DataPoint[LadybugState]) = new Point2D.Float(dataPoint.getState.position.x.toFloat, dataPoint.getState.position.y.toFloat)
//
//    val historyToShow = getHistoryToShow
//    if ( historyToShow.size > 0 ) {
//      val t = transform.modelToView(historyToShow.get(0))
//      p.moveTo(t.x, t.y)
//      for ( hIndex <- 0 until historyToShow.size ) {
//        //todo: should skip first point from moveTo
//        val h = historyToShow.get(hIndex)
//        val pt: Point2D.Float = h
//        val tx = transform.modelToView(pt)
//        p.lineTo(tx.getX.toFloat, tx.getY.toFloat)
//      }
//    }
//    path.setPathTo(p)
//  }
//
//}