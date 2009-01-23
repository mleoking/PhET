package edu.colorado.phet.movingman.ladybug.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Ellipse2D, GeneralPath, Point2D}
import model.{DataPoint, ObservableS, LadybugModel}

import umd.cs.piccolo.nodes.PPath
import umd.cs.piccolo.PNode
import java.awt.{BasicStroke, Color}

class LadybugDotTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: ObservableS) extends LadybugTraceNode(model, transform, shouldBeVisible, observable) {
  val node = new PNode()
  addChild(node)

  update()

  class DotNode(point: Point2D) extends PNode {
    val path = new PhetPPath(new Ellipse2D.Double(point.getX - 5, point.getY - 5, 10, 10), LadybugColorSet.position)
    addChild(path)
  }

  def update() = {
    node.removeAllChildren
    val p = new GeneralPath
    implicit def historyToPoint(dataPoint: DataPoint) = new Point2D.Float(dataPoint.state.position.x.toFloat, dataPoint.state.position.y.toFloat)

    if (model.getHistory.length > 0) {
      for (h <- getHistoryToShow()) {
        val pt: Point2D.Float = h
        val tx = transform.modelToView(pt)
        node.addChild(new DotNode(tx))
      }
    }
  }
}