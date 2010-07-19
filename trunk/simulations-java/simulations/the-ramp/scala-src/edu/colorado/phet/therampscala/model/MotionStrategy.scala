package edu.colorado.phet.therampscala.model

import common.phetcommon.math.MathUtil
import scalacommon.math.Vector2D
import java.lang.Math._
import therampscala.Predef._

abstract class MotionStrategy(val bead: Bead) {
  def stepInTime(dt: Double)

  def wallForce: Vector2D

  def frictionForce: Vector2D

  def normalForce: Vector2D

  def position2D: Vector2D

  def getAngle: Double

  //accessors/adapters for subclass convenience
  //This class was originally designed to be an inner class of Bead, but IntelliJ debugger didn't support debug into inner classes at the time
  //so these classes were refactored to be top level classes to enable debugging.  They can be refactored back to inner classes when there is better debug support

  def totalForce = bead.totalForce

  def gravityForce = bead.gravityForce

  def mass = bead.mass

  def normalForceVector = bead.normalForceVector

  def positionMapper = bead.positionMapper

  def rampSegmentAccessor = bead.rampSegmentAccessor

  def wallRange = bead.wallRange()

  def position = bead.position

  def appliedForce = bead.appliedForce

  def gravity = bead.gravity

  def notificationsEnabled = bead.notificationsEnabled

  def getTotalEnergy = bead.getTotalEnergy

  def state = bead.state

  def velocity = bead.velocity

  def workListeners = bead.workListeners

  def surfaceFriction = bead.surfaceFriction

  def thermalEnergy = bead.thermalEnergy

  def airborneFloor = bead.airborneFloor

  def crashListeners = bead.crashListeners

  def getRampUnitVector = bead.getRampUnitVector

  def staticFriction = bead.staticFriction

  def kineticFriction = bead.kineticFriction

  def width = bead.width

  def wallsExist = bead.wallsExist

  def getVelocityVectorDirection = bead.getVelocityVectorDirection
}

class Crashed(_position2D: Vector2D, _angle: Double, bead: Bead) extends MotionStrategy(bead) {
  def stepInTime(dt: Double) = {}

  def wallForce = new Vector2D

  def frictionForce = new Vector2D

  def normalForce = gravityForce * -1

  def position2D = _position2D

  def getAngle = _angle
}

class Airborne(private var _position2D: Vector2D, private var _velocity2D: Vector2D, _angle: Double, bead: Bead) extends MotionStrategy(bead: Bead) {
  def getAngle = _angle

  def velocity2D = _velocity2D

  override def stepInTime(dt: Double) = {
    val tf = totalForce
    val accel = totalForce / mass
    _velocity2D = _velocity2D + accel * dt
    _position2D = _position2D + _velocity2D * dt
    if (_position2D.y <= airborneFloor) {
      bead.attachState = new Crashed(new Vector2D(_position2D.x, bead.airborneFloor), _angle, bead)
      crashListeners.foreach(_())
    }
    normalForceVector.notifyListeners() //since ramp segment or motion state might have changed; could improve performance on this by only sending notifications when we are sure the ramp segment has changed
    bead.notifyListeners() //to get the new normalforce
  }

  override def wallForce = new Vector2D

  override def frictionForce = new Vector2D

  override def normalForce = new Vector2D

  override def position2D = _position2D
}

class Grounded(bead: Bead) extends MotionStrategy(bead) {
  def position2D = positionMapper(position)

  def getAngle = rampSegmentAccessor(position).getUnitVector.getAngle

  def normalForce = {
    val magnitude = (gravityForce * -1) dot getRampUnitVector.rotate(PI / 2)
    val angle = getRampUnitVector.getAngle + PI / 2
    new Vector2D(angle) * magnitude
  }

  //is the block about to collide?
  def collideLeft = position + velocity * dt < leftBound && wallsExist

  def collideRight = position + velocity * dt > rightBound && wallsExist

  def collide = collideLeft || collideRight

  def leftBound = bead.wallRange().min + width / 2

  def rightBound = bead.wallRange().max - width / 2

