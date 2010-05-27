package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import java.awt._
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.umd.cs.piccolo.nodes.PImage
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.colorado.phet.motionseries.model.RampSegment
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._
import edu.colorado.phet.motionseries.Predef._
import edu.colorado.phet.motionseries.MotionSeriesDefaults

/**
 * The RampSegmentNode is the graphical depiction (with Piccolo) of the traversible parts of the ramp, both the ground segment
 * and the angled segment.
 * @author Sam Reid
 */
class RampSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D, rampSurfaceModel: RampSurfaceModel) extends PNode with HasPaint {
  val woodColor = new Color(184, 131, 24)
  val woodStrokeColor = new Color(91, 78, 49)

  val iceColor = new Color(186, 228, 255)
  val iceStrokeColor = new Color(223, 236, 244)

  //todo: user should set a base color and an interpolation strategy, final paint should be interpolate(base)
  private var baseColor = woodColor
  val wetColor = new Color(150, 211, 238)
  val hotColor = new Color(255, 0, 0)
  val line = new PhetPPath(baseColor, new BasicStroke(2f), woodStrokeColor)
  addChild(line)
  def updateAll() = {
    updateBaseColor()
    updateColor()
    updateDecorations()
  }
  rampSurfaceModel.addListener(() => updateAll())
  val icicleImageNode = new PImage(BufferedImageUtils.multiScaleToHeight(MotionSeriesResources.getImage("icicles.gif".literal), 80))

  def updateBaseColor() = {
    baseColor = if (rampSurfaceModel.frictionless) iceColor else woodColor
    line.setStrokePaint(if (rampSurfaceModel.frictionless) iceStrokeColor else woodStrokeColor)

    if (rampSurfaceModel.frictionless && !getChildrenReference.contains(icicleImageNode))
      addChild(icicleImageNode)
    else if (!rampSurfaceModel.frictionless && getChildrenReference.contains(icicleImageNode))
      removeChild(icicleImageNode)
  }
  defineInvokeAndPass(rampSegment.addListenerByName) {
    line.setPathTo(mytransform.createTransformedShape(new BasicStroke(0.4f).createStrokedShape(rampSegment.toLine2D)))
  }
  rampSegment.wetnessListeners += (() => updateColor())
  rampSegment.addListener(() => updateDecorations())
  updateAll()
  def updateColor() = {
    val r = new LinearFunction(0, 1, baseColor.getRed, wetColor.getRed).evaluate(rampSegment.wetness).toInt
    val g = new LinearFunction(0, 1, baseColor.getGreen, wetColor.getGreen).evaluate(rampSegment.wetness).toInt
    val b = new LinearFunction(0, 1, baseColor.getBlue, wetColor.getBlue).evaluate(rampSegment.wetness).toInt
    val wetnessColor = new Color(r, g, b)

    val scaleFactor = 10000.0 * 2
    val heatBetweenZeroAndOne = max(min(rampSegment.heat / scaleFactor, 1), 0)
    val r2 = new LinearFunction(0, 1, wetnessColor.getRed, hotColor.getRed).evaluate(heatBetweenZeroAndOne).toInt
    val g2 = new LinearFunction(0, 1, wetnessColor.getGreen, hotColor.getGreen).evaluate(heatBetweenZeroAndOne).toInt
    val b2 = new LinearFunction(0, 1, wetnessColor.getBlue, hotColor.getBlue).evaluate(heatBetweenZeroAndOne).toInt
    paintColor = new Color(r2, g2, b2)
  }

  def updateDecorations() = {
    if (getChildrenReference.contains(icicleImageNode)) {
      val delta = (rampSegment.endPoint - rampSegment.startPoint).normalize
      val iceX = 0.4
      val alpha = iceX * rampSegment.length
      val pt = rampSegment.startPoint + delta * alpha
      icicleImageNode.setOffset(mytransform.modelToView(pt))
      icicleImageNode.setRotation(-rampSegment.angle)
    }
  }

  rampSegment.heatListeners += (() => updateColor())

  def paintColor_=(p: Paint) = line.setPaint(p)

  def paintColor = line.getPaint
}

class RotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D, rampSurfaceModel: RampSurfaceModel) extends RampSegmentNode(rampSegment, mytransform, rampSurfaceModel) {
  line.addInputEventListener(new CursorHandler)
  line.addInputEventListener(new RotationHandler(mytransform, line, rampSegment, 0, MotionSeriesDefaults.MAX_ANGLE))
}

trait HasPaint extends PNode {
  def paintColor_=(p: Paint): Unit

  def paintColor: Paint //couldn't name this method paint because it collides with PNode.paint
}

trait RampSurfaceModel extends Observable {
  def frictionless: Boolean
}