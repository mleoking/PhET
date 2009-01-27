package edu.colorado.phet.movingman.ladybug.model

import _root_.edu.colorado.phet.common.motion.model.TimeData
import _root_.edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import java.awt.geom.{Rectangle2D, Point2D}
import scala.collection.mutable.ArrayBuffer
import LadybugUtil._
import edu.colorado.phet.common.motion._


class LadybugModel extends ObservableS {
  val ladybug = new Ladybug
  private val history = new ArrayBuffer[DataPoint]
  val tickListeners = new ArrayBuffer[() => Unit]
  private val ladybugMotionModel = new LadybugMotionModel(this)
  private var time: Double = 0;
  var record = true
  var paused = true
  var playbackSpeed = 1.0
  private var bounds = new Rectangle2D.Double(-10, -10, 20, 20)

  case class Sample(time: Double, location: Vector2D)

  private val samplePath = new ArrayBuffer[Sample]

  def addSamplePoint(pt: Point2D) = {
    samplePath += new Sample(time, pt)
    //    println("samplecount=" + samplePath.length)
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

  def setUpdateModePosition = updateMode = positionMode

  def setUpdateModeVelocity = updateMode = velocityMode

  def setUpdateModeAcceleration = updateMode = accelerationMode

  var playbackIndexFloat = 0.0 //floor this to get playbackIndex

  def getPlaybackIndex(): Int = java.lang.Math.floor(playbackIndexFloat).toInt

  def getPlaybackIndexFloat(): Double = playbackIndexFloat

  def getFloatTime(): Double = {
    val f = new LinearFunction(0, history.length - 1, getMinRecordedTime, getMaxRecordedTime)
    f.evaluate(playbackIndexFloat)
  }

  def positionMode(dt: Double) = {
    if (estimateVelocity(history.length - 1).magnitude > 1E-6)
      ladybug.setAngle(estimateAngle())

    val delta = 10
    val index = samplePath.length - delta / 2
    if (samplePath.length > delta)
      ladybug.setPosition(samplePath(index).location)

    def estVel(index: Int,halfRange:Int) = {
      val tx = for (item <- samplePath.slice(index - halfRange, index + halfRange)) yield new TimeData(item.location.getX, item.time)
      val vx = MotionMath.estimateDerivative(tx.toArray)

      val ty = for (item <- samplePath.slice(index - halfRange, index + halfRange)) yield new TimeData(item.location.getY, item.time)
      val vy = MotionMath.estimateDerivative(ty.toArray)

      new Vector2D(vx, vy)
    }

    if (samplePath.length > 20) {
      val ax1 = for (item <- -4 to 4) yield new TimeData(estVel(index+item,4).getX, samplePath(index+item).time)
      val ay1 = for (item <- -4 to 4) yield new TimeData(estVel(index+item,4).getY, samplePath(index+item).time)

      val ax = MotionMath.estimateDerivative(MotionMath.smooth(ax1.toArray,2))
      val ay = MotionMath.estimateDerivative(MotionMath.smooth(ay1.toArray,2))

      ladybug.setVelocity(estVel(index,delta/2))
      ladybug.setAcceleration(new Vector2D(ax,ay))
    }
  }

  def positionModeORIG(dt: Double) = {
    if (estimateVelocity(history.length - 1).magnitude > 1E-6)
      ladybug.setAngle(estimateAngle())

    var velocityEstimate = average(history.length - 3, history.length - 1, estimateVelocity)
    ladybug.setVelocity(velocityEstimate)

    var accelEstimate = average(history.length - 15, history.length - 1, estimateAcceleration)
    ladybug.setAcceleration(accelEstimate)
  }

  def velocityMode(dt: Double) = {
    ladybug.translate(ladybug.getVelocity * dt)

    var accelEstimate = average(history.length - 15, history.length - 1, estimateAcceleration)
    ladybug.setAcceleration(accelEstimate)
  }

  def accelerationMode(dt: Double) = {
    ladybug.translate(ladybug.getVelocity * dt)
    ladybug.setVelocity(ladybug.getVelocity + ladybug.getAcceleration * dt)
  }

  private var updateMode: (Double) => Unit = positionMode

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

        while (getTimeRange > LadybugDefaults.timelineLengthSeconds) {
          history.remove(0)
        }

        if (!ladybugMotionModel.isExclusive()) {
          updateMode(dt)
        }
        notifyListeners()

      } else if (isPlayback()) {
        stepPlayback()
      }
    }
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
      //      setPaused(true)
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
      notifyListeners()
    }
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
    ladybugMotionModel.resetAll()
    playbackIndexFloat = 0.0
    time = 0
    ladybug.resetAll()
    notifyListeners()
  }

  def clearHistory() = {
    history.clear()
    notifyListeners()
  }
}