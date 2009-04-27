package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import scalacommon.util.Observable
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

trait Rotatable extends Observable {
  def startPoint: Vector2D

  def endPoint_=(newPt: Vector2D)

  def length: Double

  def getUnitVector: Vector2D

  def endPoint: Vector2D

  def startPoint_=(newPt: Vector2D)

  def getPivot = new Vector2D

}
class RotationHandler(val mytransform: ModelViewTransform2D, val node: PNode, val rotatable: Rotatable, min: Double, max: Double) extends PBasicInputEventHandler {
  override def mouseDragged(event: PInputEvent) = {
    val modelPt = mytransform.viewToModel(event.getPositionRelativeTo(node.getParent))

    val deltaView = event.getDeltaRelativeTo(node.getParent)
    val deltaModel = mytransform.viewToModelDifferential(deltaView.width, deltaView.height)

    val oldPtModel = modelPt - deltaModel

    val oldAngle = (rotatable.getPivot - oldPtModel).getAngle
    val newAngle = (rotatable.getPivot - modelPt).getAngle

    val deltaAngle = newAngle - oldAngle

    //draw a ray from start point to new mouse point
    val newPt = new Vector2D(rotatable.getUnitVector.getAngle + deltaAngle) * rotatable.length
    val clamped =
    if (newPt.getAngle < min) {
      val value = new Vector2D(min) * rotatable.length
      println("ang was: " + newPt.getAngle + ", value=" + value)
      value
    }
    else if (newPt.getAngle > max) {
      val value = new Vector2D(max) * rotatable.length
      println("over max: " + value)
      value
    }
    else newPt

    println("endpt=" + clamped)
    rotatable.endPoint = clamped
  }
}

class RotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends RampSegmentNode(rampSegment, mytransform) {
  line.addInputEventListener(new CursorHandler(Cursor.N_RESIZE_CURSOR))
  line.addInputEventListener(new RotationHandler(mytransform, line, rampSegment, 0, PI / 2))
}

class ReverseRotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends RampSegmentNode(rampSegment, mytransform) {
  line.addInputEventListener(new CursorHandler(Cursor.N_RESIZE_CURSOR))
  line.addInputEventListener(new RotationHandler(mytransform, line, new Reverse(rampSegment).reverse, PI / 2 + 1E-6, PI - (1E-6))) //todo: atan2 returns angle between -pi and +pi, so end behavior is incorrect
}

class Reverse(target: Rotatable) {
  //this one rotates about the end point, facilitates reuse of some view classes while still allowing generalized model objects
  object reverse extends Rotatable {
    def length = target.length

    def startPoint = target.endPoint

    def endPoint = target.startPoint

    def getUnitVector = target.getUnitVector * -1

    def endPoint_=(newPt: Vector2D) = target.startPoint = newPt

    def startPoint_=(newPt: Vector2D) = target.endPoint = newPt

    override def addListenerByName(listener: => Unit) = target.addListenerByName(listener)

    override def notifyListeners() = target.notifyListeners()

    override def removeListener(listener: () => Unit) = target.removeListener(listener)

    override def addListener(listener: () => Unit) = target.addListener(listener)
  }
}



