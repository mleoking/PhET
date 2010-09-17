package edu.colorado.phet.motionseries.model

import edu.colorado.phet.common.phetcommon.math.MathUtil
import edu.colorado.phet.scalacommon.math.Vector2D
import java.lang.Math._
import edu.colorado.phet.motionseries.Predef._

//Used to save/restore motion strategies during record/playback
trait MotionStrategyMemento {
  def getMotionStrategy(motionSeriesObject: MotionSeriesObject): MotionStrategy
}

abstract class MotionStrategy(val motionSeriesObject: MotionSeriesObject) {
  def updateAppliedForce() {
    motionSeriesObject.appliedForce.value = motionSeriesObject.rampUnitVector * motionSeriesObject.parallelAppliedForce
  }

  def updateForces() {
    updateAppliedForce() //TODO: this call is duplicated in stepintime
    motionSeriesObject.wallForce.value = wallForce
    motionSeriesObject.frictionForce.value = frictionForce(true)
    motionSeriesObject.normalForce.value = normalForce
    motionSeriesObject.gravityForce.value = motionSeriesObject.gravityForce.value
    motionSeriesObject.totalForce.value = motionSeriesObject.gravityForce.value + motionSeriesObject.normalForce.value +
            motionSeriesObject.appliedForce.value + motionSeriesObject.frictionForce.value + motionSeriesObject.wallForce.value
  }

  def isCrashed: Boolean

  def stepInTime(dt: Double)

  def getAngle: Double

  def getMemento: MotionStrategyMemento

  def wallForce = new Vector2D

  //This method has to include a parameter for whether the wall force should be included to avoid an infinite recursive loop
  //in computing the wall force
  def frictionForce(includeWallForce: Boolean): Vector2D = new Vector2D

  def normalForce = new Vector2D

  def mapPosition = motionSeriesObject.positionMapper(motionSeriesObject.position)
}

//This Crashed state indicates that the object has fallen off the ramp or off a cliff, not that it has crashed into a wall.
class Crashed(_position2D: Vector2D, _angle: Double, motionSeriesObject: MotionSeriesObject) extends MotionStrategy(motionSeriesObject) {
  def isCrashed = true

  def stepInTime(dt: Double) = {
    updateForces()
  }

  override def normalForce = motionSeriesObject.gravityForce.value * -1

  def getAngle = _angle

  def getMemento = {
    new MotionStrategyMemento {
      def getMotionStrategy(motionSeriesObject: MotionSeriesObject) = new Crashed(_position2D, getAngle, motionSeriesObject)
    }
  }

  override def mapPosition = _position2D
}

class Airborne(private var _position2D: Vector2D,
               private var _velocity2D: Vector2D,
               _angle: Double,
               motionSeriesObject: MotionSeriesObject) extends MotionStrategy(motionSeriesObject: MotionSeriesObject) {
  def isCrashed = false

  def getAngle = _angle

  def velocity2D = _velocity2D

  override def stepInTime(dt: Double) = {
    val originalEnergy = motionSeriesObject.getTotalEnergy
    updateForces()
    val accel = motionSeriesObject.totalForce.value / motionSeriesObject.mass
    _velocity2D = _velocity2D + accel * dt
    _position2D = _position2D + _velocity2D * dt
    motionSeriesObject.time = motionSeriesObject.time + dt
    if (_position2D.y <= motionSeriesObject.airborneFloor) { //Crashed
      motionSeriesObject.motionStrategy = new Crashed(new Vector2D(_position2D.x, motionSeriesObject.airborneFloor), _angle, motionSeriesObject)
      motionSeriesObject.crashListeners.foreach(_())
      //todo: make sure energy conserved on crash
      val newEnergy = motionSeriesObject.getTotalEnergy
      val energyDifference = motionSeriesObject.getTotalEnergy - originalEnergy
      //      println("Energy difference on crash: "+energyDifference)
      if (motionSeriesObject.getTotalEnergy < originalEnergy) {
        //the rest is lost to heat
        motionSeriesObject.thermalEnergy = motionSeriesObject.thermalEnergy + energyDifference.abs
      }
      else if (motionSeriesObject.getTotalEnergy >= originalEnergy) {
        //todo: what to do here?  does this ever happen?
        println("energy gained on crash")
      }
    } else { //Flying through the air
      motionSeriesObject.velocity = _velocity2D.magnitude

      //ensure that energy is exactly conserved by fine-tuning the vertical position of the object; note that this wouldn't work in low g
      val dy = (motionSeriesObject.getTotalEnergy - originalEnergy) / motionSeriesObject.mass / motionSeriesObject.gravity
      _position2D = new Vector2D(_position2D.x, _position2D.y + dy)
    }
    motionSeriesObject._position2D.value = _position2D
  }

  def getMemento = new AirborneMemento(_position2D, velocity2D, getAngle)
}

