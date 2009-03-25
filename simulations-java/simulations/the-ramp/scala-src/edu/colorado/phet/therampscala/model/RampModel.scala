package edu.colorado.phet.therampscala.model


import collection.mutable.ArrayBuffer
import graphics.ObjectModel
import scalacommon.math.Vector2D
import java.awt.geom.Point2D
import scalacommon.util.Observable
import scalacommon.record.RecordModel
import java.lang.Math._

class RampModel extends RecordModel[String] with ObjectModel {
  def setPlaybackState(state: String) {}

  def handleRecordStartedDuringPlayback() {}

  def getMaxRecordPoints = 100

  val rampSegments = new ArrayBuffer[RampSegment]
  val beads = new ArrayBuffer[Bead]
  private var _walls = true
  private var _frictionless = false
  private var _selectedObject = RampDefaults.objects(0)

  def selectedObject = _selectedObject

  def selectedObject_=(obj: ScalaRampObject) = {
    _selectedObject = obj
    beads(0).mass = _selectedObject.mass
    notifyListeners()
  }

  def walls = _walls

  def frictionless = _frictionless

  def walls_=(b: Boolean) = {
    _walls = b
    notifyListeners()
  }

  def frictionless_=(b: Boolean) = {
    _frictionless = b
    notifyListeners()
  }

  rampSegments += new RampSegment(new Point2D.Double(-10, 0), new Point2D.Double(0, 0))
  rampSegments += new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(10 * sin(PI / 4), 10 * sin(PI / 4)))

  def setRampAngle(angle: Double) = {
    rampSegments(1).setAngle(angle)
  }

  //TODO: this may need to be more general
  def positionMapper(particleLocation: Double) = {
    if (particleLocation <= 0) rampSegments(0).getUnitVector * (10 + particleLocation) + rampSegments(0).startPoint
    else rampSegments(1).getUnitVector * (particleLocation) + rampSegments(1).startPoint
  }

  def rampSegmentAccessor(particleLocation: Double) = {
    if (particleLocation <= 0) rampSegments(0) else rampSegments(1)
  }

  //Sends notification when any ramp segment changes
  object rampChangeAdapter extends Observable //todo: perhaps we should just pass the addListener method to the beads
  rampSegments(0).addListenerByName {rampChangeAdapter.notifyListeners}
  rampSegments(1).addListenerByName {rampChangeAdapter.notifyListeners}
  beads += new Bead(new BeadState(5, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)
  val tree = new Bead(new BeadState(-9, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)
  val leftWall = new Bead(new BeadState(-10, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)
  val rightWall = new Bead(new BeadState(10, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)

  val manBead = new Bead(new BeadState(2, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)

  def update(dt: Double) = {
    beads.foreach(b => newStepCode(b, dt))
  }

  case class WorkEnergyState(appliedWork: Double, gravityWork: Double, frictionWork: Double,
                            potentialEnergy: Double, kineticEnergy: Double, totalEnergy: Double)

  def newStepCode(b: Bead, dt: Double) = {
    val origState = b.state
    val forces = getForces(b)
    val netForce = forces.foldLeft(new Vector2D)((a, b) => {a + b})
    val parallelForce = netForce.dot(b.getRampUnitVector)
    val parallelAccel = parallelForce / b.mass
    b.setVelocity(b.velocity + parallelAccel * dt)

    val requestedPosition = b.position + b.velocity * dt

    //TODO: generalize boundary code
    if (requestedPosition <= -10) {
      b.setVelocity(0)
      b.setPosition(-10)
    }
    else if (requestedPosition >= 10) {
      b.setVelocity(0)
      b.setPosition(10)
    }
    else {
      b.setPosition(requestedPosition)
    }
    val justCollided = false

    if (b.getStaticFriction == 0 && b.getKineticFriction == 0) {
      val appliedWork = b.getTotalEnergy
      val gravityWork = -b.getPotentialEnergy
      val thermalEnergy = origState.thermalEnergy
      if (justCollided) {
        //        thermalEnergy += origState.kineticEnergy
      }
      val frictionWork = -thermalEnergy
      frictionWork
      new WorkEnergyState(appliedWork, gravityWork, frictionWork,
        b.getPotentialEnergy, b.getKineticEnergy, b.getTotalEnergy)
    } else {
      //      val dW=getAppliedWorkDifferential
      //      val appliedWork=origState.appliedWork
      //      val gravityWork=-getPotentialEnergy
      //      val etot=appliedWork
      //      val thermalEnergy=etot-kineticEnergy-potentialEnergy
      //      val frictionWork=-thermalEnergy

    }
  }

  def getForces(b: Bead) = {
    getGravityForce(b) :: b.appliedForce :: Nil
    //    getGravity :: getFriction(b) :: getWallForce(b) :: getNormalForce(b) :: Nil
    //    val netForce=getGravity+getFriction(b)+getNormal
  }

  def getGravityForce(b: Bead) = {
    new Vector2D(0, -9.8) * b.mass
  }
}