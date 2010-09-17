package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.common.phetcommon.model.MutableBoolean
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.Predef._
import java.lang.Math.PI
import edu.colorado.phet.motionseries.charts.MutableDouble
import edu.colorado.phet.motionseries.util.{MutableRange, ScalaMutableBoolean}

class MotionSeriesObject(_position: MutableDouble,
                         _velocity: MutableDouble,
                         _acceleration: MutableDouble,
                         _mass: MutableDouble,
                         _staticFriction: MutableDouble,
                         _kineticFriction: MutableDouble,
                         private var _height: Double,
                         private var _width: Double,
                         val positionMapper: Double => Vector2D,
                         val rampSegmentAccessor: Double => RampSegment,
                         rampChangeAdapter: Observable,
                         val wallsBounce: ScalaMutableBoolean,
                         val _wallsExist: MutableBoolean,
                         val wallRange: MutableRange,
                         thermalEnergyStrategy: Double => Double,
                         val surfaceFriction: () => Boolean,
                         private var _surfaceFrictionStrategy: SurfaceFrictionStrategy)
        extends Observable {
  def this(model: MotionSeriesModel, x: Double, width: Double, height: Double) = this (new MutableDouble(x), new MutableDouble, new MutableDouble, new MutableDouble(10),
    new MutableDouble, new MutableDouble, height, width, model.toPosition2D, model.rampSegmentAccessor, model.rampChangeAdapter,
    model.wallsBounce, model.walls, model.wallRange, model.thermalEnergyStrategy, model.surfaceFriction, model.surfaceFrictionStrategy)

  //This used to be lazily computed, but it detracting from performance due to the many calls to position2D, so now it is eagerly computed
  val _position2D = new Vector2DModel
  private var _motionStrategy: MotionStrategy = new Grounded(this)

  def updatePosition2D() = _position2D.value = motionStrategy.mapPosition
  _position.addListener(updatePosition2D)
  updatePosition2D()

  def updateForces() = motionStrategy.updateForces()

  //Recompute the forces when the position changes
  _position.addListener(updateForces)

  //Resolves: When turning on walls while objects are out of bounds and bouncy is enabled, can bounce infinitely.
  def clampBounds() = {
    if (position < wallRange().min) setPosition(wallRange().min + _width / 2)
    if (position > wallRange().max) setPosition(position = wallRange().max - _width / 2)
  }
  wallRange.addListener(clampBounds)

  if (_surfaceFrictionStrategy == null) throw new RuntimeException("Null surface friction strategy")
  private val _thermalEnergy = new MutableDouble
  private val _crashEnergy = new MutableDouble
  private val _time = new MutableDouble
  private var _airborneFloor = 0.0
  private val _gravity = new MutableDouble(-9.8)
  val workListeners = new ArrayBuffer[Double => Unit]
  private var _notificationsEnabled = true
  //This notion of crashing is only regarding falling off a cliff or off the ramp, not for crashing into a wall
  val crashListeners = new ArrayBuffer[() => Unit]
  //notified when the MotionSeriesObject is being removed
  val removalListeners = new ArrayBuffer[() => Unit]

  rampChangeAdapter.addListener(() => {
    updatePosition2D()
    updateForces()
    notifyListeners()
  })

  //values initialized and updated in motionStrategy.updateForces()
  val totalForce = new Vector2DModel
  val wallForce = new Vector2DModel
  val frictionForce = new Vector2DModel
  val normalForce = new Vector2DModel
  val gravityForce = new Vector2DModel
  val appliedForce = new Vector2DModel
  val _parallelAppliedForce = new MutableDouble

  def updateGravityForce() = {
    gravityForce.value = new Vector2D(0, gravity * mass)
    updateForces()
  }
  _gravity.addListener(updateGravityForce)
  _mass.addListener(updateGravityForce)
  updateGravityForce()

  val gravityForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.gravityForceColor, "Gravity Force".literal, "force.abbrev.gravity".translate, false, gravityForce, (a, b) => b, PI / 2)
  val normalForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.normalForceColor, "Normal Force".literal, "force.abbrev.normal".translate, true, normalForce, (a, b) => b, PI / 2)
  val totalForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.sumForceColor, "Sum of Forces".literal, "force.abbrev.total".translate, false, totalForce, (a, b) => b, 0) ////Net force vector label should always be above
  val appliedForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.appliedForceColor, "Applied Force".literal, "force.abbrev.applied".translate, false, appliedForce, (a, b) => b, PI / 2)
  val frictionForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.frictionForceColor, "Friction Force".literal, "force.abbrev.friction".translate, true, frictionForce, (a, b) => b, -PI / 2)
  val wallForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.wallForceColor, "Wall Force".literal, "force.abbrev.wall".translate, false, wallForce, (a, b) => b, PI / 2)
  val parallelAppliedForceListeners = new ArrayBuffer[() => Unit]

  private val wallCrashListeners = new ArrayBuffer[() => Unit]
  private val bounceListeners = new ArrayBuffer[() => Unit]

  def frictionless = state.staticFriction == 0 && state.kineticFriction == 0

  def gravity = _gravity.value

  def wallsExist = _wallsExist.getValue.booleanValue

  def state = {
    new MotionSeriesObjectState(position, velocity, acceleration, mass, staticFriction, kineticFriction, thermalEnergy, crashEnergy, time,
      parallelAppliedForce, gravityForce.value, normalForce.value, totalForce.value, appliedForce.value, frictionForce.value, wallForce.value)
  }

  def state_=(s: MotionSeriesObjectState) = {
    setPosition(s.position)
    setVelocity(s.velocity)
    mass = s.mass
    staticFriction = s.staticFriction
    kineticFriction = s.kineticFriction
    thermalEnergy = s.thermalEnergy
    crashEnergy = s.crashEnergy
    setTime(s.time)
    parallelAppliedForce = s.parallelAppliedForce
    gravityForce.value = s.gravityForce
    normalForce.value = s.normalForce
    totalForce.value = s.totalForce
    appliedForce.value = s.appliedForce
    frictionForce.value = s.frictionForce
    wallForce.value = s.wallForce

    notifyListeners()
  }

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

  def rampUnitVector = rampSegmentAccessor(position).unitVector

  def mass = _mass.value

  def width_=(w: Double) = {
    _width = w
    notifyListeners()
  }

  def position = _position.value

  def velocity = _velocity.value

  def translate(dx: Double) = setPosition(_position.value + dx)

  def height_=(height: Double) = {
    _height = height
    notifyListeners()
  }

  def height = _height

  def time = _time.value

  def setTime(t: Double) = {
    _time.value = t
  }

  def airborneFloor = _airborneFloor

  def airborneFloor_=(airborneFloor: Double) = this._airborneFloor = airborneFloor

  def getTotalEnergy = potentialEnergy + kineticEnergy + thermalEnergy

  def potentialEnergy = mass * gravity.abs * position2D.y

  def getAppliedWork = 0.0

  def crashEnergy_=(value: Double) = {
    if (value != state.crashEnergy) {
      _crashEnergy.value = value
      notifyListeners() //TODO: fix listeners
    }
  }

  def thermalEnergy_=(value: Double) = {
    if (value != state.thermalEnergy) {
      _thermalEnergy.value = value
      notifyListeners() //TODO: fix listeners
    }
  }

  def thermalEnergy = _thermalEnergy.value

  def rampThermalEnergy = thermalEnergy - crashEnergy

  def crashEnergy = _crashEnergy.value

  def getFrictiveWork = -thermalEnergy

  def getGravityWork = -potentialEnergy

  def getWallWork = 0.0

  def getNormalWork = 0.0

  def kineticEnergy = 1.0 / 2.0 * mass * velocity * velocity

  def getParallelComponent(f: Vector2D) = f dot rampUnitVector

  def notificationsEnabled = _notificationsEnabled

  def notificationsEnabled_=(b: Boolean) = _notificationsEnabled = b
  //allow global disabling of notifications since they are very expensive and called many times during Grounded.stepInTime
  override def notifyListeners() = {
    if (notificationsEnabled) {
      super.notifyListeners()
    }
  }

  //This method allows MotionSeriesObject subclasses to avoid thermal energy by overriding this to return 0.0
  def getThermalEnergy(x: Double) = thermalEnergyStrategy(x)

  def parallelAppliedForce = _parallelAppliedForce.value

  def parallelAppliedForce_=(value: Double) = {
    if (value != parallelAppliedForce) {
      _parallelAppliedForce.value = value
      updateForces()
      parallelAppliedForceListeners.foreach(_()) //TODO: move listeners into mutabledouble
      notifyListeners()
    }
  }

  def surfaceFrictionStrategy = _surfaceFrictionStrategy

  def surfaceFrictionStrategy_=(x: SurfaceFrictionStrategy) = {
    _surfaceFrictionStrategy = x
    notifyListeners()
  }

  def staticFriction = _staticFriction.value

  def kineticFriction = _kineticFriction.value

  def staticFriction_=(value: Double) {
    _staticFriction.value = value
    notifyListeners()

    if (kineticFriction > staticFriction)
      kineticFriction = staticFriction
  }

  def kineticFriction_=(value: Double) {
    _kineticFriction.value = value
    notifyListeners()

    //NP says to Increase static when you increase kinetic so that static >= kinetic.
    if (staticFriction < kineticFriction)
      staticFriction = kineticFriction
  }

  def forceToParallelAcceleration(f: Vector2D) = (f dot rampUnitVector) / mass

  def netForceToParallelVelocity(f: Vector2D, dt: Double) = velocity + forceToParallelAcceleration(f) * dt

  def setVelocity(velocity: Double) = {
    if (velocity != _velocity.value) {
      _velocity.value = velocity
      notifyListeners() //TODO: switch notification mechanism
    }
  }

  def mass_=(mass: Double) = {
    this._mass.value = mass
    notifyListeners() //TODO: fix listeners
  }

  //TODO: remove listener notification here so listeners must listen to positionChanged property
  def setPosition(position: Double) = {
    if (position != _position.value) {
      _position.value = position
      notifyListeners()
    }
  }

  def gravity_=(value: Double) = { //TODO: convert gravity to mutabledouble
    _gravity.value = value
    notifyListeners()
  }

  def motionStrategy = _motionStrategy

  def motionStrategy_=(s: MotionStrategy) = _motionStrategy = s

  def attach() = motionStrategy = new Grounded(this)

  def position2D = _position2D.value

  def getAngle = motionStrategy.getAngle

  def stepInTime(dt: Double) = motionStrategy.stepInTime(dt)

  def acceleration = forceToParallelAcceleration(totalForce())

  def isCrashed = motionStrategy.isCrashed

  def notifyCollidedWithWall() = for (wallCrashListener <- wallCrashListeners) wallCrashListener()

  def notifyBounced() = for (bounceListener <- bounceListeners) bounceListener()

  def addWallCrashListener(listener: () => Unit) = wallCrashListeners += listener

  def addBounceListener(listener: () => Unit) = bounceListeners += listener
}

/**Immutable memento for recording*/
case class MotionSeriesObjectState(position: Double,
                                   velocity: Double,
                                   acceleration: Double,
                                   mass: Double,
                                   staticFriction: Double,
                                   kineticFriction: Double,
                                   thermalEnergy: Double,
                                   crashEnergy: Double,
                                   time: Double,
                                   parallelAppliedForce: Double,
                                   gravityForce: Vector2D,
                                   normalForce: Vector2D,
                                   totalForce: Vector2D,
                                   appliedForce: Vector2D,
                                   frictionForce: Vector2D,
                                   wallForce: Vector2D)