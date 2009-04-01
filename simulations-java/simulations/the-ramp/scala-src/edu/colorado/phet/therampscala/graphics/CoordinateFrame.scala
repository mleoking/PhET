package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import model.RampModel
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode

class CoordinateFrame(val model: RampModel, val transform: ModelViewTransform2D) extends PNode {
  addChild(new PText("Coordinate Frame"))
}