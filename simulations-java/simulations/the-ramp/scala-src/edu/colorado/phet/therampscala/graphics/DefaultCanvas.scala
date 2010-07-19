package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.PhetPCanvas
import java.awt.{Rectangle, Dimension}
import java.awt.geom.Rectangle2D

import scalacommon.CenteredBoxStrategy
import umd.cs.piccolo.PNode

class DefaultCanvas(modelWidth: Double, modelHeight: Double) extends PhetPCanvas(new Dimension(1024, 768)) {
  def canonicalBounds = new Rectangle(0, 0, 768, 768)
  setWorldTransformStrategy(new CenteredBoxStrategy(canonicalBounds.width, canonicalBounds.height, this))
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight), canonicalBounds, true)
  val worldNode = new PNode
  addWorldChild(worldNode)

  def addNode(node: PNode) = worldNode.addChild(node)

  def addNode(index: Int, node: PNode) = worldNode.addChild(index, node)

  def indexOfChild(node: PNode) = worldNode.indexOfChild(node)

  def addNodeAfter(preNode: PNode, newNode: PNode) = addNode(indexOfChild(preNode) + 1, newNode)

  def removeNode(node: PNode) = worldNode.removeChild(node)
}