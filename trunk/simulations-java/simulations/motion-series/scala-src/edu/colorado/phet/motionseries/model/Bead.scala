package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.common.motion.model.TimeData
import edu.colorado.phet.common.motion.MotionMath
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.Predef._

/**Immutable memento for recording*/
case class BeadState(position: Double, velocity: Double, mass: Double, staticFriction: Double, kineticFriction: Double, thermalEnergy: Double, crashEnergy: Double, time: Double) {
  def translate(dx: Double) = setPosition(position + dx)

  def setPosition(pos: Double) = new BeadState(pos, velocity, mass, staticFriction, kineticFriction, thermalEnergy, crashEnergy, time)

  def setVelocity(vel: Double) = new BeadState(position, vel, mass, staticFriction, kineticFriction, thermalEnergy, crashEnergy, time)

  def setStaticFriction(value: Double) = new BeadState(position, velocity, mass, value, kineticFriction, thermalEnergy, crashEnergy, time)

  def setKineticFriction(value: Double) = new BeadState(position, velocity, mass, staticFriction, value, thermalEnergy, crashEnergy, time)

  def setMass(m: Double) = new BeadState(position, velocity, m, staticFriction, kineticFriction, thermalEnergy, crashEnergy, time)

  def setThermalEnergy(value: Double) = new BeadState(position, velocity, mass, staticFriction, kineticFriction, value, crashEnergy, time)

  def setCrashEnergy(value: Double) = new BeadState(position, velocity, mass, staticFriction, kineticFriction, thermalEnergy, value, time)

  def setTime(value: Double) = new BeadState(position, velocity, mass, staticFriction, kineticFriction, thermalEnergy, crashEnergy, value)
}

case class Range(min: Double, max: Double)

/**
 * Ways to refactor this to modularize support for moving man:
 *
 * 1. Subclass: MovingManBead, provides overrides for functions like getAcceleration
 * 2. Strategy pattern: PositionDriven, VelocityDriven, AccelerationDriven
 *
 * maybe need both.
 */
