package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import _root_.edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Ellipse2D, GeneralPath, Point2D}
import umd.cs.piccolo.nodes.PPath
import umd.cs.piccolo.PNode
import java.awt.{BasicStroke, Color}

class LadybugDotTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: ObservableS) extends PNode {
  observable.addListener(() => setVisible(shouldBeVisible()))
  setVisible(shouldBeVisible())
  model.addListener(update)

  val node = new PNode()
  addChild(node)

  update(model)

  class DotNode(point: Point2D) extends PNode {
    val path = new PhetPPath(new Ellipse2D.Double(point.getX - 5, point.getY - 5, 10, 10), Color.blue, new BasicStroke(0.5f), Color.darkGray)
    addChild(path)
  }

  def update(model: LadybugModel) = {
    node.removeAllChildren
    val p = new GeneralPath
    implicit def historyToPoint(dataPoint: DataPoint) = new Point2D.Float(dataPoint.state.position.x.toFloat, dataPoint.state.position.y.toFloat)
    if (model.history.length > 0) {
      for (h <- model.history) {
        val pt: Point2D.Float = h
        val tx = transform.modelToView(pt)
        node.addChild(new DotNode(tx))
      }
    }
  }
}