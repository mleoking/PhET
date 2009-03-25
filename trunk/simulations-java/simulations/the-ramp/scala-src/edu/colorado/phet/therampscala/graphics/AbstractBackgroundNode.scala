package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}
import java.awt.{Paint, Shape, GradientPaint, Color}

import umd.cs.piccolo.PNode
import common.piccolophet.nodes.PhetPPath


import java.awt.geom.Rectangle2D

class AbstractBackgroundNode(getPaint: => Paint, getModelShape: => Shape, transform: ModelViewTransform2D) extends PNode {
  val node = new PhetPPath(getPaint)
  addChild(node)

  def updatePath() = {
    val viewPath = transform.createTransformedShape(getModelShape)
    node.setPathTo(viewPath)
  }
  updatePath()

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = updatePath()
  })
}
class SkyNode(transform: ModelViewTransform2D) extends AbstractBackgroundNode(new GradientPaint(transform.modelToView(0, 0), new Color(250, 250, 255), transform.modelToView(0, 10), new Color(202, 187, 255)), new Rectangle2D.Double(-100, 0, 200, 200), transform)
class EarthNode(transform: ModelViewTransform2D) extends AbstractBackgroundNode(new Color(200, 240, 200), new Rectangle2D.Double(-100, -200, 200, 200), transform)