package edu.colorado.phet.therampscala.model


import collection.mutable.ArrayBuffer
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
           model: Observable, surfaceFriction: () => Boolean,
           wallsExist: => Boolean,
           wallRange: () => Range)
        extends Observable {
  val id = Bead.nextIndex()
  val crashListeners = new ArrayBuffer[() => Unit]
  val stopListeners = new ArrayBuffer[() => Unit]
  val gravity = -9.8

  def state = _state

  def state_=(s: BeadState) = {_state = s; notifyListeners()}

  var _parallelAppliedForce = 0.0
  private var attachState: MotionStrategy = new Grounded

  val gravityForceVector = new BeadVector(RampDefaults.gravityForceColor, "Gravity Force", "g", false, () => gravityForce, (a, b) => b)
  val normalForceVector = new BeadVector(RampDefaults.normalForceColor, "Normal Force", "N", true, () => normalForce, (a, b) => b)
  val totalForceVector = new BeadVector(RampDefaults.totalForceColor, "Sum of Forces", "sum", false, () => totalForce, (a, b) => b)
  val appliedForceVector = new BeadVector(RampDefaults.appliedForceColor, "Applied Force", "a", false, () => appliedForce, (a, b) => b)
  val frictionForceVector = new BeadVector(RampDefaults.frictionForceColor, "Friction Force", "f", true, () => frictionForce, (a, b) => b)
  val wallForceVector = new BeadVector(RampDefaults.wallForceColor, "Wall Force", "w", false, () => wallForce, (a, b) => b)
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

  def getVelocityVectorDirection = (positionMapper(position + velocity * 1E-6) - positionMapper(position - velocity * 1E-6)).getAngle

  def getVelocityVectorUnitVector = new Vector2D(getVelocityVectorDirection)

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

  private var _surfaceFrictionStrategy = new SurfaceFrictionStrategy {
    def getTotalFriction(objectFriction: Double) = objectFriction
  }

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

  private def setVelocityWithNotify(velocity: Double, notify: Boolean) = {
    if (velocity != state.velocity) {
      state = state.setVelocity(velocity)
      frictionForceVector.notifyListeners()
      if (notify) notifyListeners()
    }
  }

  def setVelocity(velocity: Double) = setVelocityWithNotify(velocity, true)

  def mass_=(mass: Double) = {
    state = state.setMass(mass)
    gravityForceVector.notifyListeners()
    notifyListeners()
  }

  def setPositionWithNotify(position: Double, notify: Boolean) = {
    if (position != state.position) {
      state = state.setPosition(position)
      normalForceVector.notifyListeners() //since ramp segment or motion state might have changed; could improve performance on this by only sending notifications when we are sure the ramp segment has changed
      frictionForceVector.notifyListeners() //todo: omit this call since it's probably covered by the normal force call above
      wallForceVector.notifyListeners()
      if (notify) notifyListeners()
    }
  }

  def setPosition(position: Double) = setPositionWithNotify(position, true)

  private var _airborneFloor = 0.0

  def airborneFloor = _airborneFloor

  def airborneFloor_=(airborneFloor: Double) = {
    this._airborneFloor = airborneFloor
  }

  def getTotalEnergy = getPotentialEnergy + getKineticEnergy + getThermalEnergy

  def getPotentialEnergy = mass * gravity * position2D.y

  def getAppliedWork = 0.0

  private var thermalEnergy = 0.0

  def getThermalEnergy = thermalEnergy

  def getFrictiveWork = -getThermalEnergy

  def getGravityWork = -getPotentialEnergy

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

    def multiBodyFriction(f: Double) = {
      _surfaceFrictionStrategy.getTotalFriction(f)
    }

    override def frictionForce = {
      if (surfaceFriction()) {
        //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
        if (abs(velocity) < 1E-12) {

          //use up to fMax in preventing the object from moving
          //see static friction discussion here: http://en.wikipedia.org/wiki/Friction
          val fMax = abs(multiBodyFriction(staticFriction) * normalForce.magnitude)
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
          new Vector2D(vel.getAngle + PI) * normalForce.magnitude * multiBodyFriction(kineticFriction)
        }
      }
      else new Vector2D
    }

    override def stepInTime(dt: Double) = {
      //      println("grounded.step for "+id)
      val origState = state

      setVelocityWithNotify(netForceToParallelVelocity(totalForce, dt), false)

      //stepInTime samples at least one value less than 1E-12 on direction change to handle static friction
      if ((origState.velocity < 0 && velocity > 0) || (origState.velocity > 0 && velocity < 0)) {
        //see docs in static friction computation
        setVelocity(0)
      }

      if (origState.velocity != 0 && velocity == 0) {
        stopListeners.foreach(_())
      }

      val requestedPosition = position + velocity * dt

      //      println("requested position=" + requestedPosition + ", minRange=" + wallRange().min)

      //TODO: generalize boundary code
      if (requestedPosition <= wallRange().min + width / 2) {
        setVelocity(0)
        setPosition(wallRange().min + width / 2)
      }
      else if (requestedPosition >= wallRange().max - width / 2 && wallsExist) {
        setVelocity(0)
        setPosition(wallRange().max - width / 2)
      }
      else if (requestedPosition > wallRange().max + width / 2 && !wallsExist) {
        attachState = new Airborne(position2D, new Vector2D(getVelocityVectorDirection) * velocity, getAngle)
        parallelAppliedForce = 0
      }

      else {
        setPositionWithNotify(requestedPosition, false)
      }
      val justCollided = false

      if (multiBodyFriction(staticFriction) == 0 && multiBodyFriction(kineticFriction) == 0) {
        val appliedWork = getTotalEnergy
        val gravityWork = -getPotentialEnergy
        val thermalEnergy = origState.thermalEnergy
        if (justCollided) {
          //        thermalEnergy += origState.kineticEnergy
        }
        val frictionWork = -thermalEnergy
        ()
        //        new WorkEnergyState(appliedWork, gravityWork, frictionWork, getPotentialEnergy, getKineticEnergy, getTotalEnergy)
      } else {
        val dx = position - origState.position
        thermalEnergy = thermalEnergy + abs((frictionForce dot getVelocityVectorUnitVector) * dx) //work done by friction force, absolute value
        //      val dW=getAppliedWorkDifferential
        //      val appliedWork=origState.appliedWork
        //      val gravityWork=-getPotentialEnergy
        //      val etot=appliedWork
        //      val thermalEnergy=etot-kineticEnergy-potentialEnergy
        //      val frictionWork=-thermalEnergy

      }
      val distanceVector = positionMapper(position) - positionMapper(origState.position)
      val work = appliedForce dot distanceVector
      //      println("work done on particle by applied force: "+work)
      workListeners.foreach(_(work))
      notifyListeners() //do as a batch, since it's a performance problem to do this several times in this method call
    }
  }
  val workListeners = new ArrayBuffer[Double => Unit]

  def stepInTime(dt: Double) = attachState.stepInTime(dt)
}