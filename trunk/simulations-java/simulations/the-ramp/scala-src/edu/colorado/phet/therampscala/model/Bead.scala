package edu.colorado.phet.therampscala.model


import common.phetcommon.math.MathUtil
import graphics.{PointOfOriginVector, Vector}
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.awt.{Graphics2D, TexturePaint, Paint, Color}
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

  def setStaticFriction(value: Double) = new BeadState(position, velocity, mass, value, kineticFriction)

  def setKineticFriction(value: Double) = new BeadState(position, velocity, mass, staticFriction, value)

  def setMass(m: Double) = new BeadState(position, velocity, m, staticFriction, kineticFriction)

  def thermalEnergy = 0
}

case class Range(min: Double, max: Double)

class Bead(_state: BeadState, private var _height: Double, positionMapper: Double => Vector2D,
           rampSegmentAccessor: Double => RampSegment, model: Observable, surfaceFriction: () => Boolean, wallsExist: => Boolean,
           wallRange: () => Range)
        extends Observable {
  val gravity = -9.8
  var state = _state
  var _parallelAppliedForce = 0.0
  private var attachState: MotionStrategy = new Grounded

  val gravityForceVector = new BeadVector(RampDefaults.gravityForceColor, "Gravity Force", "<html>F<sub>g</sub></html>", false, () => gravityForce, (a, b) => b)
  val normalForceVector = new BeadVector(RampDefaults.normalForceColor, "Normal Force", "<html>F<sub>N</sub></html>", true, () => normalForce, (a, b) => b)
  val totalForceVector = new BeadVector(RampDefaults.totalForceColor, "Total Force (sum of forces)", "<html>F<sub>total</sub></html>", false, () => totalForce, (a, b) => b)
  val appliedForceVector = new BeadVector(RampDefaults.appliedForceColor, "Applied Force", "<html>F<sub>a</sub></html>", false, () => appliedForce, (a, b) => b)
  val frictionForceVector: BeadVector = new BeadVector(RampDefaults.frictionForceColor, "Friction Force", "<html>F<sub>f</sub></html>", true, () => frictionForce, (a, b) => b)
  val wallForceVector = new BeadVector(RampDefaults.wallForceColor, "Wall Force", "<html>F<sub>w</sub></html>", false, () => wallForce, (a, b) => b)
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

  def attach() = attachState = new Grounded

  def totalForce = gravityForce + normalForce + appliedForce + frictionForce + wallForce

  def wallForce = attachState.wallForce

  def frictionForce = attachState.frictionForce

  def normalForce = attachState.normalForce

  def gravityForce = new Vector2D(0, gravity * mass)

  def getVelocityVectorDirection = (positionMapper(position + velocity * 1E-6) - positionMapper(position - velocity * 1E-6)).getAngle

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

  private var _airborneFloor = 0.0

  def airborneFloor_=(airborneFloor: Double) = {
    this._airborneFloor = airborneFloor
  }

  def getTotalEnergy = getPotentialEnergy + getKineticEnergy

  def getPotentialEnergy = mass * gravity * position2D.y

  def getKineticEnergy = 1 / 2 * mass * velocity * velocity

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

    override def stepInTime(dt: Double) = {
      val tf = totalForce
      val accel = totalForce / mass
      _velocity2D = _velocity2D + accel * dt
      _position2D = _position2D + _velocity2D * dt
      if (_position2D.y <= _airborneFloor)
        attachState = new Crashed(new Vector2D(_position2D.x, _airborneFloor), _angle)
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
      new Vector2D(angle) * (magnitude)
    }

    override def wallForce = {
      if (position <= wallRange().min && forceToParallelAcceleration(appliedForce) < 0) {
        appliedForce * -1
      }
      else if (position >= wallRange().max && forceToParallelAcceleration(appliedForce) > 0) {
        appliedForce * -1
      } else {
        new Vector2D
      }
    }

    override def frictionForce = {
      if (surfaceFriction()) {
        //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
        if (abs(velocity) < 1E-12) {

          //use up to fMax in preventing the object from moving
          //see static friction discussion here: http://en.wikipedia.org/wiki/Friction
          val fMax = abs(staticFriction * normalForce.magnitude)
          val netForceWithoutFriction = appliedForce + gravityForce + normalForce + wallForce

          if (netForceWithoutFriction.magnitude >= fMax) {
            new Vector2D(netForceWithoutFriction.getAngle + PI) * fMax
          }
          else {
            new Vector2D(netForceWithoutFriction.getAngle + PI) * netForceWithoutFriction.magnitude
          }
        }
        else {
          //object is moving, just use kinetic friction
          val vel = (positionMapper(position) - positionMapper(position - velocity * 1E-6))
          new Vector2D(vel.getAngle + PI) * normalForce.magnitude * kineticFriction
        }
      }
      else new Vector2D
    }

    override def stepInTime(dt: Double) = {
      val origState = state

      setVelocity(netForceToParallelVelocity(totalForce, dt))

      //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
      if ((origState.velocity < 0 && velocity > 0) || (origState.velocity > 0 && velocity < 0)) {
        //see docs in static friction computation
        setVelocity(0)
      }

      val requestedPosition = position + velocity * dt

      //TODO: generalize boundary code
      if (requestedPosition <= wallRange().min) {
        setVelocity(0)
        setPosition(wallRange().min)
      }
      else if (requestedPosition >= wallRange().max && wallsExist) {
        setVelocity(0)
        setPosition(wallRange().max)
      }
      else if (requestedPosition > wallRange().max && !wallsExist) {
        attachState = new Airborne(position2D, new Vector2D(getVelocityVectorDirection) * velocity, getAngle)
        parallelAppliedForce = 0
      }

      else {
        setPosition(requestedPosition)
      }
      val justCollided = false

      if (staticFriction == 0 && kineticFriction == 0) {
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

  def stepInTime(dt: Double) = attachState.stepInTime(dt)
}