class AirborneMemento(p: Vector2D, v: Vector2D, a: Double) extends MotionStrategyMemento {
  def getMotionStrategy(motionSeriesObject: MotionSeriesObject) = new Airborne(p, v, a, motionSeriesObject)

  override def toString = "airborn motion strategy mode, p = ".literal + p
}

class Grounded(motionSeriesObject: MotionSeriesObject) extends MotionStrategy(motionSeriesObject) {
  def isCrashed = false

  def getMemento = {
    new MotionStrategyMemento {
      def getMotionStrategy(motionSeriesObject: MotionSeriesObject) = new Grounded(motionSeriesObject)
    }
  }

  def position2D = motionSeriesObject.positionMapper(motionSeriesObject.position)

  def getAngle = motionSeriesObject.rampSegmentAccessor(motionSeriesObject.position).unitVector.angle

  override def normalForce = {
    val magnitude = (motionSeriesObject.gravityForce.value * -1) dot motionSeriesObject.rampUnitVector.rotate(PI / 2)
    val angle = motionSeriesObject.rampUnitVector.angle + PI / 2
    new Vector2D(angle) * magnitude
  }

  //is the block about to collide?
  def collideLeft = motionSeriesObject.position + motionSeriesObject.velocity * dt < leftBound && motionSeriesObject.wallsExist

  def collideRight = motionSeriesObject.position + motionSeriesObject.velocity * dt > rightBound && motionSeriesObject.wallsExist

  def collide = collideLeft || collideRight

  def leftBound = motionSeriesObject.wallRange().min + motionSeriesObject.width / 2

  def rightBound = motionSeriesObject.wallRange().max - motionSeriesObject.width / 2

  //TODO: computing this lazily is very performance intensive
  override def wallForce = {

    val epsilon = 1E-4 //this is a physics workaround to help reduce or resolve the flickering problem by enabling the 'pressing' wall force a bit sooner

    //Friction force should enter into the system before wall force, since the object would have to move before "communicating" with the wall.
    val netForceWithoutWallForce = motionSeriesObject.appliedForce.value + motionSeriesObject.gravityForce.value + normalForce + frictionForce(false)
    val pressingLeft = motionSeriesObject.position <= leftBound + epsilon && motionSeriesObject.forceToParallelAcceleration(netForceWithoutWallForce) < 0 && motionSeriesObject.wallsExist
    val pressingRight = motionSeriesObject.position >= rightBound - epsilon && motionSeriesObject.forceToParallelAcceleration(netForceWithoutWallForce) > 0 && motionSeriesObject.wallsExist
    val pressing = pressingLeft || pressingRight

    if (pressing)
      netForceWithoutWallForce * -1
    else if (collide) {
      //create a large instantaneous impulse force during a collision
      val finalVelocity = if (bounce) -motionSeriesObject.velocity else 0.0
      val deltaV = finalVelocity - motionSeriesObject.velocity
      //Fnet = m dv/dt = Fw + F_{-w}
      val sign = if (collideRight) -1.0 else +1.0
      val wallCollisionForce = abs(motionSeriesObject.mass * deltaV / dt - netForceWithoutWallForce.magnitude) * sign

      val resultWallForce = motionSeriesObject.rampUnitVector * wallCollisionForce
      resultWallForce
    } else new Vector2D
  }