  override def wallForce = {
    val netForceWithoutWallForce = appliedForce + gravityForce + normalForce //+ frictionForce //todo: net force without wall force should include friction force, but creates a loop (in which friction force depends on wall force and vice versa
    val pressingLeft = position <= leftBound && bead.forceToParallelAcceleration(netForceWithoutWallForce) < 0 && wallsExist
    val pressingRight = position >= rightBound && bead.forceToParallelAcceleration(netForceWithoutWallForce) > 0 && wallsExist
    val pressing = pressingLeft || pressingRight

    if (pressing)
      netForceWithoutWallForce * -1
    else if (collide) {
      val finalVelocity = if (bounce) -velocity else 0.0
      val deltaV = finalVelocity - velocity
      //Fnet = m dv/dt = Fw + F_{-w}
      val sign = if (collideRight) -1.0 else +1.0
      val wallCollisionForce = abs(mass * deltaV / dt - netForceWithoutWallForce.magnitude) * sign

      val resultWallForce = getRampUnitVector * wallCollisionForce
      //      println("proposedPosition = " + proposedPosition + ", wall collision force = " + wallCollisionForce + ", wall force = " + resultWallForce)
      resultWallForce
    } else new Vector2D
  }

  def multiBodyFriction(f: Double) = bead.surfaceFrictionStrategy.getTotalFriction(f)

  override def frictionForce = {
    if (surfaceFriction()) {
      //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
      if (velocity.abs < 1E-12) {

        //use up to fMax in preventing the object from moving
        //see static friction discussion here: http://en.wikipedia.org/wiki/Friction
        val fMax = abs(multiBodyFriction(staticFriction) * normalForce.magnitude)
        val netForceWithoutFriction = appliedForce + gravityForce + normalForce + wallForce

        val magnitude = if (netForceWithoutFriction.magnitude >= fMax) fMax else netForceWithoutFriction.magnitude
        new Vector2D(netForceWithoutFriction.getAngle + PI) * magnitude
      }
      else {
        //object is moving, just use kinetic friction
        val vel = (positionMapper(position) - positionMapper(position - velocity * 1E-6))
        new Vector2D(vel.getAngle + PI) * normalForce.magnitude * multiBodyFriction(kineticFriction)
      }
    }
    else new Vector2D
  }

  case class SettableState(position: Double, velocity: Double, thermalEnergy: Double, crashEnergy: Double) {
    def setPosition(p: Double) = new SettableState(p, velocity, thermalEnergy, crashEnergy)

    def setVelocity(v: Double) = new SettableState(position, v, thermalEnergy, crashEnergy)

    def setThermalEnergy(t: Double) = new SettableState(position, velocity, t, crashEnergy)

    def setPositionAndVelocity(p: Double, v: Double) = new SettableState(p, v, thermalEnergy, crashEnergy)

    //todo: this is duplicated with code in Bead
    lazy val totalEnergy = ke + pe + thermalEnergy
    lazy val ke = mass * velocity * velocity / 2.0
    lazy val pe = mass * gravity.abs * positionMapper(position).y //assumes positionmapper doesn't change, which is true during stepintime
  }

  var dt = 1.0 / 30.0 //using dummy value before getting actual value in case any computations are done
  override def stepInTime(dt: Double) = {
    this.dt = dt
    bead.notificationsEnabled = false //make sure only to send notifications as a batch at the end; improves performance by 17%
    val origEnergy = getTotalEnergy
    val origState = state
    val newState = getNewState(dt, origState, origEnergy)

    if (newState.position > bead.wallRange().max + width / 2 && !wallsExist) {
      bead.attachState = new Airborne(position2D, new Vector2D(getVelocityVectorDirection) * velocity, getAngle, bead)
      bead.parallelAppliedForce = 0
    }
    val distanceVector = positionMapper(newState.position) - positionMapper(origState.position)
    val work = appliedForce dot distanceVector
    workListeners.foreach(_(work))
    bead.setPosition(newState.position)
    bead.setVelocity(newState.velocity)
    bead.thermalEnergy = newState.thermalEnergy
    bead.setCrashEnergy(newState.crashEnergy)

    bead.notificationsEnabled = true;
    bead.notifyListeners() //do as a batch, since it's a performance problem to do this several times in this method call
  }

