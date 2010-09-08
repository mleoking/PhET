package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.motionseries.graphics.{RampSurfaceModel, ObjectModel}
import edu.colorado.phet.scalacommon.math.Vector2D
import java.awt.geom.Point2D
import edu.colorado.phet.scalacommon.util.Observable
import java.lang.Math._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.common.motion.charts.ChartCursor
import edu.colorado.phet.recordandplayback.model.{DataPoint, RecordAndPlaybackModel}
import edu.colorado.phet.common.phetcommon.model.MutableBoolean
import edu.colorado.phet.common.phetcommon.math.MathUtil
import edu.colorado.phet.common.phetcommon.util.SimpleObserver
import edu.colorado.phet.motionseries.charts.MutableDouble
import edu.colorado.phet.motionseries.util.ScalaMutableBoolean

class MotionSeriesModel(defaultPosition: Double,
                        pausedOnReset: Boolean,
                        initialAngle: Double)
        extends RecordAndPlaybackModel[RecordedState](1000) with ObjectModel with RampSurfaceModel {
  private val _walls = new ScalaMutableBoolean(true)
  private val _frictionless = new ScalaMutableBoolean(false) //FRICTIONLESS_DEFAULT
  private val _wallsBounce = new ScalaMutableBoolean(false) //BOUNCE_DEFAULT 
  private var _objectType = MotionSeriesDefaults.objectTypes(0)
  val surfaceFrictionStrategy = new SurfaceFrictionStrategy() {
    def getTotalFriction(objectFriction: Double) = objectFriction
  }
  val chartCursor = new ChartCursor()

  val rampSegments = new ArrayBuffer[RampSegment]

  val stepListeners = new ArrayBuffer[() => Unit]
  val recordListeners = new ArrayBuffer[() => Unit]
  val rampLength = 10
  setPaused(pausedOnReset)

  rampSegments += new RampSegment(new Point2D.Double(-rampLength, 0), new Point2D.Double(0, 0))
  rampSegments += new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(rampLength * cos(initialAngle), rampLength * sin(initialAngle)))

  val coordinateFrameModel = new CoordinateFrameModel(rampSegments(1))

  //Sends notification when any ramp segment changes
  val rampChangeAdapter = new Object with Observable //todo: perhaps we should just pass the addListener method to the MotionSeriesObjects
  rampSegments(0).addListener(rampChangeAdapter.notifyListeners)
  rampSegments(1).addListener(rampChangeAdapter.notifyListeners)
  val wallRange = () => Range(-rampSegments(0).length, rampSegments(1).length)
  val surfaceFriction = () => !frictionless

  val defaultManPosition = defaultPosition - 1 //Man should start 1 meter away from the object by default

  val leftWall = new MotionSeriesObject(this, -10, MotionSeriesDefaults.wall.width, MotionSeriesDefaults.wall.height)
  val rightWall = new MotionSeriesObject(this, 10, MotionSeriesDefaults.wall.width, MotionSeriesDefaults.wall.height)

  val leftWallRightEdge = new MotionSeriesObject(this, -10 + MotionSeriesDefaults.wall.width / 2, MotionSeriesDefaults.SPRING_WIDTH, MotionSeriesDefaults.SPRING_HEIGHT)
  val rightWallLeftEdge = new MotionSeriesObject(this, 10 - MotionSeriesDefaults.wall.width / 2, MotionSeriesDefaults.SPRING_WIDTH, MotionSeriesDefaults.SPRING_HEIGHT)

  val manMotionSeriesObject = new MotionSeriesObject(this, defaultManPosition, 1, 3)
  def thermalEnergyStrategy(x: Double) = x
  //This is the main object that forces are applied to
  val motionSeriesObject = new MotionSeriesObject(new MutableDouble(defaultPosition), new MutableDouble, new MutableDouble,
    new MutableDouble(_objectType.mass), new MutableDouble(_objectType.staticFriction), new MutableDouble(_objectType.kineticFriction), 
    _objectType.height, _objectType.width, toPosition2D,
    rampSegmentAccessor, rampChangeAdapter, _wallsBounce, walls, wallRange, thermalEnergyStrategy, surfaceFriction, surfaceFrictionStrategy)

  updateDueToObjectTypeChange()
  motionSeriesObject.stepInTime(0.0) //Update vectors using the motion strategy

  def stepRecord(): Unit = stepRecord(MotionSeriesDefaults.DT_DEFAULT)

  def step(simulationTimeChange: Double) = {
    stepRecord()
    val mode = motionSeriesObject.motionStrategy.getMemento
    new RecordedState(new RampState(rampAngle),
      selectedObject.state, motionSeriesObject.state, manMotionSeriesObject.state, motionSeriesObject.parallelAppliedForce, walls.booleanValue, mode, getTime, frictionless)
  }

  //Resume activity in the sim, starting it up when the user drags the object or the position slider
  def resume() = {
    if (isPlayback) {
      clearHistoryRemainder()
      setRecord(true)
    }
    setPaused(false)
  }

  override def isRecordingFull = getTime > MotionSeriesDefaults.MAX_RECORD_TIME

  //Don't let the cursor drag past max time
  override def addRecordedPoint(point: DataPoint[RecordedState]) = {
    if (point.getTime <= MotionSeriesDefaults.MAX_RECORD_TIME) {
      super.addRecordedPoint(point)
    }
  }

  def motionSeriesObjectInModelViewportRange = motionSeriesObject.position2D.x < MotionSeriesDefaults.MIN_X || motionSeriesObject.position2D.x > MotionSeriesDefaults.MAX_X

  def returnMotionSeriesObject() = {
    motionSeriesObject.setPosition(defaultPosition)
    motionSeriesObject.parallelAppliedForce = 0
    motionSeriesObject.setVelocity(0)
    motionSeriesObject.attach()
  }

  def resetObject() = {
    returnMotionSeriesObject()
    motionSeriesObject.crashEnergy = 0.0
    motionSeriesObject.thermalEnergy = 0.0
  }

  private val resetListeners = new ArrayBuffer[() => Unit]

  def resetListeners_+=(listener: () => Unit) = resetListeners += listener

  override def resetAll() = {
    super.resetAll()
    if (resetListeners != null) { //resetAll() is called from super's constructor, so have to make sure our data is inited before proceeding
      clearHistory()
      selectedObject = MotionSeriesDefaults.objectTypes(0)
      _frictionless.reset()
      walls.reset()
      resetObject()
      manMotionSeriesObject.setPosition(defaultManPosition)

      rampSegments(1).setAngle(initialAngle)

      resetListeners.foreach(_())
      bounce.reset()

      setPaused(pausedOnReset)
    }
  }

  def setPlaybackState(state: RecordedState) = {
    rampAngle = state.rampState.angle
    frictionless = state.frictionless

    selectedObject = state.selectedObject.toObject
    motionSeriesObject.motionStrategy = state.motionStrategyMemento.getMotionStrategy(motionSeriesObject)
    motionSeriesObject.state = state.motionSeriesObjectState //nice code

    motionSeriesObject.parallelAppliedForce = state.appliedForce
    manMotionSeriesObject.state = state.manState
    walls = state.walls

    chartCursor.setTime(state.time)
    stepListeners.foreach(_())
  }

  def selectedObject = _objectType

  def selectedObject_=(obj: MotionSeriesObjectType) = {
    if (_objectType != obj) {
      _objectType = obj
      updateDueToObjectTypeChange()
    }
  }

  private def updateDueToObjectTypeChange() = {
    motionSeriesObject.mass = _objectType.mass
    motionSeriesObject.width = _objectType.width
    motionSeriesObject.height = _objectType.height
    motionSeriesObject.staticFriction = _objectType.staticFriction
    motionSeriesObject.kineticFriction = _objectType.kineticFriction

    //todo: remove listeners on object selection change
    _objectType match {
      case o: MutableMotionSeriesObjectType => {
        o.addListenerByName {
          motionSeriesObject.height = o.height
          motionSeriesObject.mass = o.mass
          motionSeriesObject.width = o.width
          motionSeriesObject.staticFriction = o.staticFriction
          motionSeriesObject.kineticFriction = o.kineticFriction
        }
      }
      case _ => {}
    }

    //resolve collisions with the wall when switching objects
    motionSeriesObject.setPosition(MathUtil.clamp(MotionSeriesDefaults.MIN_X + motionSeriesObject.width / 2,
      motionSeriesObject.position, MotionSeriesDefaults.MAX_X - motionSeriesObject.width / 2))

    notifyListeners()
  }

  def bounce = _wallsBounce

  def bounce_=(b: Boolean) = {
    _wallsBounce.setValue(b)
    rampChangeAdapter.notifyListeners()
    notifyListeners()
  }

  //Determines whether the ramp is frictionless.  Object friction is handled elsewhere
  def frictionless = _frictionless.booleanValue

  def frictionless_=(b: Boolean) = {
    _frictionless.setValue(b)
    rampChangeAdapter.notifyListeners()
    notifyListeners()
  }

  def walls = _walls

  def walls_=(b: Boolean) = {
    if (b != _walls.booleanValue) {
      _walls.setValue(b)
      updateSegmentLengths()
      notifyListeners()
    }
  }

  //duplicates some work with wallrange
  //todo: call this method when ramp angle changes, since it depends on ramp angle
  def updateSegmentLengths() = {
    val seg0Length = if (rampSegments(0).angle > 0 || _walls.getValue.booleanValue) rampLength else MotionSeriesDefaults.FAR_DISTANCE
    val seg1Length = if (rampSegments(1).angle > 0 || _walls.getValue.booleanValue) rampLength else MotionSeriesDefaults.FAR_DISTANCE
    setSegmentLengths(seg0Length, seg1Length)
  }

  def setSegmentLengths(seg0Length: Double, seg1Length: Double) = {
    rampSegments(0).startPoint = new Vector2D(rampSegments(0).angle) * -seg0Length
    rampSegments(0).endPoint = new Vector2D(0, 0)

    rampSegments(1).startPoint = new Vector2D(0, 0)
    rampSegments(1).endPoint = new Vector2D(rampSegments(1).angle) * seg1Length
  }

  def rampAngle_=(angle: Double) = rampSegments(1).setAngle(angle)

  def rampAngle = rampSegments(1).angle

  /* Computes the 2D position for an object on the RampSegments, given its 1d scalar position. 
  This may need to be more general if/when there are more/less ramp segments
   */
  def toPosition2D(particleLocation: Double) = {
    if (particleLocation <= 0) {
      val backwardsUnitVector = rampSegments(0).unitVector * -1 //go backwards since position is measure from origin
      backwardsUnitVector * (-particleLocation) + rampSegments(0).endPoint
    }
    else {
      rampSegments(1).unitVector * (particleLocation) + rampSegments(1).startPoint
    }
  }

  def rampSegmentAccessor(particleLocation: Double) = if (particleLocation <= 0) rampSegments(0) else rampSegments(1)

  def stepRecord(dt: Double) = {
    motionSeriesObject.stepInTime(dt)

    notifyListeners() //signify to the Timeline that more data has been added
    recordListeners.foreach(_())
  }

  override def stepMode(dt: Double) = {
    super.stepMode(dt)
    if (!isPlayback) { //for playback mode, the stepListeners are already notified
      stepListeners.foreach(_())
    }
  }
}

trait SurfaceFrictionStrategy {
  def getTotalFriction(objectFriction: Double): Double
}