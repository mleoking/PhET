package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.Dimension
import java.awt.geom.Point2D
import model.{Bead}
import umd.cs.piccolo.PNode
import swing.ScalaValueControl
import umd.cs.piccolox.pswing.PSwing
import scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.Predef._

class AppliedForceSliderNode(bead: Bead, transform: ModelViewTransform2D) extends PNode {
  val max = RampDefaults.MAX_APPLIED_FORCE
  val control = new ScalaValueControl(-max, max, "Applied Force X", "0.0", "N",
    () => bead.parallelAppliedForce, value => bead.parallelAppliedForce = value, bead.addListener)
  control.setSize(new Dimension(control.getPreferredSize.width, (control.getPreferredSize.height * 1.45).toInt))
  val pswing = new PSwing(control)
  addChild(pswing)
  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    val scale = 1.2f * 1.2f
    val x = transform.getViewBounds
    pswing.setScale(scale)
    pswing.setOffset(x.getCenterX - pswing.getFullBounds.getWidth / 2, viewLoc.y)
  }
  updatePosition()
}