package edu.colorado.phet.motionseries.model

import edu.colorado.phet.common.motion.model.TimeData
import edu.colorado.phet.common.motion.MotionMath
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable

object ForcesAndMotionObject {
  def apply(model: MotionSeriesModel, x: Double, width: Double, height: Double) = {
    new ForcesAndMotionObject(new MotionSeriesObjectState(x, 0, 0, 10, 0, 0, 0.0, 0.0, 0.0), height, width, model.positionMapper, model.rampSegmentAccessor, model.rampChangeAdapter,
      model.surfaceFriction, model.wallsBounce, model.surfaceFrictionStrategy, model.walls, model.wallRange, model.thermalEnergyStrategy)
  }
}

class ForcesAndMotionObject(_state: MotionSeriesObjectState,
                            _height: Double,
                            _width: Double,
                            positionMapper: Double => Vector2D,
                            rampSegmentAccessor: Double => RampSegment,
                            model: Observable,
                            surfaceFriction: () => Boolean,
                            wallsBounce: () => Boolean,
                            __surfaceFrictionStrategy: SurfaceFrictionStrategy,
                            _wallsExist: => Boolean,
                            wallRange: () => Range,
                            thermalEnergyStrategy: Double => Double)
        extends ForceMotionSeriesObject(_state, _height, _width, positionMapper, rampSegmentAccessor, model, surfaceFriction, wallsBounce, __surfaceFrictionStrategy, _wallsExist, wallRange, thermalEnergyStrategy) {
  private object velocityMode
  private object positionMode
  private object accelerationMode

  private var _mode: AnyRef = accelerationMode

  def mode = _mode

  def setAccelerationMode() = {
    _mode = accelerationMode
  }

  def setVelocityMode() = {
    _mode = velocityMode
    motionStrategy = new VelocityMotionStrategy(this)
  }

  def setPositionMode() = {
    _mode = positionMode
    motionStrategy = new PositionMotionStrategy(this)
  }

  override def acceleration = {
    if (mode == positionMode) {
      if (stateHistory.length <= 3)
        0.0
      else {
        val timeData = for (i <- 0 until java.lang.Math.min(10, stateHistory.length))
        yield new TimeData(stateHistory(stateHistory.length - 1 - i).position, stateHistory(stateHistory.length - 1 - i).time)
        MotionMath.getSecondDerivative(timeData.toArray).getValue
      }
    }
    else if (mode == velocityMode) {
      //I investigated taking 2nd derivative directly, but it seemed easier and more accurate to take 1st derivative of velocity?
      val timeData = for (i <- 0 until java.lang.Math.min(10, stateHistory.length))
      yield new TimeData(stateHistory(stateHistory.length - 1 - i).velocity, stateHistory(stateHistory.length - 1 - i).time)
      MotionMath.estimateDerivative(timeData.toArray)
    }
    else //if (mode == accelerationMode)
      {
        super.acceleration
      }
  }
}

abstract class MovingManStrategy(motionSeriesObject: ForceMotionSeriesObject) extends MotionStrategy(motionSeriesObject) {
  def position2D = motionSeriesObject.positionMapper(motionSeriesObject.position)

  def getAngle = 0.0
}

class PositionMotionStrategy(motionSeriesObject: ForceMotionSeriesObject) extends MovingManStrategy(motionSeriesObject) {
  def isCrashed = false

  def getMemento = new MotionStrategyMemento {
    def getMotionStrategy(motionSeriesObject: ForceMotionSeriesObject) = new PositionMotionStrategy(motionSeriesObject)
  }

  def stepInTime(dt: Double) = {
    val mixingFactor = 0.5
    //maybe a better assumption is constant velocity or constant acceleration ?
    val dst = motionSeriesObject.desiredPosition * mixingFactor + motionSeriesObject.position * (1 - mixingFactor)
    motionSeriesObject.setPosition(dst) //attempt at filtering

    //todo: move closer to MotionSeriesObject computation of acceleration derivatives
    val timeData = for (i <- 0 until java.lang.Math.min(15, motionSeriesObject.stateHistory.length))
    yield new TimeData(motionSeriesObject.stateHistory(motionSeriesObject.stateHistory.length - 1 - i).position, motionSeriesObject.stateHistory(motionSeriesObject.stateHistory.length - 1 - i).time)
    val vel = MotionMath.estimateDerivative(timeData.toArray)
    motionSeriesObject.setVelocity(vel)
    motionSeriesObject.setTime(motionSeriesObject.time + dt)
  }
}

class VelocityMotionStrategy(motionSeriesObject: ForceMotionSeriesObject) extends MovingManStrategy(motionSeriesObject) {
  def isCrashed = false

  def getMemento = new MotionStrategyMemento {
    def getMotionStrategy(motionSeriesObject: ForceMotionSeriesObject) = new VelocityMotionStrategy(motionSeriesObject)
  }

  def stepInTime(dt: Double) = {
    motionSeriesObject.setPosition(motionSeriesObject.position + motionSeriesObject.velocity * dt)
    motionSeriesObject.setTime(motionSeriesObject.time + dt)
  }
}