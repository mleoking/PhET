package edu.colorado.phet.movingman.ladybug.canvas

import controlpanel.{PathVisibilityModel, VectorVisibilityModel}
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.event.{ComponentAdapter, ComponentEvent}
import java.awt.geom.Rectangle2D
import java.awt.{Rectangle, Dimension, Color}
import model.LadybugModel
import umd.cs.piccolo.PNode

class LadybugCanvas(model: LadybugModel, vectorVisibilityModel: VectorVisibilityModel, pathVisibilityModel: PathVisibilityModel) extends PhetPCanvas(new Dimension(1024, 768)) {
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-10, -10, 20, 20), new Rectangle(0, 0, 768, 768), false)
  val worldNode = new PNode
  addWorldChild(worldNode)
  setBackground(new Color(200, 255, 240))

  val ladybugNode=new LadybugNode(model, model.ladybug, transform, vectorVisibilityModel)
  addNode(ladybugNode)
  val solidTrace = new LadybugSolidTraceNode(model, transform, () => pathVisibilityModel.lineVisible, pathVisibilityModel)
  addNode(solidTrace)
  val dotTrace = new LadybugDotTraceNode(model, transform, () => pathVisibilityModel.dotsVisible, pathVisibilityModel)
  addNode(dotTrace)

  addNode(new ReturnLadybugButton(model, this))

  def addNode(node: PNode) = worldNode.addChild(node)

  def clearTrace() = {
    solidTrace.clearTrace
    dotTrace.clearTrace
  }
}