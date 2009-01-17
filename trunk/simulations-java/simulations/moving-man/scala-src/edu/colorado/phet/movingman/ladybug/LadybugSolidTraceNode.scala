package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{GeneralPath, Point2D}
import java.awt.{BasicStroke, Color}
import umd.cs.piccolo.PNode
import java.awt.geom.Path2D

class LadybugSolidTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: ObservableS) extends PNode {
  setPickable(false)
  setChildrenPickable(false)
  observable.addListener(() => setVisible(shouldBeVisible()))
  setVisible(shouldBeVisible())
  model.addListener(update)

  val path = new PhetPPath(new BasicStroke(4), Color.red)
  addChild(path)

  update(model)

  def update(model: LadybugModel) = {
    val p = new GeneralPath
    implicit def historyToPoint(dataPoint: DataPoint) = new Point2D.Float(dataPoint.state.position.x.toFloat, dataPoint.state.position.y.toFloat)
    if (model.history.length > 0) {

      val start = (model.history.length - 100) max 0 max clearPt
      val t = transform.modelToView(model.history(start))
      p.moveTo(t.x, t.y)
      val historyToShow = model.history.slice(start, model.history.length)
      for (h <- historyToShow) {
        val pt: Point2D.Float = h
        val tx = transform.modelToView(pt)
        p.lineTo(tx.getX, tx.getY)
      }
    }
    path.setPathTo(p)
  }

  var clearPt = 0

  def clearTrace = {
    clearPt = model.history.length
  }
}