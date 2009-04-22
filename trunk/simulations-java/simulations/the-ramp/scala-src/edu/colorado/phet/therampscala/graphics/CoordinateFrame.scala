package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.PhetPPath
import java.awt.geom.Line2D
import java.awt.{BasicStroke, Color}
import model.RampModel
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode

class CoordinateFrame(val model: RampModel, val transform: ModelViewTransform2D) extends PNode {
  addChild(new PText("Coordinate Frame"))
  val path = new PhetPPath(new BasicStroke(2), Color.black)

  def update() = {
    path.setPathTo(new Line2D.Double(transform.modelToView(0, 0), transform.modelToView(0, 10)))
  }
  update()
}