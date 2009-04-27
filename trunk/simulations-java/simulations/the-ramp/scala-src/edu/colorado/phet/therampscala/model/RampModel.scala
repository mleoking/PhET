package edu.colorado.phet.therampscala.model


import collection.mutable.ArrayBuffer
import graphics.ObjectModel
import scalacommon.math.Vector2D
import java.awt.geom.Point2D
import scalacommon.util.Observable
import scalacommon.record.RecordModel
import java.lang.Math._

class CoordinateFrameModel(snapToAngles: List[() => Double]) extends Observable {
  private var _angle = 0.0

  def angle = _angle

  def angle_=(ang: Double) = {
    _angle = ang
    notifyListeners()
  }

  def dropped() = {
    var snapChoice = _angle
    for (a <- snapToAngles) {
      val snapToAngle = a()
      if (abs(snapToAngle - _angle) < 10.0.toRadians) {
        snapChoice = snapToAngle
      }
    }

    angle = snapChoice
  }
}

class RampModel extends RecordModel[String] with ObjectModel {
  setPaused(false)

  val rampSegments = new ArrayBuffer[RampSegment]
  //  val bead=new Bead
  //  val beads = new ArrayBuffer[Bead]
  private var _walls = true
  private var _frictionless = false
  private var _selectedObject = RampDefaults.objects(0)

  val rampLength = 10
  rampSegments += new RampSegment(new Point2D.Double(-rampLength, 0), new Point2D.Double(0, 0))
  val initialAngle = 30.0.toRadians
  rampSegments += new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(rampLength * cos(initialAngle), rampLength * sin(initialAngle)))

  val coordinateFrameModel = new CoordinateFrameModel((() => rampSegments(1).angle) :: (() => 0.0) :: Nil)

  //Sends notification when any ramp segment changes
  object rampChangeAdapter extends Observable //todo: perhaps we should just pass the addListener method to the beads
  rampSegments(0).addListenerByName {rampChangeAdapter.notifyListeners}
  rampSegments(1).addListenerByName {rampChangeAdapter.notifyListeners}
  val surfaceFriction = () => !frictionless
  val wallRange = () => if (walls) new Range(RampDefaults.MIN_X, RampDefaults.MAX_X) else new Range(-10000, RampDefaults.MAX_X)
  val bead: Bead = new Bead(new BeadState(5, 0, _selectedObject.mass, _selectedObject.staticFriction, _selectedObject.kineticFriction),
    3, positionMapper, rampSegmentAccessor, rampChangeAdapter, surfaceFriction, walls, wallRange)

  def createBead(x:Double)=new Bead(new BeadState(x, 0, 10, 0, 0), 3, positionMapper, rampSegmentAccessor, rampChangeAdapter, surfaceFriction, walls, wallRange)

  val tree = createBead(-4)
  val leftWall = createBead(-10)
  val rightWall = createBead(10)
  val manBead = createBead(2)
  updateDueToObjectChange()

  override def resetAll() = {
    selectedObject = RampDefaults.objects(0)
    frictionless = false
    walls = true
    bead.setPosition(5)
    bead.parallelAppliedForce = 0
    bead.setVelocity(0)
    bead.attach()
    rampSegments(1).setAngle(initialAngle)
  }

  def setPlaybackState(state: String) {}

  def handleRecordStartedDuringPlayback() {}

  def getMaxRecordPoints = 100

  def selectedObject = _selectedObject

  def selectedObject_=(obj: ScalaRampObject) = {
    _selectedObject = obj
    updateDueToObjectChange()
  }

  def updateDueToObjectChange() = {
    bead.mass = _selectedObject.mass
    bead.height = _selectedObject.height
    bead.staticFriction = _selectedObject.staticFriction
    bead.kineticFriction = _selectedObject.kineticFriction

    //todo: remove listeners on object selection change
    _selectedObject match {
      case o: MutableRampObject => {
        o.addListenerByName {
          bead.height = o.height
          bead.mass = o.mass
        }
      }
      case _ => {}
    }
    notifyListeners()
  }

  def frictionless = _frictionless

  def walls = _walls

  def walls_=(b: Boolean) = {
    _walls = b

    if (_walls) {
      rampSegments(0).startPoint = new Vector2D(-rampLength, 0)
    } else {
      rampSegments(0).startPoint = new Vector2D(-10000, 0)
    }

    notifyListeners()
  }

  def frictionless_=(b: Boolean) = {
    _frictionless = b
    rampChangeAdapter.notifyListeners()
    notifyListeners()
  }

  def setRampAngle(angle: Double) = {
    rampSegments(1).setAngle(angle)
  }

  //TODO: this may need to be more general
  def positionMapper(particleLocation: Double) = {
    if (particleLocation <= 0) {
      val backwardsUnitVector = rampSegments(0).getUnitVector * -1 //go backwards since position is measure from origin
      backwardsUnitVector * (-particleLocation) + rampSegments(0).endPoint
    }
    else {
      rampSegments(1).getUnitVector * (particleLocation) + rampSegments(1).startPoint
    }
  }

  def rampSegmentAccessor(particleLocation: Double) = if (particleLocation <= 0) rampSegments(0) else rampSegments(1)

  def update(dt: Double) = {
    if (!isPaused) {
      //      beads.foreach(_.stepInTime(dt))
      bead.stepInTime(dt)
    }
  }

  def stepRecord(dt: Double) = {
    //    beads.foreach(_.stepInTime(dt))
    bead.stepInTime(dt)
  }
}

case class WorkEnergyState(appliedWork: Double, gravityWork: Double, frictionWork: Double,
                           potentialEnergy: Double, kineticEnergy: Double, totalEnergy: Double)