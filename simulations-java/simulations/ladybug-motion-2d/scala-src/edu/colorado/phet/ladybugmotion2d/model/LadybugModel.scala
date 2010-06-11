package edu.colorado.phet.ladybugmotion2d.model

import _root_.java.awt.geom.{Point2D, Rectangle2D}
import edu.colorado.phet.common.motion.model.TimeData
import scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.common.motion._
import edu.colorado.phet.ladybugmotion2d.{Motion2DModel, LadybugDefaults}
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.recordandplayback.model.{DataPoint, RecordAndPlaybackModel}
import edu.colorado.phet.scalacommon.util.Observable

/**
 * This class is the main model for Ladybug2DApplication.  It contains both a model for the current state as well as the history.
 * The smoothing of motion is done by leading the ladybug (with an abstraction called the pen),
 * and using the same model as Motion2D for interpolation.
 */
class LadybugModel extends RecordAndPlaybackModel[LadybugState]((LadybugDefaults.timelineLengthSeconds / LadybugDefaults.defaultDT).toInt) with Observable {
  def stepRecording(simulationTimeChange: Double) = stepRecord(LadybugDefaults.defaultDT)

  val ladybug = new Ladybug
  private val ladybugMotionModel = new LadybugMotionModel(this)
  private var bounds = new Rectangle2D.Double(-10, -10, 20, 20)
  private var updateMode: UpdateMode = PositionMode
  val tickListeners = new ArrayBuffer[() => Unit]
  val resetListeners = new ArrayBuffer[() => Unit]
  private var frictionless = false
  val motion2DModel = new Motion2DModel(10, 5, LadybugDefaults.defaultLocation.x, LadybugDefaults.defaultLocation.y)
  private val modelHistory = new ArrayBuffer[DataPoint[LadybugState]] //recent history used to compute velocities, etc.
  var dt = 0.0

  //samples inputted from the user that will be used to determine the path of the object
  //imagine as a pen on the canvas that leads the ladybug
  case class PenSample(time: Double, location: Vector2D)
  val penPath = new ArrayBuffer[PenSample]
  private var penPoint = new Vector2D //current sample point
  private var penDown = false

  def isFrictionless = frictionless

  def setFrictionless(f: Boolean) = {
    if (frictionless != f) {
      frictionless = f
      if (!frictionless) {
        //todo: make bug come to a smooth stop
        //todo: maybe easiest way is to refactor friction implementation to be more physical and less architectural
        clearSampleHistory
        resetMotion2DModel
        penPoint = ladybug.getPosition
      }
      else {
        clearSampleHistory
        resetMotion2DModel
        penPoint = ladybug.getPosition
      }
      notifyObservers()
    }
  }

  def setSamplePoint(pt: Point2D) = {
    this.penPoint = pt
    //todo: send notification?
  }

  def getBounds: Rectangle2D = {
    return new Rectangle2D.Double(bounds.getX, bounds.getY, bounds.getWidth, bounds.getHeight) //defensive copy
  }

  def setBounds(b: Rectangle2D) = {
    bounds.setRect(b.getX, b.getY, b.getWidth, b.getHeight)
  }

  def getLadybugMotionModel() = ladybugMotionModel

  abstract class UpdateMode {def update(dt: Double): Unit}
  object PositionMode extends UpdateMode {def update(dt: Double) = positionMode(dt)}
  object VelocityMode extends UpdateMode {def update(dt: Double) = velocityMode(dt)}
  object AccelerationMode extends UpdateMode {def update(dt: Double) = accelerationMode(dt)}

  def setUpdateModePosition() = {
    if (updateMode != PositionMode) {
      updateMode = PositionMode
      clearSampleHistory
      resetMotion2DModel
    }
  }

  def setUpdateModeVelocity() = {
    if (updateMode != VelocityMode) {
      updateMode = VelocityMode
    }
  }

  def setUpdateModeAcceleration() = {
    updateMode = AccelerationMode
  }

  private def getLastSamplePoint = penPath(penPath.length - 1)