abstract class Bead(private var _state: BeadState,
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
  private var _gravity = -9.8

  def gravity = _gravity

  def gravity_=(value: Double) = {
    _gravity = value
    notifyListeners()
  }

  def wallsExist = _wallsExist

  val crashListeners = new ArrayBuffer[() => Unit]

  def state = _state

  def state_=(s: BeadState) = {_state = s; notifyListeners()}

  model.addListenerByName(notifyListeners)

  //notified when the bead is being removed
  val removalListeners = new ArrayBuffer[() => Unit]

  def remove() = removalListeners.foreach(_())

  def width = _width

  def maxX = position + _width / 2

  def minX = position - _width / 2

  def getVelocityVectorDirection: Double = getVelocityVectorDirection(velocity)

  def getVelocityVectorDirection(v: Double): Double = (positionMapper(position + v * 1E-6) - positionMapper(position - v * 1E-6)).getAngle

  def getVelocityVectorUnitVector: Vector2D = new Vector2D(getVelocityVectorDirection)

  def getVelocityVectorUnitVector(v: Double): Vector2D = new Vector2D(getVelocityVectorDirection(v))

  def position2D:Vector2D

  def getRampUnitVector = rampSegmentAccessor(position).getUnitVector

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

  //todo privatize
  object velocityMode
  object positionMode
  object accelerationMode

  private var _mode: AnyRef = accelerationMode

  def mode = _mode

  def setAccelerationMode() = {
    _mode = accelerationMode
  }

  def setVelocityMode() = {
    _mode = velocityMode
  }

  def setPositionMode() = {
    _mode = positionMode
  }

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

  def getTotalEnergy = getPotentialEnergy + getKineticEnergy + getThermalEnergy

  def getPotentialEnergy = mass * gravity.abs * position2D.y

  def getAppliedWork = 0.0

  def setCrashEnergy(value: Double) = {
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

  def getThermalEnergy = state.thermalEnergy

  def getRampThermalEnergy = getThermalEnergy - getCrashEnergy

  def getCrashEnergy = state.crashEnergy

  def getFrictiveWork = -getThermalEnergy

  def getGravityWork = -getPotentialEnergy

  def getWallWork = 0.0

  def getNormalWork = 0.0

  def getKineticEnergy = 1.0 / 2.0 * mass * velocity * velocity

  def getAngle:Double

  def getParallelComponent(f: Vector2D) = f dot getRampUnitVector

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

  val stateHistory = new ArrayBuffer[BeadState] //todo: memory leak

  def stepInTime(dt: Double) = {
    stateHistory += state
  }

  def averageVelocity = {
    val velocities = for (i <- 0 until java.lang.Math.min(10, stateHistory.length)) yield stateHistory(stateHistory.length - 1 - i).velocity
    val sum = velocities.foldLeft(0.0)(_ + _)
    //    println("velocities = "+velocities.toList)
    sum / velocities.size
  }
}

/**
 * This adds Force functionality to the Bead model class, such as ability to get force components and to set applied force.
 */
class ForceBead(_state: BeadState,
                _height: Double,
                _width: Double,
                positionMapper: Double => Vector2D,
                rampSegmentAccessor: Double => RampSegment,
                model: Observable,
                val surfaceFriction: () => Boolean,
                wallsBounce: () => Boolean,
                val __surfaceFrictionStrategy: SurfaceFrictionStrategy,
                _wallsExist: => Boolean,
                wallRange: () => Range,
                thermalEnergyStrategy: Double => Double)
        extends Bead(_state, _height, _width, positionMapper, rampSegmentAccessor, model, wallsBounce, _wallsExist, wallRange, thermalEnergyStrategy) {
  //This method allows bead subclasses to avoid thermal energy by overriding this to return 0.0
  def getThermalEnergy(x: Double) = thermalEnergyStrategy(x)

  private var _parallelAppliedForce = 0.0
  val gravityForceVector = new BeadVector(MotionSeriesDefaults.gravityForceColor, "Gravity Force".literal, "force.abbrev.gravity".translate, false, () => gravityForce, (a, b) => b)
  val normalForceVector = new BeadVector(MotionSeriesDefaults.normalForceColor, "Normal Force".literal, "force.abbrev.normal".translate, true, () => normalForce, (a, b) => b)
  val totalForceVector = new BeadVector(MotionSeriesDefaults.totalForceColor, "Sum of Forces".literal, "force.abbrev.total".translate, false, () => totalForce, (a, b) => b)
  val appliedForceVector = new BeadVector(MotionSeriesDefaults.appliedForceColor, "Applied Force".literal, "force.abbrev.applied".translate, false, () => appliedForce, (a, b) => b)
  val frictionForceVector = new BeadVector(MotionSeriesDefaults.frictionForceColor, "Friction Force".literal, "force.abbrev.friction".translate, true, () => frictionForce, (a, b) => b)
  val wallForceVector = new BeadVector(MotionSeriesDefaults.wallForceColor, "Wall Force".literal, "force.abbrev.wall".translate, false, () => wallForce, (a, b) => b)
  val velocityVector = new BeadVector(MotionSeriesDefaults.velocityColor, "Velocity".literal, "velocity", false, () => getRampUnitVector * velocity, (a, b) => b) //todo: translate
  val accelerationVector = new BeadVector(MotionSeriesDefaults.accelerationColor, "Acceleration".literal, "acceleration", false, () => getRampUnitVector * acceleration, (a, b) => b) //todo: translate
  //chain listeners
  normalForceVector.addListenerByName(frictionForceVector.notifyListeners())
  //todo: add normalForceVector notification when changing friction coefficients

  appliedForceVector.addListenerByName(totalForceVector.notifyListeners())
  gravityForceVector.addListenerByName(totalForceVector.notifyListeners())
  normalForceVector.addListenerByName(totalForceVector.notifyListeners())
  frictionForceVector.addListenerByName(totalForceVector.notifyListeners())

  addListenerByName(appliedForceVector.notifyListeners()) //todo: just listen for changes to applied force parallel component

  model.addListenerByName(frictionForceVector.notifyListeners())

  def totalForce = gravityForce + normalForce + appliedForce + frictionForce + wallForce

  def wallForce = motionStrategy.wallForce

  def frictionForce = motionStrategy.frictionForce

  def normalForce = motionStrategy.normalForce

  def gravityForce = new Vector2D(0, gravity * mass)

  def parallelAppliedForce = _parallelAppliedForce

  val parallelAppliedForceListeners = new ArrayBuffer[() => Unit]

  def parallelAppliedForce_=(value: Double) = {
    _parallelAppliedForce = value
    parallelAppliedForceListeners.foreach(_())
    appliedForceVector.notifyListeners()
    notifyListeners()
  }

  def appliedForce = getRampUnitVector * _parallelAppliedForce

  private var _surfaceFrictionStrategy = __surfaceFrictionStrategy

  def surfaceFrictionStrategy = _surfaceFrictionStrategy

  def surfaceFrictionStrategy_=(x: SurfaceFrictionStrategy) = {
    _surfaceFrictionStrategy = x
    frictionForceVector.notifyListeners()
    notifyListeners()
  }

  def staticFriction = state.staticFriction

  def kineticFriction = state.kineticFriction

  def staticFriction_=(value: Double) {
    state = state.setStaticFriction(value)
    frictionForceVector.notifyListeners()
    notifyListeners()

    if (kineticFriction > staticFriction)
      kineticFriction = staticFriction
  }

  def kineticFriction_=(value: Double) {
    state = state.setKineticFriction(value)
    frictionForceVector.notifyListeners()
    notifyListeners()

    //NP says to Increase static when you increase kinetic so that static >= kinetic.
    if (staticFriction < kineticFriction)
      staticFriction = kineticFriction
  }

  def forceToParallelAcceleration(f: Vector2D) = (f dot getRampUnitVector) / mass

  def netForceToParallelVelocity(f: Vector2D, dt: Double) = velocity + forceToParallelAcceleration(f) * dt

  def acceleration = {
    if (mode == positionMode) {
      val timeData = for (i <- 0 until java.lang.Math.min(10, stateHistory.length))
      yield new TimeData(stateHistory(stateHistory.length - 1 - i).position, stateHistory(stateHistory.length - 1 - i).time)
      MotionMath.getSecondDerivative(timeData.toArray).getValue
    }
    else if (mode == velocityMode) {
      //todo: maybe better to estimate 2nd derivative of position instead of 1st derivative of velocity?
      val timeData = for (i <- 0 until java.lang.Math.min(10, stateHistory.length))
      yield new TimeData(stateHistory(stateHistory.length - 1 - i).velocity, stateHistory(stateHistory.length - 1 - i).time)
      MotionMath.estimateDerivative(timeData.toArray)
    }
    else //if (mode == accelerationMode)
      {
        forceToParallelAcceleration(totalForce)
      }
  }

  override def setVelocity(velocity: Double) = {
    super.setVelocity(velocity)
    frictionForceVector.notifyListeners() //todo: maybe this could be omitted during batch updates for performance
  }

  override def mass_=(mass: Double) = {
    super.mass_=(mass)
    gravityForceVector.notifyListeners()
    normalForceVector.notifyListeners()
  }

  override def setPosition(position: Double) = {
    super.setPosition(position)
    //todo: maybe this could be omitted during batch updates for performance
    normalForceVector.notifyListeners() //since ramp segment or motion state might have changed; could improve performance on this by only sending notifications when we are sure the ramp segment has changed
    frictionForceVector.notifyListeners() //todo: omit this call since it's probably covered by the normal force call above
    wallForceVector.notifyListeners()
  }

  override def gravity_=(value: Double) = {
    super.gravity_=(value)
    normalForceVector.notifyListeners()
    gravityForceVector.notifyListeners()
  }

  private var _motionStrategy: MotionStrategy = new Grounded(this)

  def motionStrategy = _motionStrategy

  def motionStrategy_=(s: MotionStrategy) = _motionStrategy = s

  def attach() = motionStrategy = new Grounded(this)

  def position2D =  motionStrategy.position2D

  def getAngle =  motionStrategy.getAngle

  override def stepInTime(dt: Double) = {
    super.stepInTime(dt)
    motionStrategy.stepInTime(dt)
  }
}