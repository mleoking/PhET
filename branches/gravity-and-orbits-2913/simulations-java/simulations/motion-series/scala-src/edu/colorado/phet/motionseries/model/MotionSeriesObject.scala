package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.Predef._
import java.lang.Math.PI
import edu.colorado.phet.motionseries.charts.MutableDouble
import edu.colorado.phet.motionseries.util.{MutableRange, ScalaMutableBoolean}
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty

class MotionSeriesObject(_position: MutableDouble,
                         _velocity: MutableDouble,
                         _acceleration: MutableDouble,
                         _mass: MutableDouble,
                         _staticFriction: MutableDouble,
                         _kineticFriction: MutableDouble,
                         private var _height: Double,
                         private var _width: Double,
                         val positionMapper: PositionMapper,
                         val rampSegmentAccessor: Double => RampSegment,
                         val wallsBounce: ScalaMutableBoolean,
                         val _wallsExist: BooleanProperty,
                         val wallRange: MutableRange,
                         thermalEnergyStrategy: Double => Double,
                         val surfaceFriction: () => Boolean,
                         private var _surfaceFrictionStrategy: SurfaceFrictionStrategy,
                         private val doClampBounds: Boolean = false) { //bounds should be clamped for mobile objects only
  def this(model: MotionSeriesModel,
           x: Double,
           width: Double,
           height: Double,
           doClampBounds: Boolean) =
    this (new MutableDouble(x), new MutableDouble, new MutableDouble, new MutableDouble(10),
      new MutableDouble, new MutableDouble, height, width, model.positionMapper, model.rampSegmentAccessor,
      model.wallsBounce, model.walls, model.wallRange, model.thermalEnergyStrategy, model.surfaceFriction, model.surfaceFrictionStrategy, doClampBounds)

  def this(model: MotionSeriesModel,
           x: Double,
           width: Double,
           height: Double) =
    this (model, x, width, height, false)

  private var _userSpecifiedPosition = false //Prevent the object from being moved by the motionStrategy when the object's position is under the user's direct control

  assert(positionMapper != null, "PositionMapper must be non-null")

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
    if (doClampBounds) {
      if (position < wallRange().min) position = wallRange().min + _width / 2
      if (position > wallRange().max) position = wallRange().max - _width / 2
    }
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

  positionMapper.addListener(() => {
    updatePosition2D()
    updateForces()
  })

  //values initialized and updated in motionStrategy.updateForces()
  val totalForce = new Vector2DModel
  val wallForce = new Vector2DModel
  val frictionForce = new Vector2DModel
  val normalForce = new Vector2DModel
  val gravityForce = new Vector2DModel
  val appliedForce = new Vector2DModel
  val _parallelAppliedForce = new MutableDouble

  _parallelAppliedForce.addListener(updateForces)

  def updateGravityForce() = {
    gravityForce.value = new Vector2D(0, gravity * mass)
    updateForces()
  }
  _gravity.addListener(updateGravityForce)
  _mass.addListener(updateGravityForce)
  updateGravityForce()

  val gravityForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.gravityForceColor, "gravityForce".translate, "force.abbrev.gravity".translate, false, gravityForce, (a, b) => b, PI / 2)
  val normalForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.normalForceColor, "normalForce".translate, "force.abbrev.normal".translate, true, normalForce, (a, b) => b, PI / 2)
  val totalForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.sumForceColor, "totalForce".translate, "force.abbrev.total".translate, false, totalForce, (a, b) => b, 0) ////Net force vector label should always be above
  val appliedForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.appliedForceColor, "appliedForce".translate, "force.abbrev.applied".translate, false, appliedForce, (a, b) => b, PI / 2)
  val frictionForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.frictionForceColor, "frictionForce".translate, "force.abbrev.friction".translate, true, frictionForce, (a, b) => b, -PI / 2)
  val wallForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.wallForceColor, "wallForce".translate, "force.abbrev.wall".translate, false, wallForce, (a, b) => b, PI / 2)

  private val wallCrashListeners = new ArrayBuffer[() => Unit]
  private val bounceListeners = new ArrayBuffer[() => Unit]
  val stepListeners = new ArrayBuffer[() => Unit]

  def frictionless = state.staticFriction == 0 && state.kineticFriction == 0

  def gravity = _gravity.value

  def wallsExist = _wallsExist.get.booleanValue

  def state = {
    new MotionSeriesObjectState(position, velocity, acceleration, mass, staticFriction, kineticFriction, thermalEnergy, crashEnergy, time,
      parallelAppliedForce, gravityForce.value, normalForce.value, totalForce.value, appliedForce.value, frictionForce.value, wallForce.value)
  }

  def state_=(s: MotionSeriesObjectState) = {
    position = s.position
    velocity = s.velocity
    mass = s.mass
    staticFriction = s.staticFriction
    kineticFriction = s.kineticFriction
    thermalEnergy = s.thermalEnergy
    crashEnergy = s.crashEnergy
    time = s.time
    parallelAppliedForce = s.parallelAppliedForce
    gravityForce.value = s.gravityForce
    normalForce.value = s.normalForce
    totalForce.value = s.totalForce
    appliedForce.value = s.appliedForce
    frictionForce.value = s.frictionForce
    wallForce.value = s.wallForce
  }

  //Notify that the MotionSeriesObject is being removed, note this does not remove any listeners
  def remove() = removalListeners.foreach(_())

  def width = _width

  def maxX = position + _width / 2

  def minX = position - _width / 2

  def getVelocityVectorDirection: Double = getVelocityVectorDirection(velocity)

  def getVelocityVectorDirection(v: Double): Double = (positionMapper(position + v * 1E-6) - positionMapper(position - v * 1E-6)).angle

  def getVelocityVectorUnitVector: Vector2D = new Vector2D(getVelocityVectorDirection)

  def getVelocityVectorUnitVector(v: Double): Vector2D = new Vector2D(getVelocityVectorDirection(v))

  def rampUnitVector = rampSegmentAccessor(position).unitVector

  def mass = _mass.value

  def massProperty = _mass

  def gravityProperty = _gravity

  def width_=(w: Double) = _width = w

  def position = _position.value

  def velocity = _velocity.value

  def translate(dx: Double) = position = _position.value + dx

  def height_=(height: Double) = _height = height

  def height = _height

  def time = _time.value

  def time_=(t: Double) = _time.value = t

  def airborneFloor = _airborneFloor

  def airborneFloor_=(airborneFloor: Double) = this._airborneFloor = airborneFloor

  def getTotalEnergy = potentialEnergy + kineticEnergy + thermalEnergy

  def potentialEnergy = mass * gravity.abs * position2D.y

  def crashEnergy_=(value: Double) = _crashEnergy.value = value

  def thermalEnergy_=(value: Double) = _thermalEnergy.value = value

  def thermalEnergy = _thermalEnergy.value

  def rampThermalEnergy = thermalEnergy - crashEnergy

  def crashEnergy = _crashEnergy.value

  def kineticEnergy = 1.0 / 2.0 * mass * velocity * velocity

  def getParallelComponent(f: Vector2D) = f dot rampUnitVector

  //This method allows MotionSeriesObject subclasses to avoid thermal energy by overriding this to return 0.0
  def getThermalEnergy(x: Double) = thermalEnergyStrategy(x)

  def parallelAppliedForce = _parallelAppliedForce.value

  def parallelAppliedForce_=(value: Double) = _parallelAppliedForce.value = value

  def parallelAppliedForceProperty = _parallelAppliedForce

  def surfaceFrictionStrategy = _surfaceFrictionStrategy

  def surfaceFrictionStrategy_=(x: SurfaceFrictionStrategy) = _surfaceFrictionStrategy = x

  def staticFriction = _staticFriction.value

  def staticFrictionProperty = _staticFriction

  def positionProperty = _position

  def position2DProperty = _position2D

  def kineticFrictionProperty = _kineticFriction

  def kineticFriction = _kineticFriction.value

  def staticFriction_=(value: Double) {
    _staticFriction.value = value

    if (kineticFriction > staticFriction)
      kineticFriction = staticFriction
  }

  def kineticFriction_=(value: Double) {
    _kineticFriction.value = value

    //Increase static when you increase kinetic so that static >= kinetic.
    if (staticFriction < kineticFriction)
      staticFriction = kineticFriction
  }

  def forceToParallelAcceleration(f: Vector2D) = (f dot rampUnitVector) / mass

  def netForceToParallelVelocity(f: Vector2D, dt: Double) = velocity + forceToParallelAcceleration(f) * dt

  def velocity_=(velocity: Double) = _velocity.value = velocity

  def mass_=(mass: Double) = _mass.value = mass

  def position_=(position: Double) = _position.value = position

  def gravity_=(value: Double) = _gravity.value = value

  def motionStrategy = _motionStrategy

  def motionStrategy_=(s: MotionStrategy) = _motionStrategy = s

  def attach() = motionStrategy = new Grounded(this)

  def position2D = _position2D.value

  def getAngle = motionStrategy.getAngle

  def stepInTime(dt: Double) = {
    if (!userSpecifiedPosition) { //If the user is holding the position slider, don't let the object move to another position
      motionStrategy.stepInTime(dt)
    }
    for (listener <- stepListeners) listener()
  }

  def acceleration = forceToParallelAcceleration(totalForce())

  def isCrashed = motionStrategy.isCrashed

  def notifyCollidedWithWall() = for (wallCrashListener <- wallCrashListeners) wallCrashListener()

  def notifyBounced() = for (bounceListener <- bounceListeners) bounceListener()

  def addWallCrashListener(listener: () => Unit) = wallCrashListeners += listener

  def addBounceListener(listener: () => Unit) = bounceListeners += listener

  def userSpecifiedPosition_=(b: Boolean) = _userSpecifiedPosition = b

  def userSpecifiedPosition = _userSpecifiedPosition
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