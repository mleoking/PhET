package edu.colorado.phet.movingman.ladybug.model

import edu.colorado.phet.common.motion.model.TimeData
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import edu.colorado.phet.movingman.util.Motion2DModel
import java.awt.geom.{Rectangle2D, Point2D}
import scala.collection.mutable.ArrayBuffer
import LadybugUtil._
import edu.colorado.phet.common.motion._


class LadybugModel extends Observable {
  val ladybug = new Ladybug
  private val history = new ArrayBuffer[DataPoint]

  private val ladybugMotionModel = new LadybugMotionModel(this)
  private var time: Double = 0;
  var record = true
  var paused = true
  var playbackSpeed = 1.0

  private var bounds = new Rectangle2D.Double(-10, -10, 20, 20)
  private var updateMode: UpdateMode = PositionMode

  case class Sample(time: Double, location: Vector2D)
  val samplePath = new ArrayBuffer[Sample]
  var samplePoint = new Vector2D //current sample point
  var penDown = false

  private var frictionless = false

  val tickListeners = new ArrayBuffer[() => Unit]
  val motion2DModelResetListeners = new ArrayBuffer[() => Unit]

  val motion2DModel = new Motion2DModel(10, 5, LadybugDefaults.defaultLocation.x, LadybugDefaults.defaultLocation.y)
  var playbackIndexFloat = 0.0 //floor this to get playbackIndex

  def isFrictionless = frictionless

  def setFrictionless(f: Boolean) = {
    frictionless = f
    clearSampleHistory
    resetMotion2DModel
    samplePoint = ladybug.getPosition
    notifyListeners
  }

  def setSamplePoint(pt: Point2D) = {
    this.samplePoint = pt
  }

  def getBounds(): Rectangle2D = {
    return new Rectangle2D.Double(bounds.getX, bounds.getY, bounds.getWidth, bounds.getHeight) //defensive copy
  }

  def setBounds(b: Rectangle2D) = {
    bounds.setRect(b.getX, b.getY, b.getWidth, b.getHeight)
  }

  def getLadybugMotionModel() = ladybugMotionModel

  def getTime() = time

  def getMaxRecordedTime() = if (history.length == 0) 0.0 else history(history.length - 1).time

  def getMinRecordedTime() = if (history.length == 0) 0.0 else history(0).time

  def setPlaybackTime(t: Double) = {
    val f = new LinearFunction(getMinRecordedTime, getMaxRecordedTime, 0, history.length - 1)
    setPlaybackIndexFloat(f.evaluate(t))
  }

  abstract class UpdateMode {
    def update(dt: Double): Unit
  }
  object PositionMode extends UpdateMode {
    def update(dt: Double) = {
      positionMode(dt)
    }
  }
  object VelocityMode extends UpdateMode {
    def update(dt: Double) = {
      velocityMode(dt)
    }
  }
  object AccelerationMode extends UpdateMode {
    def update(dt: Double) = {
      accelerationMode(dt)
    }
  }
  def setUpdateModePosition = {
    if (updateMode != PositionMode) {
      updateMode = PositionMode
      clearSampleHistory
      resetMotion2DModel
    }
  }

  def setUpdateModeVelocity = {
    if (updateMode != VelocityMode) {
      updateMode = VelocityMode
    }
  }

  def setUpdateModeAcceleration = {
    updateMode = AccelerationMode
  }

  def getPlaybackIndex(): Int = java.lang.Math.floor(playbackIndexFloat).toInt

  def getPlaybackIndexFloat(): Double = playbackIndexFloat

  def getFloatTime(): Double = {
    val f = new LinearFunction(0, history.length - 1, getMinRecordedTime, getMaxRecordedTime)
    f.evaluate(playbackIndexFloat)
  }

  private def getLastSamplePoint = samplePath(samplePath.length - 1)

