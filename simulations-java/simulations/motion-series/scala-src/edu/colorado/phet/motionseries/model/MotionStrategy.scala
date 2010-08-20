package edu.colorado.phet.motionseries.model

import edu.colorado.phet.common.phetcommon.math.MathUtil
import edu.colorado.phet.scalacommon.math.Vector2D
import java.lang.Math._
import edu.colorado.phet.motionseries.Predef._

//Used to save/restore motion strategies during record/playback
trait MotionStrategyMemento {
  def getMotionStrategy(motionSeriesObject: ForceMotionSeriesObject): MotionStrategy
}

abstract class MotionStrategy(val motionSeriesObject: ForceMotionSeriesObject) {
  def isCrashed: Boolean

  def stepInTime(dt: Double)

  def position2D: Vector2D

  def getAngle: Double

  def getMemento: MotionStrategyMemento

  def wallForce = new Vector2D

  //This method has to include a parameter for whether the wall force should be included to avoid an infinite recursive loop
  //in computing the wall force
  def frictionForce(includeWallForce: Boolean): Vector2D = new Vector2D

  def frictionForce: Vector2D = frictionForce(true)

  def normalForce = new Vector2D

  //accessors/adapters for subclass convenience
  //This class was originally designed to be an inner class of MotionSeriesObject, but IntelliJ debugger didn't support debug into inner classes at the time
  //so these classes were refactored to be top level classes to enable debugging.  They can be refactored back to inner classes when there is better debug support

  def totalForce = motionSeriesObject.totalForce

  def gravityForce = motionSeriesObject.gravityForce

  def mass = motionSeriesObject.mass

  def normalForceVector = motionSeriesObject.normalForceVector

  def positionMapper = motionSeriesObject.positionMapper

  def rampSegmentAccessor = motionSeriesObject.rampSegmentAccessor

  def wallRange = motionSeriesObject.wallRange()

  def position = motionSeriesObject.position

  def appliedForce = motionSeriesObject.appliedForce

  def gravity = motionSeriesObject.gravity

  def notificationsEnabled = motionSeriesObject.notificationsEnabled

  def getTotalEnergy = motionSeriesObject.getTotalEnergy

  def state = motionSeriesObject.state

  def velocity = motionSeriesObject.velocity

  def workListeners = motionSeriesObject.workListeners

  def surfaceFriction = motionSeriesObject.surfaceFriction

  def thermalEnergy = motionSeriesObject.thermalEnergy

  def airborneFloor = motionSeriesObject.airborneFloor

  def crashListeners = motionSeriesObject.crashListeners

  def getRampUnitVector = motionSeriesObject.getRampUnitVector

  def staticFriction = motionSeriesObject.staticFriction

  def kineticFriction = motionSeriesObject.kineticFriction

  def width = motionSeriesObject.width

  def wallsExist = motionSeriesObject.wallsExist

  def getVelocityVectorDirection = motionSeriesObject.getVelocityVectorDirection
}

//This Crashed state indicates that the object has fallen off the ramp or off a cliff, not that it has crashed into a wall.
class Crashed(_position2D: Vector2D, _angle: Double, motionSeriesObject: ForceMotionSeriesObject) extends MotionStrategy(motionSeriesObject) {
  def isCrashed = true

  def stepInTime(dt: Double) = {}

  override def normalForce = gravityForce * -1

  def position2D = _position2D

  def getAngle = _angle

  def getMemento = {
    new MotionStrategyMemento {
      def getMotionStrategy(motionSeriesObject: ForceMotionSeriesObject) = new Crashed(position2D, getAngle, motionSeriesObject)
    }
  }
}

class Airborne(private var _position2D: Vector2D, private var _velocity2D: Vector2D, _angle: Double, motionSeriesObject: ForceMotionSeriesObject) extends MotionStrategy(motionSeriesObject: ForceMotionSeriesObject) {
  def isCrashed = false

  override def toString = "position = ".literal + position2D

  def getAngle = _angle

  def velocity2D = _velocity2D

