package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import java.awt.geom.Point2D
import umd.cs.piccolo.PNode

class ArrowSetNode(ladybug: Ladybug, transform: ModelViewTransform2D) extends PNode {
  implicit def vector2DToPoint(vector: Vector2D) = new Point2D.Double(vector.x, vector.y)

  implicit def pointToVector2D(point: Point2D) = new Vector2D(point.getX, point.getY)

  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(200, 200), 20, 20, 10, 2, true)
  addChild(arrowNode)

  ladybug.addListener(update)
  update(ladybug)

  def update(a: Ladybug) {
    val viewPosition:Vector2D=transform.modelToView(a.getPosition)
    val viewVelocity:Vector2D=transform.modelToViewDifferentialDouble(a.getVelocity)
    arrowNode.setTipAndTailLocations(viewPosition + viewVelocity* 10, viewPosition)
  }
}