  //  println("t\tx\tvx\tax")
  def positionMode(dt: Double) = {
    //    println("pendown=" + penDown)
    if (frictionless && !penDown) {
      velocityMode(dt)
      if (penPath.length > 2) {
        penPoint = ladybug.getPosition
        penPath += new PenSample(getTime, penPoint)
        motion2DModel.addPointAndUpdate(getLastSamplePoint.location.x, getLastSamplePoint.location.y)
      }
    }
    else {
      if (penPath.length > 2) {
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
      pointInDirectionOfMotion()
    }
  }

  def pointInDirectionOfMotion() = {
    if (estimateVelocity(modelHistory.length - 1).magnitude > 1E-6)
      ladybug.setAngle(estimateAngle())
  }

  def velocityMode(dt: Double) = {
    if (penPath.length > 0)
      motion2DModel.addPointAndUpdate(getLastSamplePoint.location.x, getLastSamplePoint.location.y)
    ladybug.translate(ladybug.getVelocity * dt)

    var accelEstimate = average(modelHistory.length - 15, modelHistory.length - 1, estimateAcceleration)
    ladybug.setAcceleration(accelEstimate)
    pointInDirectionOfMotion()
  }

  def accelerationMode(dt: Double) = {
    ladybug.translate(ladybug.getVelocity * dt)
    ladybug.setVelocity(ladybug.getVelocity + ladybug.getAcceleration * dt)
    pointInDirectionOfMotion()
  }

  def setPlaybackState(state: LadybugState) = ladybug.setState(state)

  def stepRecord(dt: Double) = {
    tickListeners.foreach(_())
    if (isRecord()) {
      ladybugMotionModel.update(dt, this)

      modelHistory += new DataPoint(getTime, ladybug.getState)
      //      addRecordedPoint(new DataPoint(time, ladybug.getState))
      penPath += new PenSample(getTime, penPoint)

      while (modelHistory.length > 100) {
        modelHistory.remove(0)
      }
      while (penPath.length > 100) {
        penPath.remove(0)
      }

      while (getNumRecordedPoints > getMaxRecordPoints) {
        //decide whether to remove end of path or beginning of path.
        //          recordHistory.remove(recordHistory.length - 1)
        removeHistoryPoint(0)
      }

      if (!ladybugMotionModel.isExclusive()) {
        if (penDown) {
          PositionMode.update(dt)
        }
        else {
          updateMode.update(dt)
        }
      }
      notifyObservers()
    }
    ladybug.getState
  }

  def initManual = {
    resetMotion2DModel
    penPath.clear
  }

  def readyForInteraction(): Boolean = {
    val recording = isRecord
    val isDonePlayback = (getTime >= getMaxRecordedTime - 1) && isPaused
    recording || isDonePlayback
  }

  def estimateAngle(): Double = estimateVelocity(modelHistory.length - 1).angle

  def getPosition(index: Int): Vector2D = modelHistory(index).getState.position

  def estimateVelocity(index: Int): Vector2D = {
    val h = modelHistory.slice(modelHistory.length - 6, modelHistory.length)
    val tx = for (item <- h) yield new TimeData(item.getState.position.x, item.getTime)
    val vx = MotionMath.estimateDerivative(tx.toArray)

    val ty = for (item <- h) yield new TimeData(item.getState.position.y, item.getTime)
    val vy = MotionMath.estimateDerivative(ty.toArray)

    new Vector2D(vx, vy)
  }

  def estimateAcceleration(index: Int): Vector2D = {
    val h = modelHistory.slice(modelHistory.length - 6, modelHistory.length)
    val tx = for (item <- h) yield new TimeData(item.getState.velocity.x, item.getTime)
    val ax = MotionMath.estimateDerivative(tx.toArray)

    val ty = for (item <- h) yield new TimeData(item.getState.velocity.y, item.getTime)
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

  override def handleRecordStartedDuringPlayback() {
    ladybug.setVelocity(new Vector2D)
    ladybug.setAcceleration(new Vector2D)
  }

  override def clearHistoryRemainder() = {
    super.clearHistoryRemainder()

    val earlyEnough = modelHistory.filter(_.getTime < getTime)
    modelHistory.clear
    modelHistory.appendAll(earlyEnough)

    clearSampleHistory()
    resetMotion2DModel
  }

  override def startRecording() = {
    super.startRecording()
    getLadybugMotionModel.motion = LadybugMotionModel.MANUAL
  }

  override def resetAll() = {
    super.resetAll()

    modelHistory.clear()
    penPath.clear()

    ladybugMotionModel.resetAll()

    ladybug.resetAll()
    frictionless = false
    resetMotion2DModel

    notifyObservers()
  }

  override def clearHistory() = {
    modelHistory.clear()
    penPath.clear()

    super.clearHistory() //do super last to call notifyListeners
  }

  def clearSampleHistory() = penPath.clear()

  def resetMotion2DModel() = {
    motion2DModel.reset(ladybug.getPosition.x, ladybug.getPosition.y)
    resetListeners.foreach(_())
  }

  def returnLadybug() = {
    ladybug.setPosition(LadybugDefaults.defaultLocation)
    ladybug.setVelocity(new Vector2D)
    penPath.clear()
    setSamplePoint(ladybug.getPosition)
    resetMotion2DModel
    notifyObservers()
  }

  def setPenDown(p: Boolean) = {
    penDown = p
    notifyObservers()
  }
}