package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import java.awt.geom.Point2D
import java.awt.{BasicStroke, Color}
import umd.cs.piccolo.PNode
import LadybugUtil._

class ArrowSetNode(ladybug: Ladybug, transform: ModelViewTransform2D) extends PNode {
  def createNode(color: Color) = {
    val velocityNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(200, 200), 90, 90, 60, 2, true)
    velocityNode setPaint color
    velocityNode setStroke new BasicStroke(0.5f)
    velocityNode setStrokePaint Color.gray
    velocityNode
  }

  val velocityNode = createNode(LadybugColorSet.velocity)
  val accelNode = createNode(LadybugColorSet.acceleration)
  addChild(velocityNode)
  addChild(accelNode)

  ladybug addListener update
  update(ladybug)

  def update(a: Ladybug) {
    val viewPosition = transform modelToView a.getPosition
    val viewVelocity = transform modelToViewDifferentialDouble a.getVelocity
    velocityNode.setTipAndTailLocations(viewPosition + viewVelocity * 10, viewPosition)

    val viewAccel = transform modelToViewDifferentialDouble a.getAcceleration
    accelNode.setTipAndTailLocations(viewPosition + viewAccel * 50, viewPosition)
  }
}