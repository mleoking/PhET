package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.{BasicStroke, Color}

import edu.umd.cs.piccolo.nodes.PText
import java.awt.geom.Arc2D
import java.text.DecimalFormat
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.MotionSeriesResources._

/**
 * The RampAngleIndicator shows an arc and numerical readout for the ramp rotation in degrees.
 * @author Sam Reid
 */
class RampAngleIndicator(rampSegment: Rotatable, transform: ModelViewTransform2D) extends PNode {
  val pathNode = new PhetPPath(new BasicStroke(2f), Color.black)
  val readout = new PText {
    setFont(MotionSeriesDefaults.rampIndicatorFont)
  }
  val decimalFormat = new DecimalFormat("0.0".literal)
  addChild(pathNode)
  addChild(readout)
  def degrees = rampSegment.unitVector.angle.toDegrees

  def path = new Arc2D.Double(rampSegment.startPoint.x - 3, rampSegment.startPoint.y - 3, 6, 6, 0, -degrees, Arc2D.OPEN)
  defineInvokeAndPass(rampSegment.addListenerByName) {
    pathNode.setPathTo(transform.createTransformedShape(path))
    readout.setOffset(transform.modelToView(5, -0.7)) //tuned to ensure it's far enough away from the "meters" readout on the ramp segment node
    readout.setText("ramp.angle-readout".messageformat(decimalFormat.format(degrees)))
  }
  setPickable(false)
  setChildrenPickable(false)
}