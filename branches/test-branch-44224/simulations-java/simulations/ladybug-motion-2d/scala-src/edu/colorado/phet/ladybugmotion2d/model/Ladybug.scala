package edu.colorado.phet.ladybugmotion2d.model

import java.awt.geom.Ellipse2D
import scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.ladybugmotion2d.LadybugDefaults

case class LadybugState(_position: Vector2D, _velocity: Vector2D, _acceleration: Vector2D, _angle: Double) {
  val position = _position
  val velocity = _velocity
  val acceleration = _acceleration
  val angle = _angle

  def this() = this (LadybugDefaults.defaultLocation, new Vector2D, new Vector2D, 0)

  def this(copy: LadybugState) = this (copy.position, copy.velocity, copy.acceleration, copy.angle)

  def translate(dx: Double, dy: Double): LadybugState = new LadybugState(new Vector2D(position.x + dx, position.y + dy),
    velocity, acceleration, angle)

  def rotate(dtheta: Double): LadybugState = new LadybugState(position, velocity, acceleration, angle + dtheta)

  def setAngle(theta: Double): LadybugState = new LadybugState(position, velocity, acceleration, theta)

  def setVelocity(v: Vector2D): LadybugState = new LadybugState(position, v, acceleration, angle)

  def setAcceleration(a: Vector2D): LadybugState = new LadybugState(position, velocity, a, angle)

  def setPosition(x: Vector2D): LadybugState = new LadybugState(x, velocity, acceleration, angle)
}

class Ladybug extends Bug with Observable {
  var state = new LadybugState

  val getRadius = 1.0

  def getEllipse(): Ellipse2D.Double = {
    val ellipse = new Ellipse2D.Double
    ellipse.setFrameFromCenter(getPosition, getPosition + new Vector2D(getRadius, getRadius))
    ellipse
  }

  def getBounds = getEllipse.getBounds2D

  def resetAll() = {
    state = new LadybugState
    notifyListeners
  }

  def translate(deltaPosition: Vector2D): Unit = translate(deltaPosition.x, deltaPosition.y)

  def translate(dx: Double, dy: Double) = {
    setPosition(getPosition + new Vector2D(dx, dy))
  }

  def rotate(dtheta: Double) = setAngle(getAngle + dtheta)

  def getPosition: Vector2D = state.position

  def getAngle: Double = state.angle

  def getAngleInvertY: Double = {
    val curAng = new Vector2D(state.angle)
    new Vector2D(curAng.x, -curAng.y).angle
  }

  def getAngleInFrame = if (LadybugDefaults.POSITIVE_Y_IS_UP) getAngleInvertY else getAngle

  def getState = state

  def getVelocity = state.velocity

  def getAcceleration = state.acceleration

  def setAcceleration(acceleration: Vector2D) = {
    state = state.setAcceleration(acceleration)
    notifyListeners
  }

  def setPosition(x: Double, y: Double): Unit = setPosition(new Vector2D(x, y))

  def setPosition(position: Vector2D) = {
    state = state.setPosition(position)
    notifyListeners
  }

  def setVelocity(velocity: Vector2D) = {
    state = state.setVelocity(velocity)
    notifyListeners
  }

  def setAngle(theta: Double) = {
    state = state.setAngle(theta)
    notifyListeners
  }

  def setState(_state: LadybugState) = {
    state = _state
    notifyListeners
  }

}