package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import umd.cs.piccolo.PNode
import common.piccolophet.nodes.PhetPPath
import java.awt.{BasicStroke, Color}

import umd.cs.piccolo.nodes.PText
import java.awt.geom.Arc2D
import java.text.DecimalFormat
import edu.colorado.phet.scalacommon.Predef._
import RampResources._

//todo: consider coalescing with RampHeightIndicator
class RampAngleIndicator(rampSegment: Rotatable, transform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new BasicStroke(2f), Color.black)
  val readout = new PText
  readout.setFont(new PhetFont(24))
  addChild(line)
  addChild(readout)
  def getDegrees = rampSegment.getUnitVector.getAngle.toDegrees

  def getPath = new Arc2D.Double(rampSegment.startPoint.x - 3, rampSegment.startPoint.y - 3, 6, 6, 0, -getDegrees, Arc2D.OPEN)
  defineInvokeAndPass(rampSegment.addListenerByName) {
    line.setPathTo(transform.createTransformedShape(getPath))
    readout.setOffset(transform.modelToView(0.5, -0.08))
    val degrees = new DecimalFormat("0.0".literal).format(getDegrees)
    readout.setText("ramp.angle-readout".translate.messageformat(degrees))
  }
}