package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.common.phetcommon.model.MutableBoolean
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.Predef._
import java.lang.Math.PI

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

class MotionSeriesObject(private var _state: MotionSeriesObjectState,
                         private var _height: Double,
                         private var _width: Double,
                         val positionMapper: Double => Vector2D,
                         val rampSegmentAccessor: Double => RampSegment,
                         rampChangeAdapter: Observable,
                         val wallsBounce: SMutableBoolean,
                         val _wallsExist: MutableBoolean,
                         val wallRange: () => Range,
                         thermalEnergyStrategy: Double => Double,
                         val surfaceFriction: () => Boolean,
                         __surfaceFrictionStrategy: SurfaceFrictionStrategy)
        extends Observable {
  private var _gravity = -9.8
  //This notion of crashing is only regarding falling off a cliff or off the ramp, not for crashing into a wall
  val crashListeners = new ArrayBuffer[() => Unit]
  //notified when the MotionSeriesObject is being removed
  val removalListeners = new ArrayBuffer[() => Unit]  
  rampChangeAdapter.addListenerByName(notifyListeners)
  
  //values updated in the MotionStrategy
  val totalForce = new Vector2DModel()
  val wallForce = new Vector2DModel()
  val frictionForce = new Vector2DModel()
  val normalForce = new Vector2DModel() 
  val gravityForce = new Vector2DModel(new Vector2D(0, gravity * mass))//TODO: Update if mass changes
  val appliedForce = new Vector2DModel()

  private var _parallelAppliedForce = 0.0//TODO: convert to mutableDouble
  val gravityForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.gravityForceColor, "Gravity Force".literal, "force.abbrev.gravity".translate, false, gravityForce, (a, b) => b, PI / 2)
  val normalForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.normalForceColor, "Normal Force".literal, "force.abbrev.normal".translate, true, normalForce, (a, b) => b, PI / 2)
  val totalForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.sumForceColor, "Sum of Forces".literal, "force.abbrev.total".translate, false, totalForce, (a, b) => b, 0) ////Net force vector label should always be above
  val appliedForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.appliedForceColor, "Applied Force".literal, "force.abbrev.applied".translate, false, appliedForce, (a, b) => b, PI / 2)
  val frictionForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.frictionForceColor, "Friction Force".literal, "force.abbrev.friction".translate, true, frictionForce, (a, b) => b, -PI / 2)
  val wallForceVector = new MotionSeriesObjectVector(MotionSeriesDefaults.wallForceColor, "Wall Force".literal, "force.abbrev.wall".translate, false, wallForce, (a, b) => b, PI / 2)
  val parallelAppliedForceListeners = new ArrayBuffer[() => Unit]
  
  private var _motionStrategy: MotionStrategy = new Grounded(this)
  stepInTime(0.0)//Update vectors using the motion strategy
  
  def frictionless = state.staticFriction == 0 && state.kineticFriction == 0

  def gravity = _gravity

  def wallsExist = _wallsExist.getValue.booleanValue

  def state = _state

  def state_=(s: MotionSeriesObjectState) = {_state = s; notifyListeners()}

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
//
  def getVelocityVectorUnitVector: Vector2D = new Vector2D(getVelocityVectorDirection)

  def getVelocityVectorUnitVector(v: Double): Vector2D = new Vector2D(getVelocityVectorDirection(v))

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

  def time = state.time

  def setTime(t: Double) = state = state.setTime(t)

  private var _desiredPosition = 0.0

  def desiredPosition = _desiredPosition
  //so we can use a filter
  def setDesiredPosition(position: Double) = _desiredPosition = position

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

  //This method allows MotionSeriesObject subclasses to avoid thermal energy by overriding this to return 0.0
  def getThermalEnergy(x: Double) = thermalEnergyStrategy(x)
  
  def parallelAppliedForce = _parallelAppliedForce

  def parallelAppliedForce_=(value: Double) = {
    if (value != _parallelAppliedForce) {
      _parallelAppliedForce = value
      parallelAppliedForceListeners.foreach(_())
      notifyListeners()
    }
  }

  private var _surfaceFrictionStrategy = __surfaceFrictionStrategy

  def surfaceFrictionStrategy = _surfaceFrictionStrategy

  def surfaceFrictionStrategy_=(x: SurfaceFrictionStrategy) = {
    _surfaceFrictionStrategy = x
    notifyListeners()
  }

  def staticFriction = state.staticFriction

  def kineticFriction = state.kineticFriction

  def staticFriction_=(value: Double) {
    state = state.setStaticFriction(value)
    notifyListeners()

    if (kineticFriction > staticFriction)
      kineticFriction = staticFriction
  }

  def kineticFriction_=(value: Double) {
    state = state.setKineticFriction(value)
    notifyListeners()

    //NP says to Increase static when you increase kinetic so that static >= kinetic.
    if (staticFriction < kineticFriction)
      staticFriction = kineticFriction
  }

  def forceToParallelAcceleration(f: Vector2D) = (f dot rampUnitVector) / mass

  def netForceToParallelVelocity(f: Vector2D, dt: Double) = velocity + forceToParallelAcceleration(f) * dt

  //todo: switch to property based notifications so we don't have to remember to do this kind of fine-grained notification
  def setVelocity(velocity: Double) = {
    if (velocity != state.velocity) {
      state = state.setVelocity(velocity)
      notifyListeners()
    }
  }

  def mass_=(mass: Double) = {
    state = state.setMass(mass)//TODO: convert mass to mutable boolean
    notifyListeners()
  }

  def setPosition(position: Double) = {
    val originalPosition = this.position //have to record the position here since the next line changes the super.position
    if (position != state.position) {
      state = state.setPosition(position)
      notifyListeners()
    }
  }

  def gravity_=(value: Double) = {//TODO: convert gravity to mutabledouble
    _gravity = value
    notifyListeners()
  }

  def motionStrategy = _motionStrategy

  def motionStrategy_=(s: MotionStrategy) = _motionStrategy = s

  def attach() = motionStrategy = new Grounded(this)

  def position2D = motionStrategy.position2D

  def getAngle = motionStrategy.getAngle

  def stepInTime(dt: Double) = motionStrategy.stepInTime(dt)

  def acceleration = forceToParallelAcceleration(totalForce())

  def isCrashed = motionStrategy.isCrashed

  def notifyCollidedWithWall() = for (wallCrashListener <- wallCrashListeners) wallCrashListener()

  def notifyBounced() = for (bounceListener <- bounceListeners) bounceListener()

  private val wallCrashListeners = new ArrayBuffer[() => Unit]

  private val bounceListeners = new ArrayBuffer[() => Unit]

  def addWallCrashListener(listener: () => Unit) = wallCrashListeners += listener

  def addBounceListener(listener: () => Unit) = bounceListeners += listener
}

object MotionSeriesObject {
  def apply(model: MotionSeriesModel, x: Double, width: Double, height: Double) = {
    new MotionSeriesObject(new MotionSeriesObjectState(x, 0, 0, 10, 0, 0, 0.0, 0.0, 0.0), height, width, model.toPosition2D, model.rampSegmentAccessor, model.rampChangeAdapter,
      model.bounce, model.walls, model.wallRange, model.thermalEnergyStrategy, model.surfaceFriction, model.surfaceFrictionStrategy)
  }
}