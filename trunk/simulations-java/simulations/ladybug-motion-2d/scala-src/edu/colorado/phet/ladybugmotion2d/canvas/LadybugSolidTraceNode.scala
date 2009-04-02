package edu.colorado.phet.ladybugmotion2d.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{GeneralPath, Point2D}
import java.awt.{BasicStroke, Color}
import model.{LadybugState, LadybugModel}

import scalacommon.util.Observable
import scalacommon.record.{DataPoint, RecordModel}
import umd.cs.piccolo.PNode

class LadybugSolidTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: Observable) extends LadybugTraceNode(model, transform, shouldBeVisible, observable) {
  val path = new PhetPPath(new BasicStroke(4), LadybugColorSet.position)
  addChild(path)

  update()

  def update() = {
    val p = new GeneralPath
    implicit def historyToPoint(dataPoint: DataPoint[LadybugState]) = new Point2D.Float(dataPoint.state.position.x.toFloat, dataPoint.state.position.y.toFloat)

    val historyToShow = getHistoryToShow()
    if (historyToShow.length > 0) {
      val t = transform.modelToView(historyToShow(0))
      p.moveTo(t.x, t.y)
      for (h <- historyToShow) { //todo: should skip first point from moveTo
        val pt: Point2D.Float = h
        val tx = transform.modelToView(pt)
        p.lineTo(tx.getX.toFloat, tx.getY.toFloat)
      }
    }
    path.setPathTo(p)
  }

}