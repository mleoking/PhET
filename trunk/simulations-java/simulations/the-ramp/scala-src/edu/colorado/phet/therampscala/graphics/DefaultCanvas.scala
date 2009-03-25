package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.PhetPCanvas
import java.awt.{Rectangle, Dimension, Color}
import java.awt.geom.Rectangle2D

import scalacommon.CenteredBoxStrategy
import umd.cs.piccolo.PNode

class DefaultCanvas(modelWidth: Double, modelHeight: Double) extends PhetPCanvas(new Dimension(1024, 768)) {
  setWorldTransformStrategy(new CenteredBoxStrategy(768, 768, this))
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight), new Rectangle(0, 0, 768, 768), true)

  val worldNode = new PNode
  addWorldChild(worldNode)
  def addNode(node: PNode) = worldNode.addChild(node)

  def addNode(index: Int, node: PNode) = worldNode.addChild(index, node)
}