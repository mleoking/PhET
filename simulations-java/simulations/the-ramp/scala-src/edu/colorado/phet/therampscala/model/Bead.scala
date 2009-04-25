package edu.colorado.phet.therampscala.model


import common.phetcommon.math.MathUtil
import graphics.{PointOfOriginVector, Vector}
import java.awt.Color
import java.util.Date
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import java.lang.Math._
import scalacommon.Predef._

/**Immutable memento for recording*/
case class BeadState(position: Double, velocity: Double, mass: Double, staticFriction: Double, kineticFriction: Double) {
  def translate(dx: Double) = setPosition(position + dx)

  def setPosition(pos: Double) = new BeadState(pos, velocity, mass, staticFriction, kineticFriction)

  def setVelocity(vel: Double) = new BeadState(position, vel, mass, staticFriction, kineticFriction)

  def setMass(m: Double) = new BeadState(position, velocity, m, staticFriction, kineticFriction)

  def thermalEnergy = 0
}

abstract class BeadVector(color: Color, name: String, abbreviation: String, bottomPO: Boolean //shows point of origin at the bottom when in that mode
        ) extends Vector(color, name, abbreviation) with PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double) = if (bottomPO) 0.0 else defaultCenter
}

class Bead(_state: BeadState, private var _height: Double, positionMapper: Double => Vector2D, rampSegmentAccessor: Double => RampSegment, model: Observable) extends Observable {
  val gravity = -9.8
  var state = _state
  var _parallelAppliedForce = 0.0

  val gravityForceVector = new BeadVector(RampDefaults.gravityForceColor, "Gravity Force", "<html>F<sub>g</sub></html>", false) {
    def getValue = gravityForce
  }
  val normalForceVector = new BeadVector(RampDefaults.normalForceColor, "Normal Force", "<html>F<sub>N</sub></html>", true) {
    def getValue = normalForce
  }
  val totalForceVector = new BeadVector(RampDefaults.totalForceColor, "Total Force (sum of forces)", "<html>F<sub>total</sub></html>", false) {
    def getValue = totalForce
  }
  val appliedForceVector = new BeadVector(RampDefaults.appliedForceColor, "Applied Force", "<html>F<sub>a</sub></html>", false) {
    def getValue = appliedForce
  }
  val frictionForceVector: BeadVector = new BeadVector(RampDefaults.frictionForceColor, "Friction Force", "<html>F<sub>f</sub></html>", true) {
    def getValue = frictionForce
  }

  val wallForceVector = new BeadVector(RampDefaults.wallForceColor, "Wall Force", "<html>F<sub>w</sub></html>", false) {
    def getValue = wallForce
  }

  //chain listeners
  normalForceVector.addListenerByName(frictionForceVector.notifyListeners())
  //todo: add normalForceVector notification when changing friction coefficients

  appliedForceVector.addListenerByName(totalForceVector.notifyListeners())
  gravityForceVector.addListenerByName(totalForceVector.notifyListeners())
  normalForceVector.addListenerByName(totalForceVector.notifyListeners())
  frictionForceVector.addListenerByName(totalForceVector.notifyListeners())

  addListenerByName(appliedForceVector.notifyListeners()) //todo: just listen for changes to applied force parallel component

  def totalForce = gravityForceVector.getValue + normalForceVector.getValue + appliedForceVector.getValue + frictionForceVector.getValue + wallForceVector.getValue

  def wallForce = {
    if (position <= RampDefaults.MIN_X && forceToParallelAcceleration(appliedForceVector.getValue) < 0) {
      appliedForceVector.getValue * -1
    }
    else if (position >= RampDefaults.MAX_X && forceToParallelAcceleration(appliedForceVector.getValue) > 0) {
      appliedForceVector.getValue * -1
    } else {
      new Vector2D
    }
  }

  //todo: notify friction force changed when ramp angle changes or velocity changes
  def frictionForce = {
    if (wallForce.magnitude > 0)
      new Vector2D
    else {
      //todo: friction is not allowed to turn the object around or accelerate the object from rest
      //if friction would reverse the object's velocity (or increase its velocity)
      //then instead choose a friction vector that will bring instead the object to rest.
      val canonicalFrictionForce = getCanonicalFrictionForce
      val partialSum = appliedForceVector.getValue + gravityForceVector.getValue +
              normalForceVector.getValue + wallForceVector.getValue + canonicalFrictionForce

      val velWithNetForce = netForceToParallelVelocity(partialSum, _dt)
      val velWithNetForceIgnoreFriction = netForceToParallelVelocity(partialSum - canonicalFrictionForce, _dt)

      //      reduce friction if its magnitude is greater than magnitude of sum of other forces
      if (canonicalFrictionForce.magnitude > (appliedForce + gravityForce + normalForce).magnitude) {
        //choose friction so net force will just slow the object down

        val alpha = 0.9 //reduction scale factor, v_f= v0 * alpha
        var desiredSumForcesMagnitude = abs(velocity * (alpha - 1) * mass / _dt)
        if (desiredSumForcesMagnitude < 1E-12)
          desiredSumForcesMagnitude = 1E-12

        //        println("velocityVector=" + getVelocityVectorDirection.toDegrees + " degrees")
        val desiredSumForces = new Vector2D(getVelocityVectorDirection + PI) * desiredSumForcesMagnitude
        val desiredFrictionForce = desiredSumForces - appliedForce - gravityForce - normalForce

        desiredFrictionForce
      }
      else {
        //        println("canonical at " + System.currentTimeMillis)
        canonicalFrictionForce
      }
    }
  }

