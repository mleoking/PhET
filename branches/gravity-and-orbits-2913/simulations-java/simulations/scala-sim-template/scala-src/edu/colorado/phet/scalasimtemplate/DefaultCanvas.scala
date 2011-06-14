package edu.colorado.phet.scalasimtemplate

import java.awt.{Rectangle, Dimension, Color}
import java.awt.geom.Rectangle2D
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D

class DefaultCanvas(modelWidth: Double, modelHeight: Double) extends PhetPCanvas(new Dimension(1024, 768)) {
//  setWorldTransformStrategy(new CenteredBoxStrategy(768, 768, this))
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight), new Rectangle(0, 0, 768, 768), true)

  val worldNode = new PNode
  addWorldChild(worldNode)
  def addNode(node: PNode) = worldNode.addChild(node)

  def removeNode(node:PNode) = worldNode.removeChild(node)

  def addNode(index: Int, node: PNode) = worldNode.addChild(index, node)
}