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

//todo: consider coalescing with RampHeightIndicator
class RampAngleIndicator(rampSegment: Rotatable, transform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new BasicStroke(2f), Color.black)
  val readout = new PText
  readout.setFont(MotionSeriesDefaults.rampIndicatorFont)
  addChild(line)
  addChild(readout)
  def getDegrees = rampSegment.unitVector.angle.toDegrees

  def getPath = new Arc2D.Double(rampSegment.startPoint.x - 3, rampSegment.startPoint.y - 3, 6, 6, 0, -getDegrees, Arc2D.OPEN)
  defineInvokeAndPass(rampSegment.addListenerByName) {
    line.setPathTo(transform.createTransformedShape(getPath))
    readout.setOffset(transform.modelToView(0.7, -0.7)) //tuned to ensure it's far enough away from the "meters" readout on the ramp segment node
    val degrees = new DecimalFormat("0.0".literal).format(getDegrees)
    readout.setText("ramp.angle-readout".messageformat(degrees))
  }
  setPickable(false)
  setChildrenPickable(false)
}