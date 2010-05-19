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

trait HasPaint extends PNode {
  def paintColor_=(p: Paint): Unit

  def paintColor: Paint
}

trait RampSurfaceModel extends Observable {
  def frictionless: Boolean
}

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
  rampSurfaceModel.addListener(() => {
    updateBaseColor()
    updateColor()
    updateDecorations()
  })
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
  updateBaseColor()
  updateColor()
  updateDecorations()
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

  val iceX = java.lang.Math.random * 0.8

  def updateDecorations() = {
    if (getChildrenReference.contains(icicleImageNode)) {
      val delta = (rampSegment.endPoint - rampSegment.startPoint).normalize
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

trait Rotatable extends Observable with RotationModel {
  def startPoint: Vector2D

  def endPoint_=(newPt: Vector2D)

  def length: Double

  def getUnitVector: Vector2D

  def endPoint: Vector2D

  def startPoint_=(newPt: Vector2D)

  def angle_=(a: Double) = {
	  endPoint = new Vector2D(a) * length
  }
}

trait RotationModel {
  def getPivot: Vector2D

  def angle: Double = 0.0

  def angle_=(a: Double)
}
class RotationHandler(val transform: ModelViewTransform2D,
                      val node: PNode,
                      val rotatable: RotationModel,
                      min: Double,
                      max: Double)
        extends PBasicInputEventHandler {
  //TODO: it seems like these fields and computations should be moved to model objects
  private var totalDelta = 0.0
  private var origAngle = 0.0

  override def mouseDragged(event: PInputEvent) = {
    val modelPt = transform.viewToModel(event.getPositionRelativeTo(node.getParent))

    val deltaView = event.getDeltaRelativeTo(node.getParent)
    val deltaModel = transform.viewToModelDifferential(deltaView.width, deltaView.height)

    val oldPtModel = modelPt - deltaModel

    val oldAngle = (rotatable.getPivot - oldPtModel).getAngle
    val newAngle = (rotatable.getPivot - modelPt).getAngle

    //should be a small delta
    var deltaAngle = newAngle - oldAngle
    while (deltaAngle > PI) deltaAngle = deltaAngle - PI * 2
    while (deltaAngle < -PI) deltaAngle = deltaAngle + PI * 2

    totalDelta += deltaAngle
    val proposedAngle = origAngle + totalDelta

    val angle = getSnapAngle(if (proposedAngle > max) max else if (proposedAngle < min) min else proposedAngle)
    rotatable.angle = angle
  }

  def getSnapAngle(proposedAngle: Double) = proposedAngle

  override def mousePressed(event: PInputEvent) = {
    totalDelta = 0

    val modelPt = transform.viewToModel(event.getPositionRelativeTo(node.getParent))
    val oldAngle = (modelPt - rotatable.getPivot).getAngle
    origAngle = oldAngle
  }
}

class RotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D, rampSurfaceModel: RampSurfaceModel) extends RampSegmentNode(rampSegment, mytransform, rampSurfaceModel) {
  line.addInputEventListener(new CursorHandler)
  line.addInputEventListener(new RotationHandler(mytransform, line, rampSegment, 0, MotionSeriesDefaults.MAX_ANGLE))
}

//class ReverseRotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D, rampSurfaceModel: RampSurfaceModel) extends RampSegmentNode(rampSegment, mytransform, rampSurfaceModel) {
//  line.addInputEventListener(new CursorHandler)
//  line.addInputEventListener(new RotationHandler(mytransform, line, new Reverse(rampSegment).reverse, PI / 2 + 1E-6, PI - (1E-6))) //todo: atan2 returns angle between -pi and +pi, so end behavior is incorrect
//}

//class Reverse(target: Rotatable) {
//  //this one rotates about the end point, facilitates reuse of some view classes while still allowing generalized model objects
//  object reverse extends Rotatable {
//    def length = target.length
//
//    def startPoint = target.endPoint
//
//    def endPoint = target.startPoint
//
//    def getUnitVector = target.getUnitVector * -1
//
//    def endPoint_=(newPt: Vector2D) = target.startPoint = newPt
//
//    def startPoint_=(newPt: Vector2D) = target.endPoint = newPt
//
//    override def addListenerByName(listener: => Unit) = target.addListenerByName(listener)
//
//    override def notifyListeners() = target.notifyListeners()
//
//    override def removeListener(listener: () => Unit) = target.removeListener(listener)
//
//    override def addListener(listener: () => Unit) = target.addListener(listener)
//  }
//}



