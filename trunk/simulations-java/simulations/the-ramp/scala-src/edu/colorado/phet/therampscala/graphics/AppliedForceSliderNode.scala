package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import model.Bead
import java.awt.geom.Point2D
import umd.cs.piccolo.PNode
import swing.ScalaValueControl
import umd.cs.piccolox.pswing.PSwing
import scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.Predef._

class AppliedForceSliderNode(bead: Bead, transform: ModelViewTransform2D) extends PNode {
  val control = new ScalaValueControl(-50, 50, "Applied Force X", "0.0", "N",
    bead.appliedForce.x, value => bead.appliedForce = new Vector2D(value, 0), bead.addListener)

  val pswing = new PSwing(control)
  addChild(pswing)
  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    val scale = 1.2f
    pswing.setOffset(viewLoc - new Vector2D(pswing.getFullBounds.getWidth * scale, 0))
    pswing.setScale(scale)
  }
  updatePosition()
}