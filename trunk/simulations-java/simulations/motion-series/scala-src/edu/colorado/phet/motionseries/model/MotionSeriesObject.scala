package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable

/**Immutable memento for recording*/
case class MotionSeriesObjectState(position: Double, velocity: Double, acceleration: Double, mass: Double, staticFriction: Double, kineticFriction: Double, thermalEnergy: Double, crashEnergy: Double, time: Double) {
  def translate(dx: Double) = setPosition(position + dx)

  def setPosition(pos: Double) = copy(position = pos)

  def setVelocity(vel: Double) = copy(velocity = vel)

  def setStaticFriction(value: Double) = copy(staticFriction = value)

  def setKineticFriction(value: Double) = copy(kineticFriction = value)

  def setMass(m: Double) = copy(mass = m)

  def setThermalEnergy(value: Double) = copy(thermalEnergy = value)

  def setCrashEnergy(value: Double) = copy(crashEnergy = value)

  def setTime(value: Double) = copy(time = value)
}

case class Range(min: Double, max: Double)

abstract class MotionSeriesObject(private var _state: MotionSeriesObjectState,
                                  private var _height: Double,
                                  private var _width: Double,
                                  val positionMapper: Double => Vector2D,
                                  val rampSegmentAccessor: Double => RampSegment,
                                  model: Observable,
                                  val wallsBounce: () => Boolean,
                                  _wallsExist: => Boolean,
                                  val wallRange: () => Range,
                                  thermalEnergyStrategy: Double => Double)
        extends Observable {
  def frictionless = state.staticFriction == 0 && state.kineticFriction == 0

  private var _gravity = -9.8

  def gravity = _gravity

  def gravity_=(value: Double) = {
    _gravity = value
    notifyListeners()
  }

  def wallsExist = _wallsExist

  //This notion of crashing is only regarding falling off a cliff or off the ramp, not for crashing into a wall
  val crashListeners = new ArrayBuffer[() => Unit]

  def state = _state

  def state_=(s: MotionSeriesObjectState) = {_state = s; notifyListeners()}

  model.addListenerByName(notifyListeners)

  //notified when the MotionSeriesObject is being removed
  val removalListeners = new ArrayBuffer[() => Unit]

  /**
   * Notify that the MotionSeriesObject is being removed, and clear all listeners.
   */
  def remove() = {
    removalListeners.foreach(_())
    removeAllListeners()
  }

  override def removeAllListeners() = {
    super.removeAllListeners()
    crashListeners.clear()
    workListeners.clear()
  }

  def width = _width

  def maxX = position + _width / 2

  def minX = position - _width / 2

  def getVelocityVectorDirection: Double = getVelocityVectorDirection(velocity)

  def getVelocityVectorDirection(v: Double): Double = (positionMapper(position + v * 1E-6) - positionMapper(position - v * 1E-6)).angle

  def getVelocityVectorUnitVector: Vector2D = new Vector2D(getVelocityVectorDirection)

  def getVelocityVectorUnitVector(v: Double): Vector2D = new Vector2D(getVelocityVectorDirection(v))

  def position2D: Vector2D

  def rampUnitVector = rampSegmentAccessor(position).unitVector

  def mass = state.mass

  def width_=(w: Double) = {
    _width = w
    notifyListeners()
  }

  def position = state.position

  def velocity = state.velocity

  def translate(dx: Double) = {
    state = state.translate(dx)
    notifyListeners()
  }

  def height_=(height: Double) = {
    _height = height
    notifyListeners()
  }

  def height = _height

  def setVelocity(velocity: Double) = {
    if (velocity != state.velocity) {
      state = state.setVelocity(velocity)
      notifyListeners()
    }
  }

  def mass_=(mass: Double) = {
    state = state.setMass(mass)
    notifyListeners()
  }

  def time = state.time

  def setTime(t: Double) = state = state.setTime(t)

  private var _desiredPosition = 0.0

  def desiredPosition = _desiredPosition
  //so we can use a filter
  def setDesiredPosition(position: Double) = _desiredPosition = position

  def setPosition(position: Double) = {
    if (position != state.position) {
      state = state.setPosition(position)
      notifyListeners()
    }
  }

  private var _airborneFloor = 0.0

  def airborneFloor = _airborneFloor

  def airborneFloor_=(airborneFloor: Double) = {
    this._airborneFloor = airborneFloor
  }

  def getTotalEnergy = potentialEnergy + kineticEnergy + thermalEnergy

  def potentialEnergy = mass * gravity.abs * position2D.y

  def getAppliedWork = 0.0

  def crashEnergy_=(value: Double) = {
    if (value != state.crashEnergy) {
      state = state.setCrashEnergy(value)
      notifyListeners()
    }
  }

  def thermalEnergy_=(value: Double) = {
    if (value != state.thermalEnergy) {
      state = state.setThermalEnergy(value)
      notifyListeners()
    }
  }

  def thermalEnergy = state.thermalEnergy

  def rampThermalEnergy = thermalEnergy - crashEnergy

  def crashEnergy = state.crashEnergy

  def getFrictiveWork = -thermalEnergy

  def getGravityWork = -potentialEnergy

  def getWallWork = 0.0

  def getNormalWork = 0.0

  def kineticEnergy = 1.0 / 2.0 * mass * velocity * velocity

  def getAngle: Double

  def getParallelComponent(f: Vector2D) = f dot rampUnitVector

  val workListeners = new ArrayBuffer[Double => Unit]

  private var _notificationsEnabled = true

  def notificationsEnabled = _notificationsEnabled

  def notificationsEnabled_=(b: Boolean) = _notificationsEnabled = b
  //allow global disabling of notifications since they are very expensive and called many times during Grounded.stepInTime
  override def notifyListeners() = {
    if (notificationsEnabled) {
      super.notifyListeners()
    }
  }

  val stateHistory = new ArrayBuffer[MotionSeriesObjectState] //todo: memory leak

  def stepInTime(dt: Double) {
    stateHistory += state
  }

  def averageVelocity = {
    val velocities = for (i <- 0 until java.lang.Math.min(10, stateHistory.length)) yield stateHistory(stateHistory.length - 1 - i).velocity
    val sum = velocities.foldLeft(0.0)(_ + _)
    sum / velocities.size
  }

  def isCrashed: Boolean
}