  override def stepInTime(dt: Double) = {
    val originalEnergy = motionSeriesObject.getTotalEnergy
    val accel = totalForce / mass
    _velocity2D = _velocity2D + accel * dt
    _position2D = _position2D + _velocity2D * dt
    motionSeriesObject.setTime(motionSeriesObject.time + dt)
    if (_position2D.y <= airborneFloor) {
      motionSeriesObject.motionStrategy = new Crashed(new Vector2D(_position2D.x, motionSeriesObject.airborneFloor), _angle, motionSeriesObject)
      crashListeners.foreach(_())
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
    } else {
      motionSeriesObject.setVelocity(_velocity2D.magnitude)

      //ensure that energy is exactly conserved by fine-tuning the vertical position of the object; note that this wouldn't work in low g
      val dy = (motionSeriesObject.getTotalEnergy - originalEnergy) / motionSeriesObject.mass / motionSeriesObject.gravity
      _position2D = new Vector2D(_position2D.x, _position2D.y + dy)
    }
    normalForceVector.notifyListeners() //since ramp segment or motion state might have changed; could improve performance on this by only sending notifications when we are sure the ramp segment has changed
    motionSeriesObject.notifyListeners() //to get the new normalforce

  }

  override def position2D = _position2D

  def getMemento = new AirborneMemento(position2D, velocity2D, getAngle)
}

class AirborneMemento(p: Vector2D, v: Vector2D, a: Double) extends MotionStrategyMemento {
  def getMotionStrategy(motionSeriesObject: ForceMotionSeriesObject) = new Airborne(p, v, a, motionSeriesObject)

  override def toString = "airborn motion strategy mode, p = ".literal + p
}

class Grounded(motionSeriesObject: ForceMotionSeriesObject) extends MotionStrategy(motionSeriesObject) {
  def isCrashed = false

  def getMemento = {
    new MotionStrategyMemento {
      def getMotionStrategy(motionSeriesObject: ForceMotionSeriesObject) = new Grounded(motionSeriesObject)
    }
  }

  def position2D = positionMapper(position)

  def getAngle = rampSegmentAccessor(position).getUnitVector.angle

  override def normalForce = {
    val magnitude = (gravityForce * -1) dot getRampUnitVector.rotate(PI / 2)
    val angle = getRampUnitVector.angle + PI / 2
    new Vector2D(angle) * magnitude
  }

  //is the block about to collide?
  def collideLeft = position + velocity * dt < leftBound && wallsExist

  def collideRight = position + velocity * dt > rightBound && wallsExist

  def collide = collideLeft || collideRight

  def leftBound = motionSeriesObject.wallRange().min + width / 2

  def rightBound = motionSeriesObject.wallRange().max - width / 2

  override def wallForce = {

    val epsilon = 1E-4 //this is a physics workaround to help reduce or resolve the flickering problem by enabling the 'pressing' wall force a bit sooner

    //Friction force should enter into the system before wall force, since the object would have to move before "communicating" with the wall.
    val netForceWithoutWallForce = appliedForce + gravityForce + normalForce + frictionForce(false)
    val pressingLeft = position <= leftBound + epsilon && motionSeriesObject.forceToParallelAcceleration(netForceWithoutWallForce) < 0 && wallsExist
    val pressingRight = position >= rightBound - epsilon && motionSeriesObject.forceToParallelAcceleration(netForceWithoutWallForce) > 0 && wallsExist
    val pressing = pressingLeft || pressingRight

    if (pressing)
      netForceWithoutWallForce * -1
    else if (collide) {
      //create a large instantaneous impulse force during a collision
      val finalVelocity = if (bounce) -velocity else 0.0
      val deltaV = finalVelocity - velocity
      //Fnet = m dv/dt = Fw + F_{-w}
      val sign = if (collideRight) -1.0 else +1.0
      val wallCollisionForce = abs(mass * deltaV / dt - netForceWithoutWallForce.magnitude) * sign

      val resultWallForce = getRampUnitVector * wallCollisionForce
      resultWallForce
    } else new Vector2D
  }

  def multiBodyFriction(f: Double) = motionSeriesObject.surfaceFrictionStrategy.getTotalFriction(f)