  //  println("t\tx\tvx\tax")
  def positionMode(dt: Double) = {
    //    println("pendown=" + penDown)
    if (frictionless && !penDown) {
      velocityMode(dt)
      if (samplePath.length > 2) {
        samplePoint = ladybug.getPosition
        samplePath += new Sample(time, samplePoint)
        motion2DModel.addPointAndUpdate(getLastSamplePoint.location.x, getLastSamplePoint.location.y)
      }
    }
    else {
      if (samplePath.length > 2) {
        motion2DModel.addPointAndUpdate(getLastSamplePoint.location.x, getLastSamplePoint.location.y)
        ladybug.setPosition(new Vector2D(motion2DModel.getAvgXMid, motion2DModel.getAvgYMid))
        //added fudge factors for getting the scale right with current settings of motion2d model
        //used spreadsheet to make sure model v and a are approximately correct.
        val vscale = (1.0 / dt) / 10
        val ascale = vscale * vscale * 3.835
        ladybug.setVelocity(new Vector2D(motion2DModel.getXVel, motion2DModel.getYVel) * vscale)
        ladybug.setAcceleration(new Vector2D(motion2DModel.getXAcc, motion2DModel.getYAcc) * ascale)

        //      def debug = {println(time + "\t" + ladybug.getPosition.x + "\t" + ladybug.getVelocity.x + "\t" + ladybug.getAcceleration.x)}
        //      debug
        //      0+1
        //      println("y="+ladybug.getPosition.y)

      } else {
        ladybug.setVelocity(new Vector2D)
        ladybug.setAcceleration(new Vector2D)
      }
      if (estimateVelocity(history.length - 1).magnitude > 1E-6)
        ladybug.setAngle(estimateAngle())
    }
  }

  def velocityMode(dt: Double) = {
    if (samplePath.length > 0)
      motion2DModel.addPointAndUpdate(getLastSamplePoint.location.x, getLastSamplePoint.location.y)
    ladybug.translate(ladybug.getVelocity * dt)

    var accelEstimate = average(history.length - 15, history.length - 1, estimateAcceleration)
    ladybug.setAcceleration(accelEstimate)
  }

  def accelerationMode(dt: Double) = {
    ladybug.translate(ladybug.getVelocity * dt)
    ladybug.setVelocity(ladybug.getVelocity + ladybug.getAcceleration * dt)
  }

  def setStateToPlaybackIndex() = {
    ladybug.setState(history(getPlaybackIndex()).state)
    time = history(getPlaybackIndex).time
  }

  def getHistory() = history

  def getTimeRange(): Double = {
    if (history.length == 0) {
      0
    } else {
      history(history.length - 1).time - history(0).time
    }
  }

  def update(dt: Double) = {
    if (!paused) {
      tickListeners.foreach(_())
      if (isRecord()) {
        time += dt;
        ladybugMotionModel.update(dt, this)

        history += new DataPoint(time, ladybug.getState)
        samplePath += new Sample(time, samplePoint)

        while (getTimeRange > LadybugDefaults.timelineLengthSeconds) {
          history.remove(0)
          while (samplePath.length > history.length)
            samplePath.remove(0)
        }

        if (!ladybugMotionModel.isExclusive()) {
          if (penDown) {
            PositionMode.update(dt)
          }
          else {
            updateMode.update(dt)
          }
        }
        notifyListeners()

      } else if (isPlayback()) {
        stepPlayback()
      }
    }
  }

  def initManual = {
    println("init: " + ladybug.getPosition)
    resetMotion2DModel
    samplePath.clear
    println("cleared sample path: " + samplePath.length)
  }

  def readyForInteraction(): Boolean = {
    val recording = isRecord
    val isDonePlayback = (getPlaybackIndex() >= history.length - 1) && isPaused
    recording || isDonePlayback
  }