  def multiBodyFriction(f: Double) = motionSeriesObject.surfaceFrictionStrategy.getTotalFriction(f)

  //see super notes regarding the includeWallForce
  override def frictionForce(includeWallForce: Boolean) = {
    if (motionSeriesObject.surfaceFriction()) {
      //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
      if (motionSeriesObject.velocity.abs < 1E-12) {

        //use up to fMax in preventing the object from moving
        //see static friction discussion here: http://en.wikipedia.org/wiki/Friction
        val fMax = abs(multiBodyFriction(motionSeriesObject.staticFriction) * normalForce.magnitude)
        val netForceWithoutFriction = motionSeriesObject.appliedForce.value + motionSeriesObject.gravityForce.value + normalForce + (if (includeWallForce) wallForce else new Vector2D())

        val magnitude = if (netForceWithoutFriction.magnitude >= fMax) fMax else netForceWithoutFriction.magnitude
        new Vector2D(netForceWithoutFriction.angle + PI) * magnitude
      }
      else {
        //object is moving, just use kinetic friction
        val vel = (motionSeriesObject.positionMapper(motionSeriesObject.position) - motionSeriesObject.positionMapper(motionSeriesObject.position - motionSeriesObject.velocity * 1E-6))
        new Vector2D(vel.angle + PI) * normalForce.magnitude * multiBodyFriction(motionSeriesObject.kineticFriction)
      }
    }
    else new Vector2D
  }

  case class SettableState(position: Double, velocity: Double, thermalEnergy: Double, crashEnergy: Double) {
    //todo: this is duplicated with code in MotionSeriesObject
    lazy val totalEnergy = ke + pe + thermalEnergy
    lazy val ke = motionSeriesObject.mass * velocity * velocity / 2.0
    lazy val pe = motionSeriesObject.mass * motionSeriesObject.gravity.abs * motionSeriesObject.positionMapper(position).y //assumes positionmapper doesn't change, which is true during stepintime

    def setPosition(p: Double) = copy(position = p)

    def setVelocity(v: Double) = copy(velocity = v)

    def setThermalEnergy(t: Double) = copy(thermalEnergy = t)

    def setPositionAndVelocity(p: Double, v: Double) = copy(position = p, velocity = v)
  }

  private var dt = 1.0 / 30.0 //using dummy value before getting actual value in case any computations are done

  override def stepInTime(dt: Double) = {
    this.dt = dt
    updateAppliedForce()
    val origEnergy = motionSeriesObject.getTotalEnergy
    val origState = motionSeriesObject.state
    val newState = getNewState(dt, origState, origEnergy)

    if (newState.position > motionSeriesObject.wallRange().max + motionSeriesObject.width / 2 && !motionSeriesObject.wallsExist) {
      motionSeriesObject.motionStrategy = new Airborne(position2D, new Vector2D(motionSeriesObject.getVelocityVectorDirection) * motionSeriesObject.velocity, getAngle, motionSeriesObject)
      motionSeriesObject.parallelAppliedForce = 0
    }
    val distanceVector = motionSeriesObject.positionMapper(newState.position) - motionSeriesObject.positionMapper(origState.position)
    val work = motionSeriesObject.appliedForce.value dot distanceVector
    motionSeriesObject.workListeners.foreach(_(work))
    motionSeriesObject.time = motionSeriesObject.time + dt
    motionSeriesObject.position = newState.position
    motionSeriesObject.velocity = newState.velocity
    motionSeriesObject.thermalEnergy = newState.thermalEnergy
    motionSeriesObject.crashEnergy = newState.crashEnergy

    updateForces()
  }

  def bounce = motionSeriesObject.wallsBounce.booleanValue

  def isKineticFriction = motionSeriesObject.surfaceFriction() && motionSeriesObject.kineticFriction > 0

