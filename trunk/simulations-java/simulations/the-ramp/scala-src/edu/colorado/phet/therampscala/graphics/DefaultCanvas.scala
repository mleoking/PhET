package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.PhetPCanvas
import java.awt.{Rectangle, Dimension}
import java.awt.geom.Rectangle2D

import scalacommon.CBS
import umd.cs.piccolo.PNode
import java.lang.Math._

class DefaultCanvas(modelWidth: Double, modelHeight: Double, canvasWidth: Int, canvasHeight: Int, modelOffsetY: Double)
        extends PhetPCanvas(new Dimension(canvasWidth, canvasHeight)) {
  def this(modelWidth: Double, modelHeight: Double) = this (modelWidth, modelHeight, 1024, 768, 0)

  def canonicalBounds = new Rectangle(0, 0, min(canvasWidth, canvasHeight), min(canvasWidth, canvasHeight))

  val centeredBoxStrategy = new CBS(canonicalBounds.width, canonicalBounds.height, this, modelOffsetY)
  setWorldTransformStrategy(centeredBoxStrategy)
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2,
    modelWidth, modelHeight), canonicalBounds, true)
  val worldNode = new PNode
  addWorldChild(worldNode)

  def addNode(node: PNode) = worldNode.addChild(node)

  def addNode(index: Int, node: PNode) = worldNode.addChild(index, node)

  def indexOfChild(node: PNode) = worldNode.indexOfChild(node)

  def addNodeAfter(preNode: PNode, newNode: PNode) = addNode(indexOfChild(preNode) + 1, newNode)

  def removeNode(node: PNode) = worldNode.removeChild(node)

  def getVisibleModelBounds = centeredBoxStrategy.getVisibleModelBounds
}