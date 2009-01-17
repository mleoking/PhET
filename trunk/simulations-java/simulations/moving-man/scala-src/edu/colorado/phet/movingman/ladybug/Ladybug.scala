package edu.colorado.phet.movingman.ladybug

import scala.collection.mutable.ArrayBuffer

trait ObservableS {
  private val listeners = new ArrayBuffer[() => Unit]

  def notifyListeners(): Unit = listeners.foreach(_())

  def addListener(listener: () => Unit) = listeners += listener
}

class Observable[T] {
  private val listeners = new ArrayBuffer[T => Unit]

  def notifyListeners(obj: T): Unit = listeners.foreach(_(obj))

  def addListener(listener: T => Unit) = listeners += listener
}

case class LadybugState(_position: Vector2D, _velocity: Vector2D, _acceleration: Vector2D, _angle: Double) {
  val position = _position
  val velocity = _velocity
  val acceleration = _acceleration
  val angle = _angle

  def this() = this (new Vector2D, new Vector2D, new Vector2D, 0)

  def this(copy: LadybugState) = this (copy.position, copy.velocity, copy.acceleration, copy.angle)

  def translate(dx: Double, dy: Double): LadybugState = new LadybugState(new Vector2D(position.x + dx, position.y + dy),
    velocity, acceleration, angle)

  def rotate(dtheta: Double): LadybugState = new LadybugState(position, velocity, acceleration, angle + dtheta)

  def setAngle(theta: Double): LadybugState = new LadybugState(position, velocity, acceleration, theta)

  def setVelocity(v: Vector2D): LadybugState = new LadybugState(position, v, acceleration, angle)

  def setAcceleration(a: Vector2D): LadybugState = new LadybugState(position, velocity, a, angle)

  def setPosition(x: Vector2D): LadybugState = new LadybugState(x, velocity, acceleration, angle)
}

class Ladybug extends Observable[Ladybug] {
  var state = new LadybugState

  def translate(deltaPosition: Vector2D): Unit = translate(deltaPosition.x, deltaPosition.y)

  def translate(dx: Double, dy: Double) = {
    state = state.translate(dx, dy);
    notifyListeners(this)
  }

  def rotate(dtheta: Double) = setAngle(getAngle + dtheta)

  def getPosition: Vector2D = state.position

  def getAngle: Double = state.angle

  def getState = state

  def getVelocity = state.velocity

  def getAcceleration = state.acceleration

  def setAcceleration(acceleration: Vector2D) = {
    state = state.setAcceleration(acceleration)
    notifyListeners(this)
  }

  def setPosition(position: Vector2D) = {
    state = state.setPosition(position)
    notifyListeners(this)
  }

  def setVelocity(velocity: Vector2D) = {
    state = state.setVelocity(velocity)
    notifyListeners(this)
  }

  def setAngle(theta: Double) = {
    state = state.setAngle(theta)
    notifyListeners(this)
  }

  def setState(_state: LadybugState) = {
    state = _state
    notifyListeners(this)
  }

}