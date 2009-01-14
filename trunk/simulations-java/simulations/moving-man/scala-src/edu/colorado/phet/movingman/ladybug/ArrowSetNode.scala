package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import java.awt.geom.Point2D
import umd.cs.piccolo.PNode
import LadybugUtil._

class ArrowSetNode(ladybug: Ladybug, transform: ModelViewTransform2D) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(200, 200), 20, 20, 10, 2, true)
  addChild(arrowNode)

  ladybug.addListener(update)
  update(ladybug)

  def update(a: Ladybug) {
    val viewPosition: Vector2D = transform.modelToView(a.getPosition)
    val viewVelocity: Vector2D = transform.modelToViewDifferentialDouble(a.getVelocity)
    arrowNode.setTipAndTailLocations(viewPosition + viewVelocity * 10, viewPosition)
  }
}