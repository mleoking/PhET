package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import java.awt.geom.Point2D
import umd.cs.piccolo.PNode

class ArrowSetNode(ladybug: Ladybug) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(200, 200), 20, 20, 10, 2, true)
  addChild(arrowNode)

  ladybug.addListener(update)
  update(ladybug)

  implicit def vector2DToPoint(vector: Vector2D) = new Point2D.Double(vector.x, vector.y)

  def update(a: Ladybug) {
    arrowNode.setTipAndTailLocations(a.getPosition + a.getVelocity * 10, a.getPosition)
  }
}