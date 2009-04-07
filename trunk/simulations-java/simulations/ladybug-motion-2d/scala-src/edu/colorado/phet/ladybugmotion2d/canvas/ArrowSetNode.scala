package edu.colorado.phet.ladybugmotion2d.canvas

import controlpanel.VectorVisibilityModel
import model.Ladybug
import scalacommon.math.Vector2D
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import java.awt.geom.Point2D
import java.awt.{BasicStroke, Color}

import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.Predef._
import LadybugMotion2DResources._

class ArrowSetNode(ladybug: Ladybug, transform: ModelViewTransform2D, vectorVisibilityModel: VectorVisibilityModel) extends PNode {
  class LabeledArrowNode(color: Color, name: String) extends PNode {
    val arrowWidth = 90.0 * 0.25;
    val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(200, 200), arrowWidth, arrowWidth, arrowWidth * 2.0 / 3.0, 2, true)
    arrowNode setPaint color
    arrowNode setStroke new BasicStroke(1)
    arrowNode setStrokePaint Color.black

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

  val velocityNode = new LabeledArrowNode(LadybugColorSet.velocity, getLocalizedString("model.velocity"))
  val accelNode = new LabeledArrowNode(LadybugColorSet.acceleration, getLocalizedString("model.acceleration"))
  addChild(velocityNode)
  addChild(accelNode)

  ladybug addListener update
  update()
  vectorVisibilityModel.addListener(() => update())
  setPickable(false)
  setChildrenPickable(false)

  def update() {
    val viewPosition = transform modelToView ladybug.getPosition
    val viewVelocity = transform modelToViewDifferentialDouble ladybug.getVelocity
    val vectorScale = 0.33
    val velTip = viewPosition + viewVelocity * vectorScale
    velocityNode.setTipAndTailLocations(velTip, viewPosition)

    val viewAccel = transform modelToViewDifferentialDouble ladybug.getAcceleration
    accelNode.setTipAndTailLocations(viewPosition + viewAccel * vectorScale * LadybugDefaults.ACCEL_VECTOR_SCALE, viewPosition)

    accelNode.setVisible(vectorVisibilityModel.accelerationVectorVisible)
    velocityNode.setVisible(vectorVisibilityModel.velocityVectorVisible)
  }
}