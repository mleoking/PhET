package edu.colorado.phet.ladybugmotion2d.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Ellipse2D, GeneralPath, Point2D}
import model.{LadybugState, LadybugModel}

import scalacommon.util.Observable
import scalacommon.record.{DataPoint, RecordModel}
import umd.cs.piccolo.nodes.PPath
import umd.cs.piccolo.PNode
import java.awt.{BasicStroke, Color}
import java.lang.Math._

class LadybugDotTraceNode(model: LadybugModel, transform: ModelViewTransform2D,
                          shouldBeVisible: () => Boolean, observable: Observable, maxFade: Double)
        extends LadybugTraceNode(model, transform, shouldBeVisible, observable) {
  val node = new PNode
  addChild(node)

  update()

  class DotNode(point: Point2D, dt: Double) extends PNode {
    val color = toColor(dt, maxFade)
    val path = new PhetPPath(new Ellipse2D.Double(point.getX - 5, point.getY - 5, 10, 10), color)
    addChild(path)
  }

  def update() = {
    node.removeAllChildren
    val p = new GeneralPath
    implicit def historyToPoint(dataPoint: DataPoint[LadybugState]) = new Point2D.Float(dataPoint.state.position.x.toFloat, dataPoint.state.position.y.toFloat)

    if (model.getRecordingHistory.length > 0) {
      for (h <- getHistoryToShow()) {
        val pt: Point2D.Float = h
        val tx = transform.modelToView(pt)
        val dt = abs(model.getTime - h.time)
        node.addChild(new DotNode(tx, dt))
      }
    }
  }
}