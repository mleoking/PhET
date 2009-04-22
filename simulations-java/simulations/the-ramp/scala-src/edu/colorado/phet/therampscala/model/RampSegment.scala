package edu.colorado.phet.therampscala.model


import graphics.Rotatable
import java.awt.geom.{Point2D, Line2D}
import scalacommon.util.Observable
import scalacommon.math.Vector2D
import scalacommon.Predef._

case class RampSegmentState(startPoint: Vector2D, endPoint: Vector2D) { //don't use Point2D since it's not immutable
  def setStartPoint(newStartPoint: Vector2D) = new RampSegmentState(newStartPoint, endPoint)

  def setEndPoint(newEndPoint: Vector2D) = new RampSegmentState(startPoint, newEndPoint)

  def getUnitVector = (endPoint - startPoint).normalize

  def setAngle(angle: Double) = new RampSegmentState(startPoint, new Vector2D(angle) * (endPoint - startPoint).magnitude)
}
class RampSegment(_state: RampSegmentState) extends Observable with Rotatable{
  var state = _state;
  def this(startPt: Point2D, endPt: Point2D) = this (new RampSegmentState(startPt, endPt))

  def toLine2D = new Line2D.Double(state.startPoint, state.endPoint)

  def startPoint = state.startPoint

  def endPoint = state.endPoint

  def startPoint_=(pt: Vector2D) = {
    state = state.setStartPoint(pt)
    notifyListeners()
  }

  def endPoint_=(pt: Vector2D) = {
    state = state.setEndPoint(pt)
    notifyListeners()
  }

  def length = (endPoint - startPoint).magnitude

  def getUnitVector = state.getUnitVector

  def setAngle(angle: Double) = {
    state = state.setAngle(angle)
    notifyListeners()
  }
}
