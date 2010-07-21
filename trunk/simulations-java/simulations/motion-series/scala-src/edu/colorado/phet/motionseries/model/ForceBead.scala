package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.motionseries.Predef._
import edu.colorado.phet.motionseries._
import java.lang.Math._

/**
 * This adds Force functionality to the Bead model class, such as ability to get force components and to set applied force.
 * It's used in the Forces & Motion and Ramp applications.
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
  val gravityForceVector = new BeadVector(MotionSeriesDefaults.gravityForceColor, "Gravity Force".literal, "force.abbrev.gravity".translate, false, () => gravityForce, (a, b) => b, PI / 2)
  val normalForceVector = new BeadVector(MotionSeriesDefaults.normalForceColor, "Normal Force".literal, "force.abbrev.normal".translate, true, () => normalForce, (a, b) => b, PI / 2)
  val totalForceVector = new BeadVector(MotionSeriesDefaults.totalForceColor, "Sum of Forces".literal, "force.abbrev.total".translate, false, () => totalForce, (a, b) => b, PI / 2)
  val appliedForceVector = new BeadVector(MotionSeriesDefaults.appliedForceColor, "Applied Force".literal, "force.abbrev.applied".translate, false, () => appliedForce, (a, b) => b, PI / 2)
  val frictionForceVector = new BeadVector(MotionSeriesDefaults.frictionForceColor, "Friction Force".literal, "force.abbrev.friction".translate, true, () => frictionForce, (a, b) => b, -PI / 2)
  val wallForceVector = new BeadVector(MotionSeriesDefaults.wallForceColor, "Wall Force".literal, "force.abbrev.wall".translate, false, () => wallForce, (a, b) => b, PI / 2)
  val velocityVector = new BeadVector(MotionSeriesDefaults.velocityColor, "Velocity".literal, "properties.velocity".translate, false, () => getRampUnitVector * velocity, (a, b) => b, PI / 2) //todo: translate
  val accelerationVector = new BeadVector(MotionSeriesDefaults.accelerationColor, "Acceleration".literal, "properties.acceleration".translate, false, () => getRampUnitVector * acceleration, (a, b) => b, PI / 2) //todo: translate
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
    if (value != _parallelAppliedForce) {
      _parallelAppliedForce = value
      parallelAppliedForceListeners.foreach(_())
      appliedForceVector.notifyListeners()
      notifyListeners()
    }
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

  //todo: switch to property based notifications so we don't have to remember to do this kind of fine-grained notification
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
    val originalPosition = super.position //have to record the position here since the next line changes the super.position
    super.setPosition(position)
    if (position != originalPosition) {
      //todo: maybe this could be omitted during batch updates for performance
      normalForceVector.notifyListeners() //since ramp segment or motion state might have changed; could improve performance on this by only sending notifications when we are sure the ramp segment has changed
      frictionForceVector.notifyListeners() //todo: omit this call since it's probably covered by the normal force call above
      wallForceVector.notifyListeners()
    } else {
      //      println("omitting setPosition notification, position was the same: "+position)
    }
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

  def position2D = motionStrategy.position2D

  def getAngle = motionStrategy.getAngle

  override def stepInTime(dt: Double) {
    super.stepInTime(dt)
    motionStrategy.stepInTime(dt)
  }

  def acceleration = forceToParallelAcceleration(totalForce)

  def isCrashed = motionStrategy.isCrashed
}