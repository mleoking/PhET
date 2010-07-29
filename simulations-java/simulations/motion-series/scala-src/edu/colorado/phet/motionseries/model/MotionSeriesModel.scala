package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.motionseries.graphics.{RampSurfaceModel, ObjectModel}
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import edu.colorado.phet.scalacommon.math.Vector2D
import java.awt.geom.Point2D
import edu.colorado.phet.scalacommon.util.Observable
import java.lang.Math._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.common.motion.charts.ChartCursor
import edu.colorado.phet.recordandplayback.model.{DataPoint, RecordAndPlaybackModel}

case class RampState(angle: Double, heat: Double, wetness: Double)

//This class stores all state information used in record/playback
case class RecordedState(rampState: RampState,
                         selectedObject: MotionSeriesObjectTypeState,
                         beadState: MotionSeriesObjectState,
                         manBeadState: MotionSeriesObjectState,
                         appliedForce: Double,
                         walls: Boolean,
                         motionStrategyMemento: MotionStrategyMemento,
                         time: Double,
                         frictionless: Boolean)

class MotionSeriesModel(defaultBeadPosition: Double,
                        pausedOnReset: Boolean,
                        initialAngle: Double)
        extends RecordAndPlaybackModel[RecordedState](1000) with ObjectModel with RampSurfaceModel {
  override def isRecordingFull = {
    getTime > 20.0 //TODO: factor out max time
  }

  //Don't let the cursor drag past max time
  override def addRecordedPoint(point: DataPoint[RecordedState]) = {
    if (point.getTime <= 20.0) { //TODO: factor out max time
      super.addRecordedPoint(point)
    }
  }

  override def clearHistory() = {
    super.clearHistory()
  }

  private var _walls = true
  private var _frictionless = MotionSeriesDefaults.FRICTIONLESS_DEFAULT
  private var _bounce = MotionSeriesDefaults.BOUNCE_DEFAULT
  private var _selectedObject = MotionSeriesDefaults.objects(0)
  val chartCursor = new ChartCursor()

  val rampSegments = new ArrayBuffer[RampSegment]
  val stepListeners = new ArrayBuffer[() => Unit]
  val playbackListeners = new ArrayBuffer[() => Unit]
  val rampLength = 10
  setPaused(pausedOnReset)

  rampSegments += new RampSegment(new Point2D.Double(-rampLength, 0), new Point2D.Double(0, 0))
  rampSegments += new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(rampLength * cos(initialAngle), rampLength * sin(initialAngle)))

  val coordinateFrameModel = new CoordinateFrameModel(rampSegments(1))

  //Sends notification when any ramp segment changes
  object rampChangeAdapter extends Observable //todo: perhaps we should just pass the addListener method to the beads
  rampSegments(0).addListenerByName {rampChangeAdapter.notifyListeners}
  rampSegments(1).addListenerByName {rampChangeAdapter.notifyListeners}
  val surfaceFriction = () => !frictionless
  val wallsBounce = () => bounce

  val defaultManPosition = defaultBeadPosition - 1
  val leftWall = MovingManMotionSeriesObject(this, -10, MotionSeriesDefaults.wall.width, MotionSeriesDefaults.wall.height)
  val rightWall = MovingManMotionSeriesObject(this, 10, MotionSeriesDefaults.wall.width, MotionSeriesDefaults.wall.height)

  val leftWallRightEdge = MovingManMotionSeriesObject(this, -10 + MotionSeriesDefaults.wall.width / 2, MotionSeriesDefaults.SPRING_WIDTH, MotionSeriesDefaults.SPRING_HEIGHT)
  val rightWallLeftEdge = MovingManMotionSeriesObject(this, 10 - MotionSeriesDefaults.wall.width / 2, MotionSeriesDefaults.SPRING_WIDTH, MotionSeriesDefaults.SPRING_HEIGHT)

  val manMotionSeriesObject = MovingManMotionSeriesObject(this, defaultManPosition, 1, 3)

  val wallRange = () => new Range(-rampSegments(0).length, rampSegments(1).length)

  val surfaceFrictionStrategy = new SurfaceFrictionStrategy() {
    //todo: allow different values for different segments
    def getTotalFriction(objectFriction: Double) = new LinearFunction(0, 1, objectFriction, objectFriction * 0.75).evaluate(rampSegments(0).wetness)
  }
  val motionSeriesObject = new MovingManMotionSeriesObject(new MotionSeriesObjectState(defaultBeadPosition, 0,
    _selectedObject.mass, _selectedObject.staticFriction, _selectedObject.kineticFriction, 0.0, 0.0, 0.0),
    _selectedObject.height, _selectedObject.width, positionMapper,
    rampSegmentAccessor, rampChangeAdapter, surfaceFriction, wallsBounce, surfaceFrictionStrategy, walls, wallRange, thermalEnergyStrategy)
  updateDueToObjectChange()

  def thermalEnergyStrategy(x: Double) = x

  val raindrops = new ArrayBuffer[Raindrop]
  val fireDogs = new ArrayBuffer[FireDog]
  val fireDogAddedListeners = new ArrayBuffer[FireDog => Unit]
  val raindropAddedListeners = new ArrayBuffer[Raindrop => Unit]
  private var totalThermalEnergyOnClear = 0.0
  val maxDrops = (60 * 0.75).toInt
  val elapsedTimeHistory = new ArrayBuffer[Long]

  def stepRecord(): Unit = stepRecord(MotionSeriesDefaults.DT_DEFAULT)

  def step(simulationTimeChange: Double) = {
    stepRecord()
    val mode = motionSeriesObject.motionStrategy.getMemento
    new RecordedState(new RampState(rampAngle, rampSegments(1).heat, rampSegments(1).wetness),
      selectedObject.state, motionSeriesObject.state, manMotionSeriesObject.state, motionSeriesObject.parallelAppliedForce, walls, mode, getTime, frictionless)
  }

  def motionSeriesObjectInModelViewportRange = motionSeriesObject.position2D.x < MotionSeriesDefaults.MIN_X || motionSeriesObject.position2D.x > MotionSeriesDefaults.MAX_X

  def returnMotionSeriesObject() = {
    motionSeriesObject.setDesiredPosition(defaultBeadPosition)
    motionSeriesObject.setPosition(defaultBeadPosition)
    motionSeriesObject.parallelAppliedForce = 0
    motionSeriesObject.setVelocity(0)
    motionSeriesObject.attach()
  }

  def resetBead() = {
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
      selectedObject = MotionSeriesDefaults.objects(0)
      frictionless = MotionSeriesDefaults.FRICTIONLESS_DEFAULT
      walls = true
      resetBead()
      manMotionSeriesObject.setPosition(defaultManPosition)

      rampSegments(0).setWetness(0.0)
      rampSegments(0).setHeat(0.0)
      rampSegments(1).setWetness(0.0)
      rampSegments(1).setHeat(0.0)
      rampSegments(1).setAngle(initialAngle)

      resetListeners.foreach(_())
      bounce = MotionSeriesDefaults.BOUNCE_DEFAULT

      setPaused(pausedOnReset)
    }
  }

  /**
   * Instantly clear the heat from the ramps.
   */
  def clearHeatInstantly() {
    rampSegments(0).setWetness(0.0)
    rampSegments(0).setHeat(0.0)
    rampSegments(1).setWetness(0.0)
    rampSegments(1).setHeat(0.0)
    motionSeriesObject.thermalEnergy = 0.0
  }

  /**
   * Requests that the fire dog clear the heat over a period of time.
   */
  def clearHeat() = {
    if (isPaused) {
      clearHeatInstantly()
    } else {
      if (fireDogs.length == 0) {
        totalThermalEnergyOnClear = motionSeriesObject.thermalEnergy
        val fireDog = new FireDog(this) //cue the fire dog, which will eventually clear the thermal energy
        fireDogs += fireDog //updates when clock ticks
        fireDogAddedListeners.foreach(_(fireDog))
      }
    }
  }

  def rainCrashed() = {
    rampSegments(0).dropHit()
    rampSegments(1).dropHit()
    val reducedEnergy = totalThermalEnergyOnClear / (maxDrops / 2.0)
    motionSeriesObject.thermalEnergy = motionSeriesObject.thermalEnergy - reducedEnergy
    motionSeriesObject.crashEnergy = java.lang.Math.max(motionSeriesObject.crashEnergy - reducedEnergy, 0)
    if (motionSeriesObject.thermalEnergy < 1) motionSeriesObject.thermalEnergy = 0.0
  }

  def setPlaybackState(state: RecordedState) = {
    rampAngle = state.rampState.angle
    frictionless = state.frictionless
    rampSegments(0).setHeat(state.rampState.heat)
    rampSegments(0).setWetness(state.rampState.wetness)

    rampSegments(1).setHeat(state.rampState.heat)
    rampSegments(1).setWetness(state.rampState.wetness)

    selectedObject = state.selectedObject.toObject
    motionSeriesObject.motionStrategy = state.motionStrategyMemento.getMotionStrategy(motionSeriesObject)
    motionSeriesObject.state = state.beadState //nice code

    motionSeriesObject.parallelAppliedForce = state.appliedForce
    manMotionSeriesObject.state = state.manBeadState
    walls = state.walls

    //based on time constraints, decision was made to not record and playback firedogs + drops, just make sure they clear
    while (raindrops.length > 0)
      raindrops(0).remove()

    while (fireDogs.length > 0)
      fireDogs(0).remove()

    chartCursor.setTime(state.time)
  }

  def selectedObject = _selectedObject

  def selectedObject_=(obj: MotionSeriesObjectType) = {
    if (_selectedObject != obj) {
      _selectedObject = obj
      updateDueToObjectChange()
    }
  }

  def updateDueToObjectChange() = {
    motionSeriesObject.mass = _selectedObject.mass
    motionSeriesObject.width = _selectedObject.width
    motionSeriesObject.height = _selectedObject.height
    motionSeriesObject.staticFriction = _selectedObject.staticFriction
    motionSeriesObject.kineticFriction = _selectedObject.kineticFriction

    //todo: remove listeners on object selection change
    _selectedObject match {
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
    notifyListeners()
  }

  def bounce = _bounce

  def frictionless = _frictionless

  def walls = _walls

  def walls_=(b: Boolean) = {
    if (b != walls) {
      _walls = b
      updateSegmentLengths()
      notifyListeners()
    }
  }

  //duplicates some work with wallrange
  //todo: call this method when ramp angle changes, since it depends on ramp angle
  def updateSegmentLengths() = {
    val seg0Length = if (rampSegments(0).angle > 0 || _walls) rampLength else 10000
    val seg1Length = if (rampSegments(1).angle > 0 || _walls) rampLength else 10000
    setSegmentLengths(seg0Length, seg1Length)
  }

  def setSegmentLengths(seg0Length: Double, seg1Length: Double) = {
    rampSegments(0).startPoint = new Vector2D(rampSegments(0).angle) * -seg0Length
    rampSegments(0).endPoint = new Vector2D(0, 0)

    rampSegments(1).startPoint = new Vector2D(0, 0)
    rampSegments(1).endPoint = new Vector2D(rampSegments(1).angle) * seg1Length
  }

  def bounce_=(b: Boolean) = {
    _bounce = b
    rampChangeAdapter.notifyListeners()
    notifyListeners()
  }

  def frictionless_=(b: Boolean) = {
    _frictionless = b
    rampChangeAdapter.notifyListeners()
    if (_frictionless) clearHeatInstantly()
    notifyListeners()
  }

  def rampAngle_=(angle: Double) = rampSegments(1).setAngle(angle)

  def rampAngle = rampSegments(1).angle

  //TODO: this may need to be more general if/when there are more/less ramp segments
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

  override def step() = {
    super.step()
    playbackListeners.foreach(_())
  }

  private def doStep(dt: Double) = {
    motionSeriesObject.stepInTime(dt)
    for (f <- fireDogs) f.stepInTime(dt)
    for (r <- raindrops) r.stepInTime(dt)
    val rampHeat = motionSeriesObject.rampThermalEnergy
    rampSegments(0).setHeat(rampHeat)
    rampSegments(1).setHeat(rampHeat)
    rampSegments(0).stepInTime(dt)
    rampSegments(1).stepInTime(dt)

    stepListeners.foreach(_())
    notifyListeners() //signify to the Timeline that more data has been added
  }

  override def stepInTime(dt: Double) = {
    val startTime = System.nanoTime
    super.stepInTime(dt)
    val endTime = System.nanoTime
    val elapsedTimeMS = endTime - startTime
    elapsedTimeHistory += elapsedTimeMS
    while (elapsedTimeHistory.length > 100) elapsedTimeHistory.remove(0)
    val avg = elapsedTimeHistory.foldLeft(0L)(_ + _) / elapsedTimeHistory.length
    ()
  }

  def stepRecord(dt: Double) = doStep(dt)
}

trait SurfaceFrictionStrategy {
  def getTotalFriction(objectFriction: Double): Double
}