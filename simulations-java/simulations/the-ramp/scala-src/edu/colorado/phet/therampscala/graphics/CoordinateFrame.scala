package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.PhetPPath
import java.awt.geom.Line2D
import java.awt.{BasicStroke, Color}
import model.RampModel
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import java.lang.Math._

class CoordinateFrame(val model: RampModel, val transform: ModelViewTransform2D) extends PNode {
  addChild(new PText("Coordinate Frame"))
  def update() = {
  }
  update()

  val yAxisModel = new AxisModel(PI/2, 7)
  val yAxis = new AxisNodeWithModel(transform,  "y", yAxisModel)
  addChild(yAxis)

  val xAxis = new AxisNode(transform, 0, 0, 7, 0, "x")
  addChild(xAxis)
}