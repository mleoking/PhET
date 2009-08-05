package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import common.piccolophet.nodes.PhetPPath
import java.awt.Color
import java.awt.geom.{Rectangle2D, Point2D}
import java.text.DecimalFormat
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import RampResources._

class TickMarkSet(transform: ModelViewTransform2D, positionMapper: Double => Point2D, addListener: (() => Unit) => Unit) extends PNode {
  for (x <- -10 to 10 by 2) {
    addTickLabel(x)
  }
  setPickable(false)
  setChildrenPickable(false)
  def addTickLabel(x: Double) = {
    val path = new PhetPPath(Color.black)
    val label = new PText(new DecimalFormat("0".literal).format(x))

    label.setFont(new PhetFont(18, true))
    addChild(path)
    addChild(label)
    addListener(update)
    def update() = {
      val d = transform.modelToView(positionMapper(x))
      path.setPathTo(new Rectangle2D.Double(d.x, d.y, 2, 2))
      label.setOffset(path.getFullBounds.getCenterX - label.getFullBounds.width / 2, path.getFullBounds.getMaxY)
    }
    update()
  }
}