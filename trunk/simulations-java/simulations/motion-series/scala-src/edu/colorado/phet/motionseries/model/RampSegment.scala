package edu.colorado.phet.motionseries.model

import java.awt.geom.{Point2D, Line2D}
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.motionseries.graphics.Rotatable

case class RampSegmentState(startPoint: Vector2D, endPoint: Vector2D) { //don't use Point2D since it's not immutable
  lazy val getUnitVector = (endPoint - startPoint).normalize
  lazy val angle = (endPoint - startPoint).angle
  lazy val length = (endPoint - startPoint).magnitude

  def setStartPoint(newStartPoint: Vector2D) = new RampSegmentState(newStartPoint, endPoint)

  def setEndPoint(newEndPoint: Vector2D) = new RampSegmentState(startPoint, newEndPoint)

  def setAngle(angle: Double) = new RampSegmentState(startPoint, new Vector2D(angle) * length)
}

class RampSegment(_state: RampSegmentState) extends Observable with Rotatable {
  private var state = _state

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

  def pivot = startPoint

  def length = state.length

  def unitVector = state.getUnitVector

  def setAngle(angle: Double) = {
    state = state.setAngle(angle)
    notifyListeners()
  }

  override def angle = state.angle

  override def angle_=(a: Double) = {
    super.angle_=(a)
    setAngle(a)
  }

}
