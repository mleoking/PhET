package edu.colorado.phet.movingman.ladybug.canvas

import controlpanel.{PathVisibilityModel, VectorVisibilityModel}
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.event.{ComponentAdapter, ComponentEvent}
import java.awt.geom.{AffineTransform, Rectangle2D, Point2D}
import java.awt.{Rectangle, Dimension, Color}
import javax.swing.JComponent
import model.LadybugModel
import umd.cs.piccolo.PNode
import edu.colorado.phet.common.piccolophet.PhetPCanvas.RenderingSizeStrategy
import edu.colorado.phet.common.piccolophet.PhetPCanvas.TransformStrategy
import edu.colorado.phet.common.piccolophet.PhetPCanvas.ViewportStrategy
import umd.cs.piccolo.util.PDimension

class CenteredBoxStrategy(modelWidth: Double, modelHeight: Double, canvas: JComponent) extends TransformStrategy {
  def getTransform(): AffineTransform = {
    if (canvas.getWidth > 0 && canvas.getHeight > 0) {
      val sx = canvas.getWidth / modelWidth
      val sy = canvas.getHeight / modelHeight

      //use the smaller
      var scale = if (sx < sy) sx else sy
      scale = if (scale <= 0) sy else scale //if scale is negative or zero, just use scale=sy as a default
      val outputBox =
      if (scale == sx)
        new Rectangle2D.Double(0, (canvas.getHeight - canvas.getWidth) / 2.0, canvas.getWidth, canvas.getWidth)
      else
        new Rectangle2D.Double((canvas.getWidth - canvas.getHeight) / 2.0, 0, canvas.getHeight, canvas.getHeight)
      new ModelViewTransform2D(new Rectangle2D.Double(0, 0, modelWidth, modelHeight), outputBox, false).getAffineTransform
    } else {
      new AffineTransform
    }
  }
}

class LadybugCanvas(model: LadybugModel, vectorVisibilityModel: VectorVisibilityModel, pathVisibilityModel: PathVisibilityModel)
        extends PhetPCanvas(new Dimension(1024, 768)) {
  setWorldTransformStrategy(new CenteredBoxStrategy(768, 768, this));
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-10, -10, 20, 20), new Rectangle(0, 0, 768, 768), LadybugDefaults.POSITIVE_Y_IS_UP)
  val constructed = true
  updateWorldScale

  protected override def updateWorldScale = {
    super.updateWorldScale
    if (constructed) {
      //to go from pixels to model, must go backwards through canvas transform and modelviewtransform
      val topLeft = new Point2D.Double(0, 0)
      val bottomRight = new Point2D.Double(getWidth, getHeight)

      def tx(pt: Point2D) = {
        val intermediate = getWorldTransformStrategy.getTransform.inverseTransform(pt, null)
        val model = transform.viewToModel(intermediate.getX, intermediate.getY)
        model
      }
      val out = new Rectangle2D.Double()
      out.setFrameFromDiagonal(tx(topLeft).getX, tx(topLeft).getY, tx(bottomRight).getX, tx(bottomRight).getY)
      model.setBounds(out)
    }
  }


  val worldNode = new PNode
  addWorldChild(worldNode)
  setBackground(new Color(200, 255, 240))

  val ladybugNode = new LadybugNode(model, model.ladybug, transform, vectorVisibilityModel)
  addNode(ladybugNode)
  val dotTrace = new LadybugDotTraceNode(model, transform, () => pathVisibilityModel.dotsVisible, pathVisibilityModel, 0.7)
  addNode(dotTrace)
  val fadeTrace = new LadybugFadeTraceNode(model, transform, () => pathVisibilityModel.fadeVisible, pathVisibilityModel, 0.7)
  addNode(fadeTrace)
  addNode(new ReturnLadybugButton(model, this))

  def addNode(node: PNode) = worldNode.addChild(node)

  def clearTrace() = {
    dotTrace.clearTrace
    fadeTrace.clearTrace
  }

  def setLadybugDraggable(draggable: Boolean) = ladybugNode.setDraggable(draggable)
}