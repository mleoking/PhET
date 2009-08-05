package edu.colorado.phet.therampscala.model

import RampResources._
import collection.mutable.ArrayBuffer
import common.phetcommon.math.MathUtil
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import java.lang.Math._
import scalacommon.Predef._

/**Immutable memento for recording*/
case class BeadState(position: Double, velocity: Double, mass: Double, staticFriction: Double, kineticFriction: Double, thermalEnergy: Double) {
  def translate(dx: Double) = setPosition(position + dx)

  def setPosition(pos: Double) = new BeadState(pos, velocity, mass, staticFriction, kineticFriction, thermalEnergy)

  def setVelocity(vel: Double) = new BeadState(position, vel, mass, staticFriction, kineticFriction, thermalEnergy)

  def setStaticFriction(value: Double) = new BeadState(position, velocity, mass, value, kineticFriction, thermalEnergy)

  def setKineticFriction(value: Double) = new BeadState(position, velocity, mass, staticFriction, value, thermalEnergy)

  def setMass(m: Double) = new BeadState(position, velocity, m, staticFriction, kineticFriction, thermalEnergy)

  def setThermalEnergy(value: Double) = new BeadState(position, velocity, mass, staticFriction, kineticFriction, value)
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
           positionMapper: Double => Vector2D,
           rampSegmentAccessor: Double => RampSegment,
           model: Observable,
           surfaceFriction: () => Boolean,
           __surfaceFrictionStrategy: SurfaceFrictionStrategy,
           wallsExist: => Boolean,
           wallRange: () => Range)
        extends Observable {
  val id = Bead.nextIndex()
  val crashListeners = new ArrayBuffer[() => Unit]
  val gravity = -9.8

  def state = _state

  def state_=(s: BeadState) = {_state = s; notifyListeners()}

  var _parallelAppliedForce = 0.0
  //todo: privatize
  var attachState: MotionStrategy = new Grounded

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

  def attach() = attachState = new Grounded

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

  def parallelAppliedForce_=(value: Double) = {
    _parallelAppliedForce = value
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

  def thermalEnergy_=(value: Double) = {
    if (value != state.thermalEnergy) {
      state = state.setThermalEnergy(value)
      notifyListeners()
    }
  }

  def thermalEnergy = state.thermalEnergy

  def getThermalEnergy = state.thermalEnergy

  def getFrictiveWork = -getThermalEnergy

  def getGravityWork = -getPotentialEnergy

  def getWallWork = 0.0

  def getNormalWork = 0.0

  def getKineticEnergy = 1.0 / 2.0 * mass * velocity * velocity

  def getAngle = attachState.getAngle

  def forceToParallelAcceleration(f: Vector2D) = (f dot getRampUnitVector) / mass

  def netForceToParallelVelocity(f: Vector2D, dt: Double) = velocity + forceToParallelAcceleration(f) * dt

  abstract class MotionStrategy {
    def stepInTime(dt: Double)

    def wallForce: Vector2D

    def frictionForce: Vector2D

    def normalForce: Vector2D

    def position2D: Vector2D

    def getAngle: Double
  }

  class Crashed(_position2D: Vector2D, _angle: Double) extends MotionStrategy {
    def stepInTime(dt: Double) = {}

    def wallForce = new Vector2D

    def frictionForce = new Vector2D

    def normalForce = gravityForce * -1

    def position2D = _position2D

    def getAngle = _angle
  }
  class Airborne(private var _position2D: Vector2D, private var _velocity2D: Vector2D, _angle: Double) extends MotionStrategy {
    def getAngle = _angle
    def velocity2D = _velocity2D
    override def stepInTime(dt: Double) = {
      val tf = totalForce
      val accel = totalForce / mass
      _velocity2D = _velocity2D + accel * dt
      _position2D = _position2D + _velocity2D * dt
      if (_position2D.y <= _airborneFloor) {
        attachState = new Crashed(new Vector2D(_position2D.x, _airborneFloor), _angle)
        crashListeners.foreach(_())
      }
      normalForceVector.notifyListeners() //since ramp segment or motion state might have changed; could improve performance on this by only sending notifications when we are sure the ramp segment has changed
      notifyListeners() //to get the new normalforce
    }

    override def wallForce = new Vector2D

    override def frictionForce = new Vector2D

    override def normalForce = new Vector2D

    override def position2D = _position2D
  }
  class Grounded extends MotionStrategy {
    def position2D = positionMapper(position)

    def getAngle = rampSegmentAccessor(position).getUnitVector.getAngle

    def normalForce = {
      val magnitude = (gravityForce * -1) dot getRampUnitVector.rotate(PI / 2)
      val angle = getRampUnitVector.getAngle + PI / 2
      new Vector2D(angle) * magnitude
    }

    override def wallForce = {
      val leftBound = wallRange().min + width / 2
      val rightBound = wallRange().max - width / 2
      if (position <= leftBound && forceToParallelAcceleration(appliedForce) < 0 && wallsExist) appliedForce * -1
      else if (position >= rightBound && forceToParallelAcceleration(appliedForce) > 0 && wallsExist) appliedForce * -1//todo: account for gravity force
      else new Vector2D
    }

    def multiBodyFriction(f: Double) = _surfaceFrictionStrategy.getTotalFriction(f)

    override def frictionForce = {
      if (surfaceFriction()) {
        //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
        if (velocity.abs < 1E-12) {

          //use up to fMax in preventing the object from moving
          //see static friction discussion here: http://en.wikipedia.org/wiki/Friction
          val fMax = abs(multiBodyFriction(staticFriction) * normalForce.magnitude)
          val netForceWithoutFriction = appliedForce + gravityForce + normalForce + wallForce

          if (netForceWithoutFriction.magnitude >= fMax) new Vector2D(netForceWithoutFriction.getAngle + PI) * fMax
          else new Vector2D(netForceWithoutFriction.getAngle + PI) * netForceWithoutFriction.magnitude
        }
        else {
          //object is moving, just use kinetic friction
          val vel = (positionMapper(position) - positionMapper(position - velocity * 1E-6))
          new Vector2D(vel.getAngle + PI) * normalForce.magnitude * multiBodyFriction(kineticFriction)
        }
      }
      else new Vector2D
    }

    case class SettableState(position: Double, velocity: Double, thermalEnergy: Double) {
      def setPosition(p: Double) = new SettableState(p, velocity, thermalEnergy)

      def setVelocity(v: Double) = new SettableState(position, v, thermalEnergy)

      def setThermalEnergy(t: Double) = new SettableState(position, velocity, t)

      def setPositionAndVelocity(p: Double, v: Double) = new SettableState(p, v, thermalEnergy)

      //todo: this is duplicated with code in Bead
      lazy val totalEnergy = ke + pe + thermalEnergy
      lazy val ke = mass * velocity * velocity / 2.0
      lazy val pe = mass * gravity.abs * positionMapper(position).y //assumes positionmapper doesn't change, which is true during stepintime
    }
    override def stepInTime(dt: Double) = {
      notificationsEnabled = false //make sure only to send notifications as a batch at the end; improves performance by 17%
      val origEnergy = getTotalEnergy
      val origState = state
      val newState = getNewState(dt, origState, origEnergy)

      if (newState.position > wallRange().max + width / 2 && !wallsExist) {
        attachState = new Airborne(position2D, new Vector2D(getVelocityVectorDirection) * velocity, getAngle)
        parallelAppliedForce = 0
      }
      val distanceVector = positionMapper(newState.position) - positionMapper(origState.position)
      val work = appliedForce dot distanceVector
      workListeners.foreach(_(work))
      setPosition(newState.position)
      setVelocity(newState.velocity)
      thermalEnergy = newState.thermalEnergy

      notificationsEnabled = true;
      notifyListeners() //do as a batch, since it's a performance problem to do this several times in this method call
    }

    def getNewState(dt: Double, origState: BeadState, origEnergy: Double) = {
      val origVel = velocity
      val desiredVel = netForceToParallelVelocity(totalForce, dt)
      //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
      //see docs in static friction computation
      val newVelocity = if ((origVel < 0 && desiredVel > 0) || (origVel > 0 && desiredVel < 0)) 0.0 else desiredVel
      val requestedPosition = position + newVelocity * dt
      val stateAfterVelocityUpdate = new SettableState(requestedPosition, newVelocity, origState.thermalEnergy)

      val isKineticFriction = surfaceFriction() && kineticFriction > 0
      val leftBound = wallRange().min + width / 2
      val rightBound = wallRange().max - width / 2
      val collidedLeft = requestedPosition <= leftBound && wallsExist
      val collidedRight = requestedPosition >= rightBound && wallsExist
      val collided = collidedLeft || collidedRight
      val crashEnergy = stateAfterVelocityUpdate.ke

      val stateAfterCollision = if (collidedLeft && isKineticFriction) {
        new SettableState(leftBound, 0, stateAfterVelocityUpdate.thermalEnergy + crashEnergy)
      }
      else if (collidedRight && isKineticFriction) {
        new SettableState(rightBound, 0, stateAfterVelocityUpdate.thermalEnergy + crashEnergy)
      }
      else if (collided) { //bounce
        stateAfterVelocityUpdate.setVelocity(-newVelocity)
      }
      else {
        stateAfterVelocityUpdate
      }

      val dx = stateAfterCollision.position - origState.position

      //account for external forces, such as the applied force, which should increase the total energy
      val appliedEnergy = (appliedForce dot getVelocityVectorUnitVector(stateAfterCollision.velocity)) * dx.abs

      //      val thermalFromWork = getThermalEnergy + abs((frictionForce dot getVelocityVectorUnitVector(stateAfterBounds.velocity)) * dx) //work done by friction force, absolute value
      //todo: this may differ significantly from thermalFromWork
      val thermalFromEnergy = if (isKineticFriction && !collided)
        origEnergy - stateAfterCollision.ke - stateAfterCollision.pe + appliedEnergy
      else if (collided){
        //choose thermal energy so energy is exactly conserved
        origEnergy + appliedEnergy - stateAfterCollision.ke - stateAfterCollision.pe
      }
      else
        origState.thermalEnergy


      def getVelocityToConserveEnergy(state: SettableState) = {
        val sign = MathUtil.getSign(state.velocity)
        sign * sqrt(abs(2.0 / mass * (origEnergy + appliedEnergy - state.pe - origState.thermalEnergy)))
      }

      //we'd like to just use thermalFromEnergy, since it guarantees conservation of energy
      //however, it may lead to a decrease in thermal energy, which would be physically incorrect
      //      val stateAfterThermalEnergy = stateAfterBounds.setThermalEnergy(thermalFromWork)
      val stateAfterThermalEnergy = stateAfterCollision.setThermalEnergy(thermalFromEnergy)

      val dE = stateAfterThermalEnergy.totalEnergy - origEnergy
      val dT = stateAfterThermalEnergy.thermalEnergy - origState.thermalEnergy

      //drop in thermal energy indicates a problem, since total thermal energy should never decrease
      //preliminary tests indicate this happens when switching between ramp segment 0 and 1

      val stateAfterPatchup = if (dT < 0) {
        val patchedVelocity = getVelocityToConserveEnergy(stateAfterThermalEnergy)
        val patch = stateAfterThermalEnergy.setThermalEnergy(origState.thermalEnergy).setVelocity(patchedVelocity)
        val dEPatch = stateAfterThermalEnergy.totalEnergy - origEnergy
        if (dEPatch.abs > 1E-8) {
          println("applied energy = ".literal + appliedEnergy + ", dT = ".literal + dT + ", origVel=".literal + stateAfterThermalEnergy.velocity + ", newV=".literal + patchedVelocity + ", dE=".literal + dEPatch)
          //accept some problem here
          //todo: should the state be changed, given that energy is problematic?
          patch
        } else
          patch
      } else {
        stateAfterThermalEnergy
      }

      val finalState = if (abs(stateAfterPatchup.totalEnergy - origEnergy - appliedEnergy) > 1E-8 && stateAfterPatchup.velocity.abs > 1E-3) {
        stateAfterPatchup.setVelocity(getVelocityToConserveEnergy(stateAfterThermalEnergy))
      } else {
        stateAfterPatchup
      }

      val patchPosition = if (abs(finalState.totalEnergy - origEnergy) > 1E-8 && getAngle != 0.0) {
        val x = (origEnergy + appliedEnergy - finalState.thermalEnergy - finalState.ke) / mass / gravity.abs / sin(getAngle)
        stateAfterPatchup.setPosition(x)
      } else {
        finalState
      }

      val delta = patchPosition.totalEnergy - origEnergy - appliedEnergy
      if (delta.abs> 1E-8) {
        println("failed to conserve energy, delta=".literal + delta)
      }

      patchPosition
    }
  }
  val workListeners = new ArrayBuffer[Double => Unit]

  private var _notificationsEnabled = true

  private def notificationsEnabled = _notificationsEnabled

  private def notificationsEnabled_=(b: Boolean) = _notificationsEnabled = b
  //allow global disabling of notifications since they are very expensive and called many times during Grounded.stepInTime
  override def notifyListeners() = {
    if (notificationsEnabled) {
      super.notifyListeners()
    }
  }

  def stepInTime(dt: Double) = attachState.stepInTime(dt)
}