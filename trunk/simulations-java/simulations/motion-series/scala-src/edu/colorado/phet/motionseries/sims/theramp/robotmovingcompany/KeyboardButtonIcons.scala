package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import edu.umd.cs.piccolo.PNode
import java.awt.{BasicStroke, Color}
import edu.umd.cs.piccolo.nodes.PImage
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import java.awt.geom.Point2D
import edu.colorado.phet.motionseries.Predef._

class KeyboardButtonIcons extends PNode {
  def directionKeyNode(dx: Double, dy: Double) = {
    new PImage(MotionSeriesResources.getImage("robotmovingcompany/empty-key.png".literal)) {
      val b = getFullBounds
      val y1 = b.getCenterY + b.getHeight * dy
      val y2 = b.getCenterY - b.getHeight * dy
      val x1 = b.getCenterX + b.getWidth * dx
      val x2 = b.getCenterX - b.getWidth * dx
      val node = new ArrowNode(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), 20, 20, 10) {
        setPaint(Color.black)
        setStroke(new BasicStroke(2))
        setStrokePaint(Color.gray)
      }
      scale(0.8)
      addChild(node)
    }
  }

  val leftKey = directionKeyNode(1 / 5.0, 0)
  val rightKey = directionKeyNode(-1 / 5.0, 0)

  addChild(leftKey)
  addChild(rightKey)
  rightKey.setOffset(leftKey.getFullBounds.getMaxX + 10, leftKey.getFullBounds.getY)
}