package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import java.awt.geom.Point2D
import java.awt.{BasicStroke, Color}
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import LadybugUtil._

class ArrowSetNode(ladybug: Ladybug, transform: ModelViewTransform2D, vectorVisibilityModel: VectorVisibilityModel) extends PNode {
  class LabeledArrowNode(color: Color, name: String) extends PNode {
    val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(200, 200), 90, 90, 60, 2, true)
    arrowNode setPaint color
    arrowNode setStroke new BasicStroke(0.5f)
    arrowNode setStrokePaint Color.gray

    val labelNode = new PText(name)
    labelNode.setFont(new PhetFont(24))

    addChild(arrowNode)
    addChild(labelNode)

    def setTipAndTailLocations(a: Vector2D, b: Vector2D) = {
      arrowNode.setTipAndTailLocations(a, b)
      labelNode.setOffset(a)
      labelNode.setVisible(labelNode.getFullBounds.width < (a - b).magnitude)
    }
  }

  val velocityNode = new LabeledArrowNode(LadybugColorSet.velocity, "Velocity")
  val accelNode = new LabeledArrowNode(LadybugColorSet.acceleration, "Acceleration")
  addChild(velocityNode)
  addChild(accelNode)

  ladybug addListener update
  update(ladybug)
  vectorVisibilityModel.addListener(() => update(ladybug))
  setPickable(false)
  setChildrenPickable(false)

  def update(a: Ladybug) {
    val viewPosition = transform modelToView a.getPosition
    val viewVelocity = transform modelToViewDifferentialDouble a.getVelocity
    velocityNode.setTipAndTailLocations(viewPosition + viewVelocity * 20, viewPosition)

    val viewAccel = transform modelToViewDifferentialDouble a.getAcceleration
    accelNode.setTipAndTailLocations(viewPosition + viewAccel * 75, viewPosition)

    accelNode.setVisible(vectorVisibilityModel.accelerationVectorVisible)
    velocityNode.setVisible(vectorVisibilityModel.velocityVectorVisible)
  }
}