  def getVelocityVectorDirection = (positionMapper(position + velocity * 1E-6) - positionMapper(position - velocity * 1E-6)).getAngle

  def getCanonicalFrictionForce = {
    val frictionCoefficient = if (velocity > 1E-6) getKineticFriction else getStaticFriction
    val magnitude = normalForce.magnitude * frictionCoefficient
    val vel = (positionMapper(position) - positionMapper(position - velocity * 1E-6))
    val angle = (vel * -1).getAngle
    new Vector2D(angle) * magnitude
  }

  def normalForce = {
    val magnitude = (gravityForce * -1) dot getRampUnitVector.rotate(PI / 2)
    val angle = getRampUnitVector.getAngle + PI / 2
    new Vector2D(angle) * (magnitude)
  }

  def gravityForce = new Vector2D(0, gravity * mass)

  def parallelAppliedForce = _parallelAppliedForce

  def parallelAppliedForce_=(value: Double) = {
    _parallelAppliedForce = value
    appliedForceVector.notifyListeners()
    notifyListeners()
  }

  def appliedForce = getRampUnitVector * _parallelAppliedForce

  def position2D = positionMapper(position)

  def getRampUnitVector = rampSegmentAccessor(position).getUnitVector

  model.addListenerByName(notifyListeners)
  def mass = state.mass

  def position = state.position

  def velocity = state.velocity

  def translate(dx: Double) = {
    state = state.translate(dx)
    notifyListeners()
  }

  def height_=(height: Double) = {
    _height = height
    notifyListeners
  }

  def height = _height

  def getStaticFriction = state.staticFriction

  def getKineticFriction = state.kineticFriction

  def setVelocity(velocity: Double) = {
    state = state.setVelocity(velocity)
    frictionForceVector.notifyListeners()
    notifyListeners()
  }

  def mass_=(mass: Double) = {
    state = state.setMass(mass)
    gravityForceVector.notifyListeners()
    notifyListeners()
  }

  def setPosition(position: Double) = {
    state = state.setPosition(position)
    normalForceVector.notifyListeners() //since ramp segment might have changed; could improve performance on this by only sending notifications when we are sure the ramp segment has changed
    frictionForceVector.notifyListeners() //todo: omit this call since it's probably covered by the normal force call above
    wallForceVector.notifyListeners()
    notifyListeners()
  }

  def getTotalEnergy = getPotentialEnergy + getKineticEnergy

  def getPotentialEnergy = mass * gravity * position2D.y

  def getKineticEnergy = 1 / 2 * mass * velocity * velocity

  def getAngle = rampSegmentAccessor(position).getUnitVector.getAngle

  def getAngleInvertY = {
    val vector = rampSegmentAccessor(position).getUnitVector
    val vectorInvertY = new Vector2D(vector.x, -vector.y)
    vectorInvertY.getAngle
  }

  def forceToParallelAcceleration(f: Vector2D) = (f dot getRampUnitVector) / mass

  def netForceToParallelVelocity(f: Vector2D, dt: Double) = velocity + forceToParallelAcceleration(f) * dt

  private var _dt = 1E-6

  def newStepCode(dt: Double) = {
    _dt = dt
    val origState = state

    val netForce = totalForce

    setVelocity(netForceToParallelVelocity(totalForce, dt))

    val requestedPosition = position + velocity * dt

    //TODO: generalize boundary code
    if (requestedPosition <= RampDefaults.MIN_X) {
      setVelocity(0)
      setPosition(RampDefaults.MIN_X)
    }
    else if (requestedPosition >= RampDefaults.MAX_X) {
      setVelocity(0)
      setPosition(RampDefaults.MAX_X)
    }
    else {
      setPosition(requestedPosition)
    }
    val justCollided = false

    if (getStaticFriction == 0 && getKineticFriction == 0) {
      val appliedWork = getTotalEnergy
      val gravityWork = -getPotentialEnergy
      val thermalEnergy = origState.thermalEnergy
      if (justCollided) {
        //        thermalEnergy += origState.kineticEnergy
      }
      val frictionWork = -thermalEnergy
      frictionWork
      new WorkEnergyState(appliedWork, gravityWork, frictionWork,
        getPotentialEnergy, getKineticEnergy, getTotalEnergy)
    } else {
      //      val dW=getAppliedWorkDifferential
      //      val appliedWork=origState.appliedWork
      //      val gravityWork=-getPotentialEnergy
      //      val etot=appliedWork
      //      val thermalEnergy=etot-kineticEnergy-potentialEnergy
      //      val frictionWork=-thermalEnergy

    }
  }
}