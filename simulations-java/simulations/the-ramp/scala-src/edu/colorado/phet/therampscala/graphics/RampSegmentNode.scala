package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import umd.cs.piccolo.PNode
import common.piccolophet.nodes.PhetPPath
import java.awt.{Color, BasicStroke, Cursor}

import model.RampSegment
import common.piccolophet.event.CursorHandler

import scalacommon.math.Vector2D
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import scalacommon.Predef._
import java.lang.Math._

class RampSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new Color(184, 131, 24), new BasicStroke(2f), new Color(91, 78, 49))
  addChild(line)
  defineInvokeAndPass(rampSegment.addListenerByName) {
    line.setPathTo(mytransform.createTransformedShape(new BasicStroke(0.4f).createStrokedShape(rampSegment.toLine2D)))
  }
}

trait Rotatable {
  def startPoint: Vector2D

  def endPoint_=(newPt: Vector2D)

  def length: Double

  def getUnitVector: Vector2D

  def endPoint: Vector2D

}
class RotationHandler(val mytransform: ModelViewTransform2D, val node: PNode, val rampSegment: Rotatable, min: Double, max: Double) extends PBasicInputEventHandler {
  override def mouseDragged(event: PInputEvent) = {
    val modelPt = mytransform.viewToModel(event.getPositionRelativeTo(node.getParent))

    val deltaView = event.getDeltaRelativeTo(node.getParent)
    val deltaModel = mytransform.viewToModelDifferential(deltaView.width, deltaView.height)

    val oldPtModel = modelPt - deltaModel

    val oldAngle = (rampSegment.startPoint - oldPtModel).getAngle
    val newAngle = (rampSegment.startPoint - modelPt).getAngle

    val deltaAngle = newAngle - oldAngle

    //draw a ray from start point to new mouse point
    val newPt = new Vector2D(rampSegment.getUnitVector.getAngle + deltaAngle) * rampSegment.length
    val clamped =
    if (newPt.getAngle < min) new Vector2D(min) * rampSegment.length
    else if (newPt.getAngle > max) new Vector2D(max) * rampSegment.length
    else newPt
    rampSegment.endPoint = clamped
  }
}

class RotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends RampSegmentNode(rampSegment, mytransform) {
  line.addInputEventListener(new CursorHandler(Cursor.N_RESIZE_CURSOR))
  line.addInputEventListener(new RotationHandler(mytransform, line, rampSegment,0,PI/2))
}
