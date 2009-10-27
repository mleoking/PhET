package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.geom.Point2D
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.umd.cs.piccolo.nodes.PText
import java.awt.Color
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.scalacommon.view.ToggleListener
import edu.colorado.phet.motionseries.model.AdjustableCoordinateModel
import edu.colorado.phet.scalacommon.Predef._

class AxisNode(val transform: ModelViewTransform2D,
               x0: Double,
               y0: Double,
               x1: Double,
               y1: Double,
               label: String)
        extends PNode {
  private val axisNode = new ArrowNode(transform.modelToViewDouble(x0, y0), transform.modelToViewDouble(x1, y1), 5, 5, 2)
  axisNode.setStroke(null)
  axisNode.setPaint(Color.black)

  //use an invisible wider hit region for mouse events
  protected val hitNode = new ArrowNode(transform.modelToViewDouble(x0, y0), transform.modelToViewDouble(x1, y1), 20, 20, 20, 1.0, true)
  hitNode.setStroke(null)
  hitNode.setPaint(new Color(0, 0, 0, 0))
  axisNode.setPickable(false)
  axisNode.setChildrenPickable(false)

  addChild(hitNode)
  addChild(axisNode)

  val text = new PText(label)
  text.setFont(new PhetFont(16, true))
  addChild(text)

  updateTextNodeLocation()
  def updateTextNodeLocation() = {
    val viewDst = axisNode.getTipLocation
    text.setOffset(viewDst.x - text.getFullBounds.getWidth * 1.5, viewDst.y)
  }

  def setTipAndTailLocations(tip: Point2D, tail: Point2D) = {
    axisNode.setTipAndTailLocations(tip, tail)
    hitNode.setTipAndTailLocations(tip, tail)
  }
}

class AxisNodeWithModel(transform: ModelViewTransform2D,
                        label: String,
                        val axisModel: SynchronizedAxisModel,
                        adjustableCoordinateModel: AdjustableCoordinateModel)
        extends AxisNode(transform,
          transform.modelToViewDouble(axisModel.startPoint).x, transform.modelToViewDouble(axisModel.startPoint).y,
          transform.modelToViewDouble(axisModel.getEndPoint).x, transform.modelToViewDouble(axisModel.getEndPoint).y, label) {
  defineInvokeAndPass(axisModel.addListenerByName) {
    setTipAndTailLocations(transform.modelToViewDouble(axisModel.getEndPoint), transform.modelToViewDouble(axisModel.startPoint))
    updateTextNodeLocation()
  }
  defineInvokeAndPass(adjustableCoordinateModel.addListenerByName) {
    setPickable(adjustableCoordinateModel.adjustable)
    setChildrenPickable(adjustableCoordinateModel.adjustable)
  }
  hitNode.addInputEventListener(new ToggleListener(new CursorHandler, () => adjustableCoordinateModel.adjustable))
  hitNode.addInputEventListener(new ToggleListener(new RotationHandler(transform, hitNode, axisModel, axisModel.minAngle, axisModel.maxAngle) {
    override def getSnapAngle(proposedAngle: Double) = axisModel.getSnapAngle(proposedAngle)
  }, () => adjustableCoordinateModel.adjustable))
  hitNode.addInputEventListener(new ToggleListener(new PBasicInputEventHandler {
    override def mouseReleased(event: PInputEvent) = axisModel.dropped()
  }, () => adjustableCoordinateModel.adjustable))
}