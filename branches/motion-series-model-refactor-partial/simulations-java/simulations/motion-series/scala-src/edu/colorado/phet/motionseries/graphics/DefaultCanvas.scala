package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.{Rectangle, Dimension}
import java.awt.geom.Rectangle2D

import edu.umd.cs.piccolo.PNode
import java.lang.Math._
import edu.colorado.phet.scalacommon.CenteredBoxStrategy

class DefaultCanvas(modelWidth: Double, modelHeight: Double, canvasWidth: Int, canvasHeight: Int, modelOffsetY: Double)
        extends PhetPCanvas(new Dimension(canvasWidth, canvasHeight)) {
  def this(modelWidth: Double, modelHeight: Double) = this (modelWidth, modelHeight, 1024, 768, 0)

  def canonicalBounds = new Rectangle(0, 0, min(canvasWidth, canvasHeight), min(canvasWidth, canvasHeight))

  val centeredBoxStrategy = new CenteredBoxStrategy(canonicalBounds.width, canonicalBounds.height, this, modelOffsetY)
  setWorldTransformStrategy(centeredBoxStrategy)
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2,
    modelWidth, modelHeight), canonicalBounds, true)
  val stageNode = new PNode //call it the stage in analogy with flash
  addWorldChild(stageNode)

  def getScale = centeredBoxStrategy.getScale

  def addStageNode(node: PNode) = stageNode.addChild(node)

  def addStageNode(index: Int, node: PNode) = stageNode.addChild(index, node)

  def indexOfStageNode(node: PNode) = stageNode.indexOfChild(node)

  def addStageNodeAfter(preNode: PNode, newNode: PNode) = addStageNode(indexOfStageNode(preNode) + 1, newNode)

  def removeStageNode(node: PNode) = stageNode.removeChild(node)

  def getVisibleModelBounds = centeredBoxStrategy.getVisibleModelBounds
}