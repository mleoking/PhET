package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import common.motion.model.TimeData
import common.motion.MotionMath
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import motionseries.MotionSeriesDefaults
import motionseries.Predef._

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

object Bead {
  private var index = 0

  def nextIndex() = {
    val nextInd = index
    index = index + 1
    nextInd
  }
}
class Bead(private var _state: BeadState,
           private var _height: Double,
           private var _width: Double,
           val positionMapper: Double => Vector2D,
           val rampSegmentAccessor: Double => RampSegment,
           model: Observable,
           val surfaceFriction: () => Boolean,
           __surfaceFrictionStrategy: SurfaceFrictionStrategy,
           _wallsExist: => Boolean,
           val wallRange: () => Range)
        extends Observable {
  def wallsExist = _wallsExist

  val id = Bead.nextIndex()
  val crashListeners = new ArrayBuffer[() => Unit]
  private var _gravity = -9.8

  def gravity = _gravity

  def gravity_=(value: Double) = {
    _gravity = value
    normalForceVector.notifyListeners()
    gravityForceVector.notifyListeners()
    notifyListeners()
  }

  def state = _state

  def state_=(s: BeadState) = {_state = s; notifyListeners()}

  private var _parallelAppliedForce = 0.0
  private var _motionStrategy: MotionStrategy = new Grounded(this)

  def motionStrategy = _motionStrategy

  def motionStrategy_=(s: MotionStrategy) = {
    _motionStrategy = s
  }

  val gravityForceVector = new BeadVector(MotionSeriesDefaults.gravityForceColor, "Gravity Force".literal, "force.abbrev.gravity".translate, false, () => gravityForce, (a, b) => b)
  val normalForceVector = new BeadVector(MotionSeriesDefaults.normalForceColor, "Normal Force".literal, "force.abbrev.normal".translate, true, () => normalForce, (a, b) => b)
  val totalForceVector = new BeadVector(MotionSeriesDefaults.totalForceColor, "Sum of Forces".literal, "force.abbrev.total".translate, false, () => totalForce, (a, b) => b)
  val appliedForceVector = new BeadVector(MotionSeriesDefaults.appliedForceColor, "Applied Force".literal, "force.abbrev.applied".translate, false, () => appliedForce, (a, b) => b)
  val frictionForceVector = new BeadVector(MotionSeriesDefaults.frictionForceColor, "Friction Force".literal, "force.abbrev.friction".translate, true, () => frictionForce, (a, b) => b)
  val wallForceVector = new BeadVector(MotionSeriesDefaults.wallForceColor, "Wall Force".literal, "force.abbrev.wall".translate, false, () => wallForce, (a, b) => b)
  val velocityVector = new BeadVector(MotionSeriesDefaults.velocityColor,"Velocity".literal,"velocity",false,()=>getRampUnitVector * velocity,(a, b) => b)//todo: translate
  val accelerationVector = new BeadVector(MotionSeriesDefaults.accelerationColor,"Acceleration".literal,"acceleration",false,()=>getRampUnitVector * acceleration,(a, b) => b)//todo: translate
  //chain listeners
  normalForceVector.addListenerByName(frictionForceVector.notifyListeners())
  //todo: add normalForceVector notification when changing friction coefficients

  appliedForceVector.addListenerByName(totalForceVector.notifyListeners())
  gravityForceVector.addListenerByName(totalForceVector.notifyListeners())
  normalForceVector.addListenerByName(totalForceVector.notifyListeners())
  frictionForceVector.addListenerByName(totalForceVector.notifyListeners())

  addListenerByName(appliedForceVector.notifyListeners()) //todo: just listen for changes to applied force parallel component
  model.addListenerByName(notifyListeners)
  model.addListenerByName(frictionForceVector.notifyListeners())

  //notified when the bead is being removed
  val removalListeners = new ArrayBuffer[() => Unit]

  def remove() = removalListeners.foreach(_())

  def width = _width

  def maxX = position + _width / 2

  def minX = position - _width / 2

  def attach() = motionStrategy = new Grounded(this)

  def totalForce = gravityForce + normalForce + appliedForce + frictionForce + wallForce

  def wallForce = motionStrategy.wallForce

  def frictionForce = motionStrategy.frictionForce

  def normalForce = motionStrategy.normalForce

  def gravityForce = new Vector2D(0, gravity * mass)

  def getVelocityVectorDirection: Double = getVelocityVectorDirection(velocity)

  def getVelocityVectorDirection(v: Double): Double = (positionMapper(position + v * 1E-6) - positionMapper(position - v * 1E-6)).getAngle

  def getVelocityVectorUnitVector: Vector2D = new Vector2D(getVelocityVectorDirection)

  def getVelocityVectorUnitVector(v: Double): Vector2D = new Vector2D(getVelocityVectorDirection(v))

  def parallelAppliedForce = _parallelAppliedForce

  val parallelAppliedForceListeners = new ArrayBuffer[() => Unit]

  def parallelAppliedForce_=(value: Double) = {
    _parallelAppliedForce = value
    parallelAppliedForceListeners.foreach(_())
    appliedForceVector.notifyListeners()
    notifyListeners()
  }

  def appliedForce = getRampUnitVector * _parallelAppliedForce

  def position2D = motionStrategy.position2D

  def getRampUnitVector = rampSegmentAccessor(position).getUnitVector

  def mass = state.mass

  def width_=(w: Double) = {
    _width = w
    notifyListeners()
  }

  private var _surfaceFrictionStrategy = __surfaceFrictionStrategy

  def surfaceFrictionStrategy = _surfaceFrictionStrategy

  def surfaceFrictionStrategy_=(x: SurfaceFrictionStrategy) = {
    _surfaceFrictionStrategy = x
    frictionForceVector.notifyListeners()
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

  def staticFriction = state.staticFriction

  def kineticFriction = state.kineticFriction

  def staticFriction_=(value: Double) = {
    state = state.setStaticFriction(value)
    frictionForceVector.notifyListeners()
    notifyListeners()
  }

  def kineticFriction_=(value: Double) = {
    state = state.setKineticFriction(value)
    frictionForceVector.notifyListeners()
    notifyListeners()
  }

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
      frictionForceVector.notifyListeners() //todo: maybe this could be omitted during batch updates for performance
      notifyListeners()
    }
  }

  def mass_=(mass: Double) = {
    state = state.setMass(mass)
    gravityForceVector.notifyListeners()
    normalForceVector.notifyListeners()
    notifyListeners()
  }

  def time = state.time

  def setTime(t: Double) = {
    state = state.setTime(t)
  }

  private var _desiredPosition = 0.0
  def desiredPosition = _desiredPosition
  //so we can use a filter
  def setDesiredPosition(position:Double) = {
    _desiredPosition = position
  }

  def setPosition(position: Double) = {
    if (position != state.position) {
      state = state.setPosition(position)
      //todo: maybe this could be omitted during batch updates for performance
      normalForceVector.notifyListeners() //since ramp segment or motion state might have changed; could improve performance on this by only sending notifications when we are sure the ramp segment has changed
      frictionForceVector.notifyListeners() //todo: omit this call since it's probably covered by the normal force call above
      wallForceVector.notifyListeners()
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

  def getAngle = motionStrategy.getAngle

  def forceToParallelAcceleration(f: Vector2D) = (f dot getRampUnitVector) / mass

  def getParallelComponent(f: Vector2D) = f dot getRampUnitVector

  def netForceToParallelVelocity(f: Vector2D, dt: Double) = velocity + forceToParallelAcceleration(f) * dt

  def acceleration = {
    if (mode == positionMode) {
      val timeData = for (i <- 0 until java.lang.Math.min(10, stateHistory.length))
      yield new TimeData(stateHistory(stateHistory.length - 1 - i).position, stateHistory(stateHistory.length - 1 - i).time)
      MotionMath.getSecondDerivative(timeData.toArray).getValue
      //      10.0
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
    motionStrategy.stepInTime(dt)
  }

  def averageVelocity = {
    val velocities = for (i <- 0 until java.lang.Math.min(10, stateHistory.length)) yield stateHistory(stateHistory.length - 1 - i).velocity
    val sum = velocities.foldLeft( 0.0)(_ + _)
//    println("velocities = "+velocities.toList)
    sum / velocities.size
  }
}