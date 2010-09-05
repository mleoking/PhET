package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import java.awt._
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.umd.cs.piccolo.nodes.PImage
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.Predef._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.model.{MotionSeriesObject, RampSegment}

/**
 * The RampSegmentNode is the graphical depiction (with Piccolo) of the traversible parts of the ramp, both the ground segment
 * and the angled segment.
 * @author Sam Reid
 */
class RampSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D, rampSurfaceModel: RampSurfaceModel, motionSeriesObject: MotionSeriesObject) extends PNode with HasPaint {
  private val woodColor = new Color(184, 131, 24)
  private val iceColor = new Color(186, 228, 255)
  protected val pathNode = new PhetPPath(woodColor)
  addChild(pathNode)
  paintColor = woodColor

  rampSurfaceModel.addListener(updateAll)
  val icicleImageNode = new PImage(BufferedImageUtils.multiScaleToHeight(MotionSeriesResources.getImage("icicles.gif".literal), 80))

  def updateAll() = {
    //Update base color
    paintColor = if (frictionless) iceColor else woodColor

    //Update images
    if (frictionless && !getChildrenReference.contains(icicleImageNode))
      addChild(icicleImageNode)
    else if (!frictionless && getChildrenReference.contains(icicleImageNode))
      removeChild(icicleImageNode)
    if (getChildrenReference.contains(icicleImageNode)) {
      val delta = (rampSegment.endPoint - rampSegment.startPoint).normalize
      val iceX = 0.4
      val alpha = iceX * rampSegment.length
      val pt = rampSegment.startPoint + delta * alpha
      icicleImageNode.setOffset(mytransform.modelToView(pt))
      icicleImageNode.setRotation(-rampSegment.angle)
    }
  }

  motionSeriesObject.addListener(updateAll) //TODO: only update when friction coefficient changes

  def frictionless = rampSurfaceModel.frictionless || motionSeriesObject.frictionless

  defineInvokeAndPass(rampSegment.addListenerByName) {
    pathNode.setPathTo(mytransform.createTransformedShape(new BasicStroke(0.4f).createStrokedShape(rampSegment.toLine2D)))
  }
  rampSegment addListener updateAll
  updateAll()

  def paintColor_=(p: Paint) = pathNode.setPaint(p)

  def paintColor = pathNode.getPaint
}

class RotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D, rampSurfaceModel: RampSurfaceModel, motionSeriesObject: MotionSeriesObject)
        extends RampSegmentNode(rampSegment, mytransform, rampSurfaceModel, motionSeriesObject) {
  pathNode.addInputEventListener(new CursorHandler)
  pathNode.addInputEventListener(new RotationHandler(mytransform, pathNode, rampSegment, 0, MotionSeriesDefaults.MAX_ANGLE))
}

trait HasPaint extends PNode {
  def paintColor_=(p: Paint): Unit

  def paintColor: Paint //couldn't name this method paint because it collides with PNode.paint
}

trait RampSurfaceModel extends Observable {
  def frictionless: Boolean
}