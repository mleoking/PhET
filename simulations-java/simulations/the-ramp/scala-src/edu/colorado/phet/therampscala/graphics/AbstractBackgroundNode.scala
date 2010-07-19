package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}
import java.awt.geom.{Area, Rectangle2D}
import java.awt.{Paint, Shape, GradientPaint, Color}

import umd.cs.piccolo.PNode
import common.piccolophet.nodes.PhetPPath


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
class SkyNode(transform: ModelViewTransform2D) extends AbstractBackgroundNode(new GradientPaint(transform.modelToView(0, 0), RampDefaults.SKY_GRADIENT_BOTTOM, transform.modelToView(0, 10), new Color(202, 187, 255)), new Rectangle2D.Double(-100, 0, 200, 200), transform)
class EarthNode(transform: ModelViewTransform2D) extends AbstractBackgroundNode(RampDefaults.EARTH_COLOR, new Rectangle2D.Double(-100, -200, 200, 200), transform)

object EarthNodeWithCliff {
  def getArea(maxX: Double, lowerGroundY: Double) = {
    val area = new Area
    area.add(new Area(new Rectangle2D.Double(-100, -200, 100 + maxX, 200)))
    area.add(new Area(new Rectangle2D.Double(-100, -200, 200, 200 + lowerGroundY)))
    area
  }
}
class EarthNodeWithCliff(transform: ModelViewTransform2D, maxX: Double, lowerGroundY: Double)
        extends AbstractBackgroundNode(new Color(200, 240, 200), EarthNodeWithCliff.getArea(maxX, lowerGroundY), transform)