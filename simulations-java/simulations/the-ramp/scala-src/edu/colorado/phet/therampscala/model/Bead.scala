package edu.colorado.phet.therampscala.model


import graphics.Vector
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import java.lang.Math._
import scalacommon.Predef._

case class BeadState(position: Double, velocity: Double, mass: Double, staticFriction: Double, kineticFriction: Double) {
  def translate(dx: Double) = setPosition(position + dx)

  def setPosition(pos: Double) = new BeadState(pos, velocity, mass, staticFriction, kineticFriction)

  def setVelocity(vel: Double) = new BeadState(position, vel, mass, staticFriction, kineticFriction)

  def setMass(m: Double) = new BeadState(position, velocity, m, staticFriction, kineticFriction)

  def thermalEnergy = 0
}
class Bead(_state: BeadState, private var _height: Double, positionMapper: Double => Vector2D, rampSegmentAccessor: Double => RampSegment, model: Observable) extends Observable {
  val gravity = -9.8
  var state = _state
  var _parallelAppliedForce = 0.0

  val gravityForceVector = new Vector(RampDefaults.gravityForceColor, "Gravity Force", "<html>F<sub>g</sub></html>") {
    def getValue = gravityForce
  }
  val normalForceVector = new Vector(RampDefaults.normalForceColor, "Normal Force", "<html>F<sub>N</sub></html>") {
    def getValue = normalForce
  }
  val totalForceVector = new Vector(RampDefaults.totalForceColor, "Total Force (sum of forces)", "<html>F<sub>total</sub></html>") {
    def getValue = totalForce
  }
  val appliedForceVector = new Vector(RampDefaults.appliedForceColor, "Applied Force", "<html>F<sub>a</sub></html>") {
    def getValue = appliedForce
  }
  appliedForceVector.addListenerByName(totalForceVector.notifyListeners())
  gravityForceVector.addListenerByName(totalForceVector.notifyListeners())
  normalForceVector.addListenerByName(totalForceVector.notifyListeners())
  //  frictionForceVector.addListener(totalForceVector.notifyListeners())

  addListenerByName(appliedForceVector.notifyListeners())//todo: just listen for changes to applied force parallel component

  def totalForce = {
    gravityForceVector.getValue + normalForceVector.getValue + appliedForceVector.getValue
  }

  def normalForce = {
    val magnitude = (gravityForce * -1) dot getRampUnitVector.rotate(PI / 2)
    val angle = getRampUnitVector.getAngle + PI / 2
    //    debug eval "angle" -> angle
    //    debug eval "magnitude" -> magnitude
    new Vector2D(angle) * (magnitude)
  }

  def gravityForce = new Vector2D(0, gravity * mass)

  def parallelAppliedForce = _parallelAppliedForce

  def parallelAppliedForce_=(value: Double) = {
    _parallelAppliedForce = value
    //    _appliedForce = new Vector2D(value, 0)
    notifyListeners()
  }
  //  def getRampUnitVector=new Vector2D(rampSegmentAccessor(position).angle)

  def appliedForce = getRampUnitVector * _parallelAppliedForce

  //  def appliedForce_=(force: Vector2D) = {
  //    _appliedForce = force
  //    notifyListeners()
  //  }

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
    notifyListeners()
  }

  def getTotalEnergy = getPotentialEnergy + getKineticEnergy

  def getPotentialEnergy = mass * gravity * position2D.y

  def getKineticEnergy = 1 / 2 * mass * velocity * velocity

  def getAngleInvertY = {
    val vector = rampSegmentAccessor(position).getUnitVector
    val vectorInvertY = new Vector2D(vector.x, -vector.y)
    vectorInvertY.getAngle
  }
}