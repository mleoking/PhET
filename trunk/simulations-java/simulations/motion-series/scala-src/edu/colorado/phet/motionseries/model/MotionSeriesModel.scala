package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.motionseries.graphics.{RampSurfaceModel, ObjectModel}
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import edu.colorado.phet.scalacommon.math.Vector2D
import java.awt.geom.Point2D
import edu.colorado.phet.scalacommon.record.{DataPoint, RecordModel}
import edu.colorado.phet.scalacommon.util.Observable
import java.lang.Math._
import edu.colorado.phet.motionseries.MotionSeriesDefaults

case class RampState(angle: Double, heat: Double, wetness: Double)

//This class stores all state information used in record/playback
case class RecordedState(rampState: RampState,
                         selectedObject: MotionSeriesObjectState,
                         beadState: BeadState,
                         manBeadState: BeadState,
                         appliedForce: Double,
                         walls: Boolean,
                         motionStrategyMemento: MotionStrategyMemento)

class MotionSeriesModel(defaultBeadPosition: Double,
                        pausedOnReset: Boolean,
                        initialAngle: Double)
        extends RecordModel[RecordedState] with ObjectModel with RampSurfaceModel {
  private var _walls = true
  private var _frictionless = MotionSeriesDefaults.FRICTIONLESS_DEFAULT
  private var _bounce = MotionSeriesDefaults.BOUNCE_DEFAULT
  private var _selectedObject = MotionSeriesDefaults.objects(0)

  val rampSegments = new ArrayBuffer[RampSegment]
  val stepListeners = new ArrayBuffer[() => Unit]
  val rampLength = 10
  setPaused(pausedOnReset)

  rampSegments += new RampSegment(new Point2D.Double(-rampLength, 0), new Point2D.Double(0, 0))
  rampSegments += new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(rampLength * cos(initialAngle), rampLength * sin(initialAngle)))

  val coordinateFrameModel = new CoordinateFrameModel((() => rampSegments(1).angle) :: (() => 0.0) :: Nil)

  //Sends notification when any ramp segment changes
  object rampChangeAdapter extends Observable //todo: perhaps we should just pass the addListener method to the beads
  rampSegments(0).addListenerByName {rampChangeAdapter.notifyListeners}
  rampSegments(1).addListenerByName {rampChangeAdapter.notifyListeners}
  val surfaceFriction = () => !frictionless
  val wallsBounce = () => bounce

  val defaultManPosition = defaultBeadPosition - 1
  val leftWall = createBead(-10, MotionSeriesDefaults.wall.width, MotionSeriesDefaults.wall.height)
  val rightWall = createBead(10, MotionSeriesDefaults.wall.width, MotionSeriesDefaults.wall.height)

  val leftWallRightEdge = createBead(-10 + MotionSeriesDefaults.wall.width / 2, MotionSeriesDefaults.SPRING_WIDTH, MotionSeriesDefaults.SPRING_HEIGHT)
  val rightWallLeftEdge = createBead(10 - MotionSeriesDefaults.wall.width / 2, MotionSeriesDefaults.SPRING_WIDTH, MotionSeriesDefaults.SPRING_HEIGHT)

  val manBead = createBead(defaultManPosition, 1,3)

  val wallRange = () => {
    new Range(-rampSegments(0).length, rampSegments(1).length)
  }
  val surfaceFrictionStrategy = new SurfaceFrictionStrategy() {
    //todo: allow different values for different segments
    def getTotalFriction(objectFriction: Double) = new LinearFunction(0, 1, objectFriction, objectFriction * 0.75).evaluate(rampSegments(0).wetness)
  }
  val bead = new MovingManBead(new BeadState(defaultBeadPosition, 0,
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

  def createBead(x: Double, width: Double, height: Double) =
    new MovingManBead(new BeadState(x, 0, 10, 0, 0, 0.0, 0.0, 0.0), height, width, positionMapper, rampSegmentAccessor, rampChangeAdapter, surfaceFriction, wallsBounce, surfaceFrictionStrategy, walls, wallRange, thermalEnergyStrategy)

  def createBead(x: Double, width: Double): MovingManBead= createBead(x, width, 3)

  def stepRecord(): Unit = stepRecord(MotionSeriesDefaults.DT_DEFAULT)

  def beadInModelViewportRange = bead.position2D.x < MotionSeriesDefaults.MIN_X || bead.position2D.x > MotionSeriesDefaults.MAX_X

  def returnBead() = {
    bead.setDesiredPosition(defaultBeadPosition)
    bead.setPosition(defaultBeadPosition)
    bead.parallelAppliedForce = 0
    bead.setVelocity(0)
    bead.attach()
  }

  def resetBead() = {
    returnBead()
    bead.setCrashEnergy(0.0)
    bead.thermalEnergy = 0.0
  }

  override def resetAll() = {
    super.resetAll()
    clearHistory()
    selectedObject = MotionSeriesDefaults.objects(0)
    frictionless = MotionSeriesDefaults.FRICTIONLESS_DEFAULT
    walls = true
    resetBead()
    manBead.setPosition(defaultManPosition)

    rampSegments(1).setAngle(initialAngle)

    rampSegments(0).setWetness(0.0)
    rampSegments(0).setHeat(0.0)
    rampSegments(1).setWetness(0.0)
    rampSegments(1).setHeat(0.0)

    setPaused(pausedOnReset)
  }

  def clearHeatInstantly() {
    rampSegments(0).setWetness(0.0)
    rampSegments(0).setHeat(0.0)
    rampSegments(1).setWetness(0.0)
    rampSegments(1).setHeat(0.0)
    bead.thermalEnergy = 0.0
  }

  def clearHeat() = {
    if (isPaused) {
      clearHeatInstantly()
    } else {
      if (fireDogs.length == 0) {
        totalThermalEnergyOnClear = bead.thermalEnergy
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
    bead.thermalEnergy = bead.thermalEnergy - reducedEnergy
    bead.setCrashEnergy(java.lang.Math.max(bead.getCrashEnergy - reducedEnergy, 0))
    if (bead.thermalEnergy < 1) bead.thermalEnergy = 0.0
  }

  def setPlaybackState(state: RecordedState) = {
    setRampAngle(state.rampState.angle)
    rampSegments(0).setHeat(state.rampState.heat)
    rampSegments(1).setHeat(state.rampState.heat)

    rampSegments(0).setWetness(state.rampState.wetness)
    rampSegments(1).setWetness(state.rampState.wetness)

    selectedObject = state.selectedObject.toObject
    bead.motionStrategy = state.motionStrategyMemento.getMotionStrategy(bead)
    //println("playback state: "+bead.motionStrategy)
    bead.state = state.beadState //nice code

    bead.parallelAppliedForce = state.appliedForce
    manBead.state = state.manBeadState
    walls = state.walls

    //based on time constraints, decision was made to not record and playback firedogs + drops, just make sure they clear
    while (raindrops.length > 0)
      raindrops(0).remove()

    while (fireDogs.length > 0)
      fireDogs(0).remove()
  }

  def handleRecordStartedDuringPlayback() = {}

  def getMaxRecordPoints = 1000

  def selectedObject = _selectedObject

  def selectedObject_=(obj: MotionSeriesObject) = {
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
      case o: MutableMotionSeriesObject => {
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

  def bounce = _bounce

  def frictionless = _frictionless

  def walls = _walls

  def walls_=(b: Boolean) = {
    _walls = b
    updateSegmentLengths()
    notifyListeners()
  }

  //duplicates some work with wallrange
  //todo: call this method when ramp angle changes, since it depends on ramp angle
  def updateSegmentLengths() = {
    val seg0Length = if (rampSegments(0).angle > 0 || _walls) rampLength else 10000
    val seg1Length = if (rampSegments(1).angle > 0 || _walls) rampLength else 10000
    setSegmentLengths(seg0Length, seg1Length)
  }

  def setSegmentLengths(seg0Length: Double, seg1Length: Double) = {
    rampSegments(0).startPoint = new Vector2D(-seg0Length, 0)
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

  def setRampAngle(angle: Double) = {
    rampSegments(1).setAngle(angle)
  }

  def getRampAngle = rampSegments(1).angle

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

  private def doStep(dt: Double) = {
    super.setTime(getTime + dt)
    bead.stepInTime(dt)
    for (f <- fireDogs) f.stepInTime(dt)
    for (r <- raindrops) r.stepInTime(dt)
    val rampHeat = bead.getRampThermalEnergy
    rampSegments(0).setHeat(rampHeat)
    rampSegments(1).setHeat(rampHeat)
    rampSegments(0).stepInTime(dt)
    rampSegments(1).stepInTime(dt)
    if (getTime < MotionSeriesDefaults.MAX_RECORD_TIME) {
      val mode = bead.motionStrategy.getMemento
      //println("recording mode: "+mode)
      recordHistory += new DataPoint(getTime, new RecordedState(new RampState(getRampAngle, rampSegments(1).heat, rampSegments(1).wetness),
        selectedObject.state, bead.state, manBead.state, bead.parallelAppliedForce, walls, mode))
    }
    stepListeners.foreach(_())
    notifyListeners() //signify to the Timeline that more data has been added
  }

  override def clearHistory() = {
    super.clearHistory()
    setTime(0.0)
  }

  def update(dt: Double) = {
    val startTime = System.nanoTime
    if (!isPaused) {
      if (isRecord) doStep(dt)
      else if (isPlayback) stepPlayback()
    }
    val endTime = System.nanoTime
    val elapsedTimeMS = endTime - startTime
    elapsedTimeHistory += elapsedTimeMS
    while (elapsedTimeHistory.length > 100) elapsedTimeHistory.remove(0)
    val avg = elapsedTimeHistory.foldLeft(0L)(_ + _) / elapsedTimeHistory.length
    ()
    //    println("elapsed time average (ns) = "+avg)
  }

  def stepRecord(dt: Double) = doStep(dt)
}

trait SurfaceFrictionStrategy {
  def getTotalFriction(objectFriction: Double): Double
}