  def getNewState(dt: Double, origState: MotionSeriesObjectState, origEnergy: Double) = {
    val newVelocity = {
      val desiredVel = motionSeriesObject.netForceToParallelVelocity(motionSeriesObject.totalForce.value, dt)
      //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
      //see docs in static friction computation
      val newVelocityThatGoesThroughZero = if ((motionSeriesObject.velocity < 0 && desiredVel > 0) || (motionSeriesObject.velocity > 0 && desiredVel < 0)) 0.0 else desiredVel
      //make sure velocity is exactly zero or opposite after wall collision
      if (collide && bounce) -motionSeriesObject.velocity else if (collide) 0.0 else newVelocityThatGoesThroughZero
    }

    val stateAfterVelocityUpdate = new SettableState(motionSeriesObject.position + newVelocity * dt, newVelocity, origState.thermalEnergy, origState.crashEnergy)

    val crashEnergy = 0.5 * motionSeriesObject.mass * motionSeriesObject.velocity * motionSeriesObject.velocity //this is the energy it would lose in a crash
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
    val appliedEnergy = (motionSeriesObject.appliedForce.value dot motionSeriesObject.getVelocityVectorUnitVector(stateAfterCollision.velocity)) * dx.abs

    //      val thermalFromWork = getThermalEnergy + abs((frictionForce dot getVelocityVectorUnitVector(stateAfterBounds.velocity)) * dx) //work done by friction force, absolute value.  Leaving this line here (commented out) in case this code is reused for WorkEnergy sim 
    //todo: this may differ significantly from thermalFromWork
    val thermalFromEnergy = if (isKineticFriction && !collide)
      origEnergy - stateAfterCollision.ke - stateAfterCollision.pe + appliedEnergy
    else if (!bounce && collide) {
      //choose thermal energy so energy is exactly conserved
      origEnergy + appliedEnergy - stateAfterCollision.ke - stateAfterCollision.pe
    }
    else
      origState.thermalEnergy

    def getVelocityToConserveEnergy(state: SettableState) = {
      val sign = MathUtil.getSign(state.velocity)
      sign * sqrt(abs(2.0 / motionSeriesObject.mass * (origEnergy + appliedEnergy - state.pe - origState.thermalEnergy)))
    }

    //we'd like to just use thermalFromEnergy, since it guarantees conservation of energy
    //however, it may lead to a decrease in thermal energy, which would be physically incorrect
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
        //        println("applied energy = ".literal + appliedEnergy + ", dT = ".literal + dT + ", velocity=".literal + stateAfterThermalEnergy.velocity + ", newV=".literal + patchedVelocity + ", dE=".literal + dEPatch)
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

    val stateAfterFixingPosition = if (abs(stateAfterFixingVelocity.totalEnergy - origEnergy) > 1E-8 && getAngle > 1E-8) { //todo: angle is greater than 1E-8 instead of 0 for compatibility with workaround for forces and motion game tab, see ForcesAndMotionApplication
      val x = (origEnergy + appliedEnergy - stateAfterFixingVelocity.thermalEnergy - stateAfterFixingVelocity.ke) / motionSeriesObject.mass / motionSeriesObject.gravity.abs / sin(getAngle)
      stateAfterFixingThermal.setPosition(x)
    } else {
      stateAfterFixingVelocity
    }

    val delta = stateAfterFixingPosition.totalEnergy - origEnergy - appliedEnergy
    if (delta.abs > 1E-6 && appliedEnergy.abs < 1E-4) { //assume applied energy could absorb some error
      println("failed to conserve energy, delta=".literal + delta + ", applied energy = ".literal + appliedEnergy)
    }

    val stateAfterPatchingUpThermalEnergy = stateAfterFixingPosition.setThermalEnergy(motionSeriesObject.getThermalEnergy(stateAfterFixingPosition.thermalEnergy))

    if (stateAfterPatchingUpThermalEnergy.thermalEnergy < origState.thermalEnergy) {
      println("lost thermal energy.  original = " + origState.thermalEnergy + ", final = " + stateAfterPatchingUpThermalEnergy.thermalEnergy)
    }

    if (collide && bounce) motionSeriesObject.notifyBounced()
    else if (collide && !bounce) motionSeriesObject.notifyCollidedWithWall()

    stateAfterPatchingUpThermalEnergy
  }
}