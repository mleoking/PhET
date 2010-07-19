package edu.colorado.phet.therampscala.model

import RampResources._
import collection.mutable.ArrayBuffer
import common.phetcommon.math.MathUtil
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import java.lang.Math._
/**Immutable memento for recording*/
case class BeadState(position: Double, velocity: Double, mass: Double, staticFriction: Double, kineticFriction: Double, thermalEnergy: Double, crashEnergy: Double) {
  def translate(dx: Double) = setPosition(position + dx)

  def setPosition(pos: Double) = new BeadState(pos, velocity, mass, staticFriction, kineticFriction, thermalEnergy, crashEnergy)

  def setVelocity(vel: Double) = new BeadState(position, vel, mass, staticFriction, kineticFriction, thermalEnergy, crashEnergy)

  def setStaticFriction(value: Double) = new BeadState(position, velocity, mass, value, kineticFriction, thermalEnergy, crashEnergy)

  def setKineticFriction(value: Double) = new BeadState(position, velocity, mass, staticFriction, value, thermalEnergy, crashEnergy)

  def setMass(m: Double) = new BeadState(position, velocity, m, staticFriction, kineticFriction, thermalEnergy, crashEnergy)

  def setThermalEnergy(value: Double) = new BeadState(position, velocity, mass, staticFriction, kineticFriction, value, crashEnergy)

  def setCrashEnergy(value: Double) = new BeadState(position, velocity, mass, staticFriction, kineticFriction, thermalEnergy, value)
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
  val gravity = -9.8

  def state = _state

  def state_=(s: BeadState) = {_state = s; notifyListeners()}

  var _parallelAppliedForce = 0.0
  //todo: privatize
  var attachState: MotionStrategy = new Grounded(this)

  val gravityForceVector = new BeadVector(RampDefaults.gravityForceColor, "Gravity Force".literal, "force.abbrev.gravity".translate, false, () => gravityForce, (a, b) => b)
  val normalForceVector = new BeadVector(RampDefaults.normalForceColor, "Normal Force".literal, "force.abbrev.normal".translate, true, () => normalForce, (a, b) => b)
  val totalForceVector = new BeadVector(RampDefaults.totalForceColor, "Sum of Forces".literal, "force.abbrev.total".translate, false, () => totalForce, (a, b) => b)
  val appliedForceVector = new BeadVector(RampDefaults.appliedForceColor, "Applied Force".literal, "force.abbrev.applied".translate, false, () => appliedForce, (a, b) => b)
  val frictionForceVector = new BeadVector(RampDefaults.frictionForceColor, "Friction Force".literal, "force.abbrev.friction".translate, true, () => frictionForce, (a, b) => b)
  val wallForceVector = new BeadVector(RampDefaults.wallForceColor, "Wall Force".literal, "force.abbrev.wall".translate, false, () => wallForce, (a, b) => b)
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

  def attach() = attachState = new Grounded(this)

  def totalForce = gravityForce + normalForce + appliedForce + frictionForce + wallForce

  def wallForce = attachState.wallForce

  def frictionForce = attachState.frictionForce

  def normalForce = attachState.normalForce

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

  def position2D = attachState.position2D

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
    notifyListeners()
  }

  def kineticFriction_=(value: Double) = {
    state = state.setKineticFriction(value)
    notifyListeners()
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
    notifyListeners()
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

  def getAngle = attachState.getAngle

  def forceToParallelAcceleration(f: Vector2D) = (f dot getRampUnitVector) / mass

  def netForceToParallelVelocity(f: Vector2D, dt: Double) = velocity + forceToParallelAcceleration(f) * dt

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

  def stepInTime(dt: Double) = attachState.stepInTime(dt)
}