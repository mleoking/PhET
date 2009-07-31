package edu.colorado.phet.therampscala.model


import collection.mutable.ArrayBuffer
import graphics.ObjectModel
import scalacommon.math.Vector2D
import java.awt.geom.Point2D
import scalacommon.record.{DataPoint, RecordModel}
import scalacommon.util.Observable
import java.lang.Math._

class WordModel extends Observable {
  var _physicsWords = true
  var _everydayWords = false

  resetAll()
  def resetAll() = {
    physicsWords = true
    everydayWords = false
  }

  def physicsWords_=(v: Boolean) = {
    _physicsWords = v
    _everydayWords = !_physicsWords

    notifyListeners()
  }

  def physicsWords = _physicsWords

  def everydayWords = _everydayWords

  def everydayWords_=(v: Boolean) = {
    _everydayWords = v
    _physicsWords = !v
    notifyListeners()
  }
}
class FreeBodyDiagramModel extends Observable {
  private var _windowed = false
  private var _visible = false
  private var _closable = true

  resetAll()
  def resetAll() = {
    windowed = false
    visible = false
    closable = true
  }

  def closable = _closable

  def closable_=(b: Boolean) = {
    _closable = b
    notifyListeners()
  }

  def visible = _visible

  def windowed = _windowed

  def visible_=(value: Boolean) = {
    _visible = value;
    notifyListeners()
  }

  def windowed_=(value: Boolean) = {
    _windowed = value
    notifyListeners()
  }

}
class CoordinateSystemModel extends Observable {
  private var _fixed = true

  resetAll()
  def resetAll() = {
    fixed = true
  }

  def fixed = _fixed

  def adjustable = !_fixed

  def fixed_=(b: Boolean) = {
    _fixed = b
    notifyListeners()
  }

  def adjustable_=(b: Boolean) = {
    _fixed = !b
    notifyListeners()
  }
}

class VectorViewModel extends Observable {
  private var _centered = true
  private var _originalVectors = true
  private var _parallelComponents = false
  private var _xyComponentsVisible = false
  private var _sumOfForcesVector = false

  resetAll()
  def resetAll() = {
    centered = true
    originalVectors = true
    parallelComponents = false
    xyComponentsVisible = false
    sumOfForcesVector = false
  }

  def centered = _centered

  def centered_=(__centered: Boolean) = {
    _centered = __centered;
    notifyListeners()
  }

  def originalVectors = _originalVectors

  def parallelComponents = _parallelComponents

  def xyComponentsVisible = _xyComponentsVisible

  def sumOfForcesVector = _sumOfForcesVector

  def originalVectors_=(b: Boolean) = {
    _originalVectors = b
    notifyListeners()
  }

  def parallelComponents_=(b: Boolean) = {
    _parallelComponents = b
    notifyListeners()
  }

  def xyComponentsVisible_=(b: Boolean) = {
    _xyComponentsVisible = b
    notifyListeners()
  }

  def sumOfForcesVector_=(b: Boolean) = {
    _sumOfForcesVector = b
    notifyListeners()
  }
}

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

//This class stores all state information used in record/playback
case class RecordedState(angle: Double, selectedObject: ScalaRampObjectState, beadState: BeadState, manBeadState: BeadState, appliedForce: Double, walls: Boolean)

class RampModel(defaultBeadPosition:Double,pausedOnReset:Boolean) extends RecordModel[RecordedState] with ObjectModel {
  setPaused(pausedOnReset)

  private var _walls = true
  private var _frictionless = false
  private var _selectedObject = RampDefaults.objects(0)

  val rampSegments = new ArrayBuffer[RampSegment]
  val stepListeners = new ArrayBuffer[() => Unit]

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
  //  val wallRange = () => if (walls) new Range(RampDefaults.MIN_X, RampDefaults.MAX_X) else new Range(-10000, RampDefaults.MAX_X)
  val wallRange = () => {
    if (walls)
      new Range(leftWall.maxX, rightWall.minX)
    else
      new Range(-10000, RampDefaults.MAX_X)
  }
  val bead = new Bead(new BeadState(defaultBeadPosition, 0, _selectedObject.mass, _selectedObject.staticFriction, _selectedObject.kineticFriction),
    _selectedObject.height, _selectedObject.width, positionMapper, rampSegmentAccessor, rampChangeAdapter, surfaceFriction, walls, wallRange)

  def createBead(x: Double, width: Double, height: Double) = new Bead(new BeadState(x, 0, 10, 0, 0), height, width, positionMapper, rampSegmentAccessor, rampChangeAdapter, surfaceFriction, walls, wallRange)

  def createBead(x: Double, width: Double): Bead = createBead(x, width, 3)

  val leftWall: Bead = createBead(-10, RampDefaults.wall.width, RampDefaults.wall.height)
  val rightWall: Bead = createBead(10, RampDefaults.wall.width, RampDefaults.wall.height)
  val manBead = createBead(2, 1)
  updateDueToObjectChange()

  override def resetAll() = {
    super.resetAll()
    clearHistory()
    selectedObject = RampDefaults.objects(0)
    frictionless = false
    walls = true
    bead.setPosition(defaultBeadPosition)
    bead.parallelAppliedForce = 0
    bead.setVelocity(0)
    bead.attach()
    rampSegments(1).setAngle(initialAngle)
    setPaused(pausedOnReset)
  }

  def setPlaybackState(state: RecordedState) = {
    setRampAngle(state.angle)
    selectedObject = state.selectedObject.toObject
    bead.state = state.beadState //nice code
    bead.parallelAppliedForce = state.appliedForce
    manBead.state = state.manBeadState
    walls = state.walls
  }

  def handleRecordStartedDuringPlayback() = {}

  def getMaxRecordPoints = 1000

  def selectedObject = _selectedObject

  def selectedObject_=(obj: ScalaRampObject) = {
    if (_selectedObject != obj) {
      _selectedObject = obj
      updateDueToObjectChange()
    }
  }

  def updateDueToObjectChange() = {
    bead.mass = _selectedObject.mass
    bead.width = _selectedObject.width
    bead.height = _selectedObject.height
    bead.staticFriction = _selectedObject.staticFriction
    bead.kineticFriction = _selectedObject.kineticFriction

    //todo: remove listeners on object selection change
    _selectedObject match {
      case o: MutableRampObject => {
        o.addListenerByName {
          bead.height = o.height
          bead.mass = o.mass
          bead.width = o.width
          bead.staticFriction = o.staticFriction
          bead.kineticFriction = o.kineticFriction
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

  def getRampAngle = rampSegments(1).angle

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

  private def doStep(dt: Double) = {
    super.setTime(getTime + dt)
    bead.stepInTime(dt)
    recordHistory += new DataPoint(getTime, new RecordedState(getRampAngle, selectedObject.state, bead.state, manBead.state, bead.parallelAppliedForce, walls))
    stepListeners.foreach(_())
    notifyListeners() //signify to the Timeline that more data has been added
  }

  override def clearHistory() = {
    super.clearHistory()
    setTime(0.0)
  }

  def update(dt: Double) = {
    if (!isPaused) {
      if (isRecord) doStep(dt)
      else if (isPlayback) stepPlayback()
    }
  }

  def stepRecord(dt: Double) = doStep(dt)
}

trait SurfaceFrictionStrategy {
  def getTotalFriction(objectFriction: Double): Double
}