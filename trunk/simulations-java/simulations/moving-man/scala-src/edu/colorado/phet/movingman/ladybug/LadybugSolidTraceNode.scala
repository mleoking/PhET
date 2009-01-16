package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import _root_.edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{GeneralPath, Point2D}
import java.awt.{BasicStroke, Color}
import umd.cs.piccolo.PNode
import java.awt.geom.Path2D

class LadybugSolidTraceNode(model: LadybugModel, transform: ModelViewTransform2D) extends PNode {
  model.addListener(update)

  val path = new PhetPPath(new BasicStroke(4), Color.red)
  addChild(path)

  update(model)

  def update(model: LadybugModel) = {
    val p = new GeneralPath
    implicit def historyToPoint(dataPoint: DataPoint) = new Point2D.Float(dataPoint.state.position.x.toFloat, dataPoint.state.position.y.toFloat)
    if (model.history.length > 0) {
      val t = transform.modelToView(model.history(0))
      p.moveTo(t.x, t.y)
      for (h <- model.history) {
        val pt: Point2D.Float = h
        val tx = transform.modelToView(pt)
        p.lineTo(tx.getX, tx.getY)
      }
    }
    path.setPathTo(p)
  }
}