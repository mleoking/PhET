package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import common.motion.model.TimeData
import common.motion.MotionMath
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import edu.colorado.phet.motionseries.Predef._

/**
 * This adds Force functionality to the Bead model class, such as ability to get force components and to set applied force.
 * It's used in the "Forces & Motion" and "Ramp" applications.
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

  def position2D = motionStrategy.position2D

  def getAngle = motionStrategy.getAngle

  override def stepInTime(dt: Double) = {
    super.stepInTime(dt)
    motionStrategy.stepInTime(dt)
  }
  def acceleration = forceToParallelAcceleration(totalForce)
}

//TODO: change from:
//MovingManBead extends ForceBead extends Bead
//to
//MovingManBead extends Bead
//ForceBead extends Bead
/**
 *
    if (bead.mode == bead.velocityMode) {
      //compute acceleration as derivative of velocity
      val acceleration = (bead.velocity - origState.velocity) / dt
      bead.parallelAppliedForce = acceleration * bead.mass
    } else if (bead.mode == bead.positionMode) {
      //      println("position = " + bead.position + ", desired = " + bead.desiredPosition)
      //      bead.setPosition((bead.desiredPosition + bead.position) / 2) //attempt at filtering
      val mixingFactor = 0.5
      //maybe a better assumption is constant velocity or constant acceleration ?
      val dst = bead.desiredPosition * mixingFactor + bead.position * (1 - mixingFactor)
      bead.setPosition(dst) //attempt at filtering

      //todo: move closer to bead computation of acceleration derivatives
      val timeData = for (i <- 0 until java.lang.Math.min(15, bead.stateHistory.length))
      yield new TimeData(bead.stateHistory(bead.stateHistory.length - 1 - i).position, bead.stateHistory(bead.stateHistory.length - 1 - i).time)
      val vel = MotionMath.estimateDerivative(timeData.toArray)
      bead.setVelocity(vel)
    }
 */
class MovingManBead(_state: BeadState,
                    _height: Double,
                    _width: Double,
                    positionMapper: Double => Vector2D,
                    rampSegmentAccessor: Double => RampSegment,
                    model: Observable,
                    surfaceFriction: () => Boolean,
                    wallsBounce: () => Boolean,
                    __surfaceFrictionStrategy: SurfaceFrictionStrategy,
                    _wallsExist: => Boolean,
                    wallRange: () => Range,
                    thermalEnergyStrategy: Double => Double)
        extends ForceBead(_state, _height, _width, positionMapper, rampSegmentAccessor, model, surfaceFriction,wallsBounce, __surfaceFrictionStrategy,_wallsExist, wallRange, thermalEnergyStrategy) {

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
//    motionStrategy  = new MotionStrategy(this){
//      def getFactory = null
//
//      override def wallForce = null
//
//      def stepInTime(dt: Double) = null
//
//      def position2D = null
//
//      def getAngle = 0.0
//    }
  }
  override def acceleration = {
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
        super.acceleration
      }
  }  
}