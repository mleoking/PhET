package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import java.awt.geom.Point2D
import java.awt.{BasicStroke, Color}
import umd.cs.piccolo.PNode
import LadybugUtil._

class ArrowSetNode(ladybug: Ladybug, transform: ModelViewTransform2D) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(200, 200), 20, 20, 10, 2, true)
  arrowNode.setPaint(Color.blue)
  arrowNode.setStroke(new BasicStroke(2))
  addChild(arrowNode)

  ladybug.addListener(update)
  update(ladybug)

  def update(a: Ladybug) {
    val viewPosition = transform.modelToView(a.getPosition)
    val viewVelocity = transform.modelToViewDifferentialDouble(a.getVelocity)
    arrowNode.setTipAndTailLocations(viewPosition + viewVelocity * 10, viewPosition)
  }
}