  //see super notes regarding the includeWallForce
  override def frictionForce(includeWallForce: Boolean) = {
    if (surfaceFriction()) {
      //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
      if (velocity.abs < 1E-12) {

        //use up to fMax in preventing the object from moving
        //see static friction discussion here: http://en.wikipedia.org/wiki/Friction
        val fMax = abs(multiBodyFriction(staticFriction) * normalForce.magnitude)
        val netForceWithoutFriction = appliedForce + gravityForce + normalForce + (if (includeWallForce) wallForce else new Vector2D())

        val magnitude = if (netForceWithoutFriction.magnitude >= fMax) fMax else netForceWithoutFriction.magnitude
        new Vector2D(netForceWithoutFriction.angle + PI) * magnitude
      }
      else {
        //object is moving, just use kinetic friction
        val vel = (positionMapper(position) - positionMapper(position - velocity * 1E-6))
        new Vector2D(vel.angle + PI) * normalForce.magnitude * multiBodyFriction(kineticFriction)
      }
    }
    else new Vector2D
  }

  case class SettableState(position: Double, velocity: Double, thermalEnergy: Double, crashEnergy: Double) {
    def setPosition(p: Double) = new SettableState(p, velocity, thermalEnergy, crashEnergy)

    def setVelocity(v: Double) = new SettableState(position, v, thermalEnergy, crashEnergy)

    def setThermalEnergy(t: Double) = new SettableState(position, velocity, t, crashEnergy)

    def setPositionAndVelocity(p: Double, v: Double) = new SettableState(p, v, thermalEnergy, crashEnergy)

    //todo: this is duplicated with code in MotionSeriesObject
    lazy val totalEnergy = ke + pe + thermalEnergy
    lazy val ke = mass * velocity * velocity / 2.0
    lazy val pe = mass * gravity.abs * positionMapper(position).y //assumes positionmapper doesn't change, which is true during stepintime
  }

  private var dt = 1.0 / 30.0 //using dummy value before getting actual value in case any computations are done
  override def stepInTime(dt: Double) = {
    this.dt = dt
    motionSeriesObject.notificationsEnabled = false //make sure only to send notifications as a batch at the end; improves performance by 17%
    val origEnergy = getTotalEnergy
    val origState = state
    val newState = getNewState(dt, origState, origEnergy)

    if (newState.position > motionSeriesObject.wallRange().max + width / 2 && !wallsExist) {
      motionSeriesObject.motionStrategy = new Airborne(position2D, new Vector2D(getVelocityVectorDirection) * velocity, getAngle, motionSeriesObject)
      motionSeriesObject.parallelAppliedForce = 0
    }
    val distanceVector = positionMapper(newState.position) - positionMapper(origState.position)
    val work = appliedForce dot distanceVector
    workListeners.foreach(_(work))
    motionSeriesObject.setTime(motionSeriesObject.time + dt)
    motionSeriesObject.setPosition(newState.position)
    motionSeriesObject.setVelocity(newState.velocity)
    motionSeriesObject.thermalEnergy = newState.thermalEnergy
    motionSeriesObject.crashEnergy = newState.crashEnergy

    motionSeriesObject.notificationsEnabled = true
    motionSeriesObject.notifyListeners() //do as a batch, since it's a performance problem to do this several times in this method call
    motionSeriesObject.wallForceVector.notifyListeners()
  }

  def bounce = motionSeriesObject.wallsBounce()

  def isKineticFriction = surfaceFriction() && kineticFriction > 0

  def getNewState(dt: Double, origState: MotionSeriesObjectState, origEnergy: Double) = {
    val newVelocity = {
      val desiredVel = motionSeriesObject.netForceToParallelVelocity(totalForce, dt)
      //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
      //see docs in static friction computation
      val newVelocityThatGoesThroughZero = if ((velocity < 0 && desiredVel > 0) || (velocity > 0 && desiredVel < 0)) 0.0 else desiredVel
      //make sure velocity is exactly zero or opposite after wall collision
      if (collide && bounce) -velocity else if (collide) 0.0 else newVelocityThatGoesThroughZero
    }

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
    val appliedEnergy = (appliedForce dot motionSeriesObject.getVelocityVectorUnitVector(stateAfterCollision.velocity)) * dx.abs

    //      val thermalFromWork = getThermalEnergy + abs((frictionForce dot getVelocityVectorUnitVector(stateAfterBounds.velocity)) * dx) //work done by friction force, absolute value
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
      sign * sqrt(abs(2.0 / mass * (origEnergy + appliedEnergy - state.pe - origState.thermalEnergy)))
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
      val x = (origEnergy + appliedEnergy - stateAfterFixingVelocity.thermalEnergy - stateAfterFixingVelocity.ke) / mass / gravity.abs / sin(getAngle)
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