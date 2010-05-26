package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import java.awt.geom.{Point2D, Line2D}
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.motionseries.graphics.{Rotatable}

case class RampSegmentState(startPoint: Vector2D, endPoint: Vector2D) { //don't use Point2D since it's not immutable
  def setStartPoint(newStartPoint: Vector2D) = new RampSegmentState(newStartPoint, endPoint)

  def setEndPoint(newEndPoint: Vector2D) = new RampSegmentState(startPoint, newEndPoint)

  def getUnitVector = (endPoint - startPoint).normalize

  def setAngle(angle: Double) = new RampSegmentState(startPoint, new Vector2D(angle) * (endPoint - startPoint).magnitude)

  def angle = (endPoint - startPoint).angle
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

  def getPivot = startPoint

  def length = (endPoint - startPoint).magnitude

  def getUnitVector = state.getUnitVector

  def setAngle(angle: Double) = {
    state = state.setAngle(angle)
    notifyListeners()
  }

  override def angle = state.angle

  override def angle_=(a: Double) = {
    super.angle_=(a)
    setAngle(a)
  }

  private var _wetness = 0.0 // 1.0 means max wetness
  def wetness = _wetness

  import java.lang.Math._
  def dropHit() = setWetness(min(_wetness + 0.1, 1.0))

  def setWetness(w: Double) = {
    _wetness = w
    wetnessListeners.foreach(_())
  }

  def resetWetness() = setWetness(0.0)

  def stepInTime(dt: Double) = {
    setWetness(max(_wetness - 0.01, 0.0))
    heatListeners.foreach(_())
  }

  val wetnessListeners = new ArrayBuffer[() => Unit]
  val heatListeners = new ArrayBuffer[() => Unit]

  private var _heat = 0.0 //Joules
  def heat = _heat

  def setHeat(heat: Double) = {
    _heat = heat
    heatListeners.foreach(_())
  }
}