  def stepPlayback() = {
    if (getPlaybackIndex() < history.length) {
      setStateToPlaybackIndex()
      time = history(getPlaybackIndex()).time
      playbackIndexFloat = playbackIndexFloat + playbackSpeed
      notifyListeners()
    } else {
      if (LadybugDefaults.recordAtEndOfPlayback) {
        setRecord(true)
      }

      if (LadybugDefaults.pauseAtEndOfPlayback) {
        setPaused(true)
      }
    }
  }

  def estimateAngle(): Double = estimateVelocity(history.length - 1).getAngle

  def getPosition(index: Int): Vector2D = {
    history(index).state.position
  }

  def estimateVelocity(index: Int): Vector2D = {
    val h = history.slice(history.length - 6, history.length)
    val tx = for (item <- h) yield new TimeData(item.state.position.x, item.time)
    val vx = MotionMath.estimateDerivative(tx.toArray)

    val ty = for (item <- h) yield new TimeData(item.state.position.y, item.time)
    val vy = MotionMath.estimateDerivative(ty.toArray)

    new Vector2D(vx, vy)
  }

  def estimateAcceleration(index: Int): Vector2D = {
    val h = history.slice(history.length - 6, history.length)
    val tx = for (item <- h) yield new TimeData(item.state.velocity.x, item.time)
    val ax = MotionMath.estimateDerivative(tx.toArray)

    val ty = for (item <- h) yield new TimeData(item.state.velocity.y, item.time)
    val ay = MotionMath.estimateDerivative(ty.toArray)

    new Vector2D(ax, ay)
  }

  def average(start: Int, end: Int, function: Int => Vector2D): Vector2D = {
    var sum = new Vector2D
    for (i <- start until end) {
      sum = sum + function(i)
    }
    sum / (end - start)
  }

  def isPlayback() = !record

  def isRecord() = record

  def setRecord(rec: Boolean) = {
    if (record != rec) {
      record = rec
      if (record) {
        clearHistoryRemainder
      }

      notifyListeners()
    }
  }

  def clearHistoryRemainder = {
    val earlyEnough = history.filter(_.time < time)
    history.clear
    history.appendAll(earlyEnough)
    clearSampleHistory()
    resetMotion2DModel
  }

  def setPlaybackSpeed(speed: Double) = {
    if (speed != playbackSpeed) {
      playbackSpeed = speed
      notifyListeners()
    }
  }

  def setPlayback(speed: Double) = {
    setPlaybackSpeed(speed)
    setRecord(false)
  }

  def setPaused(p: Boolean) = {
    if (paused != p) {
      paused = p
      notifyListeners()
    }
  }

  def isPaused() = paused

  def rewind = {
    setPlaybackIndexFloat(0.0)
  }

  def setPlaybackIndexFloat(index: Double) = {
    playbackIndexFloat = index
    setStateToPlaybackIndex()
    notifyListeners()
  }

  def startRecording() = {
    getLadybugMotionModel.motion = LadybugMotionModel.MANUAL
    setRecord(true)
    setPaused(false)
  }

  def resetAll() = {
    record = true
    paused = true
    playbackSpeed = 1.0
    history.clear
    samplePath.clear

    ladybugMotionModel.resetAll()
    playbackIndexFloat = 0.0
    time = 0
    ladybug.resetAll()
    frictionless = false
    resetMotion2DModel

    notifyListeners()
  }

  def clearHistory() = {
    history.clear()
    samplePath.clear()
    notifyListeners()
  }

  def clearSampleHistory() = samplePath.clear

  def resetMotion2DModel = {
    motion2DModel.reset(ladybug.getPosition.x, ladybug.getPosition.y)
    motion2DModelResetListeners.foreach(_())
  }

  def returnLadybug = {
    ladybug.setPosition(LadybugDefaults.defaultLocation)
    ladybug.setVelocity(new Vector2D)
    samplePath.clear
    setSamplePoint(ladybug.getPosition)
    resetMotion2DModel
    notifyListeners
  }

  def setPenDown(p: Boolean) = {
    penDown = p
    notifyListeners
  }
}