  def bounce = !isKineticFriction

  def isKineticFriction = surfaceFriction() && kineticFriction > 0

  def getNewState(dt: Double, origState: BeadState, origEnergy: Double) = {
    val desiredVel = bead.netForceToParallelVelocity(totalForce, dt)
    //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
    //see docs in static friction computation
    val newVelocityThatGoesThroughZero = if ((velocity < 0 && desiredVel > 0) || (velocity > 0 && desiredVel < 0)) 0.0 else desiredVel
    //make sure velocity is exactly zero or opposite after wall collision
    val newVelocity = if (collide && bounce) -velocity else if (collide) 0.0 else newVelocityThatGoesThroughZero
    val stateAfterVelocityUpdate = new SettableState(position + newVelocity * dt, newVelocity, origState.thermalEnergy, origState.crashEnergy)

    val crashEnergy = 0.5 * mass * velocity * velocity //this is the energy it would lose in a crash
    val stateAfterCollision = if (collideLeft && !bounce) {
      new SettableState(leftBound, 0, stateAfterVelocityUpdate.thermalEnergy + crashEnergy, origState.crashEnergy + crashEnergy)
    }
    else if (collideRight && !bounce) {
      new SettableState(rightBound, 0, stateAfterVelocityUpdate.thermalEnergy + crashEnergy, origState.crashEnergy + crashEnergy)
    }
    else {
      stateAfterVelocityUpdate
    }

    val dx = stateAfterCollision.position - origState.position

    //account for external forces, such as the applied force, which should increase the total energy
    val appliedEnergy = (appliedForce dot bead.getVelocityVectorUnitVector(stateAfterCollision.velocity)) * dx.abs

    //      val thermalFromWork = getThermalEnergy + abs((frictionForce dot getVelocityVectorUnitVector(stateAfterBounds.velocity)) * dx) //work done by friction force, absolute value
    //todo: this may differ significantly from thermalFromWork
    val thermalFromEnergy = if (isKineticFriction && !collide)
      origEnergy - stateAfterCollision.ke - stateAfterCollision.pe + appliedEnergy
    else if (isKineticFriction && collide) {
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
    val stateAfterFixingThermal = if (dT < 0) {
      val patchedVelocity = getVelocityToConserveEnergy(stateAfterThermalEnergy)
      val patch = stateAfterThermalEnergy.setThermalEnergy(origState.thermalEnergy).setVelocity(patchedVelocity)
      val dEPatch = stateAfterThermalEnergy.totalEnergy - origEnergy
      if (dEPatch.abs > 1E-8) {
        println("applied energy = ".literal + appliedEnergy + ", dT = ".literal + dT + ", velocity=".literal + stateAfterThermalEnergy.velocity + ", newV=".literal + patchedVelocity + ", dE=".literal + dEPatch)
        //accept some problem here
        //todo: should the state be changed, given that energy is problematic?
        patch
      } else
        patch
    } else {
      stateAfterThermalEnergy
    }

    val stateAfterFixingVelocity = if (abs(stateAfterFixingThermal.totalEnergy - origEnergy - appliedEnergy) > 1E-8 && stateAfterFixingThermal.velocity.abs > 1E-3) {
      stateAfterFixingThermal.setVelocity(getVelocityToConserveEnergy(stateAfterThermalEnergy))
    } else {
      stateAfterFixingThermal
    }

    val stateAfterFixingPosition = if (abs(stateAfterFixingVelocity.totalEnergy - origEnergy) > 1E-8 && getAngle != 0.0) {
      val x = (origEnergy + appliedEnergy - stateAfterFixingVelocity.thermalEnergy - stateAfterFixingVelocity.ke) / mass / gravity.abs / sin(getAngle)
      stateAfterFixingThermal.setPosition(x)
    } else {
      stateAfterFixingVelocity
    }

    val delta = stateAfterFixingPosition.totalEnergy - origEnergy - appliedEnergy
    if (delta.abs > 1E-6 && appliedEnergy.abs < 1E-4) { //assume applied energy could absorb some error
      println("failed to conserve energy, delta=".literal + delta + ", applied energy = " + appliedEnergy)
    }

    stateAfterFixingPosition
  }
}