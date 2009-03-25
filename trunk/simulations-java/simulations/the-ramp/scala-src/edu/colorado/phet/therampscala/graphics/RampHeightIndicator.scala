package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import model.RampSegment
import umd.cs.piccolo.PNode
import common.piccolophet.nodes.PhetPPath
import java.awt.{BasicStroke, Color}

import umd.cs.piccolo.nodes.PText
import java.awt.geom.Line2D
import java.text.DecimalFormat
import scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.Predef._

class RampHeightIndicator(rampSegment: RampSegment, transform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new BasicStroke(2f), Color.black)
  addChild(line)

  val readout = new PText
  readout.setFont(new PhetFont(24))
  addChild(readout)
  def getLine = new Line2D.Double(new Vector2D(rampSegment.endPoint.x, 0), rampSegment.endPoint)
  defineInvokeAndPass(rampSegment.addListenerByName) {
    line.setPathTo(transform.createTransformedShape(getLine))
    readout.setOffset(line.getFullBounds.getMaxX + 10, line.getFullBounds.getCenterY)
    readout.setText("h = " + new DecimalFormat("0.0").format(rampSegment.endPoint.y) + " m")
  }
}