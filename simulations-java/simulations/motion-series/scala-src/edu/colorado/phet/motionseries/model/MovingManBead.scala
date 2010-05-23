package edu.colorado.phet.motionseries.model

import edu.colorado.phet.common.motion.model.TimeData
import edu.colorado.phet.common.motion.MotionMath
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable

//TODO: change from:
//MovingManBead extends ForceBead extends Bead
//to
//MovingManBead extends Bead
//ForceBead extends Bead

object MovingManBead{
  def apply(model:MotionSeriesModel,x: Double, width: Double, height: Double) = {
    new MovingManBead(new BeadState(x, 0, 10, 0, 0, 0.0, 0.0, 0.0), height, width, model.positionMapper, model.rampSegmentAccessor, model.rampChangeAdapter,
      model.surfaceFriction, model.wallsBounce, model.surfaceFrictionStrategy, model.walls, model.wallRange, model.thermalEnergyStrategy)
  }
}

class MovingManBead(_state: BeadState,
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
        extends ForceBead(_state, _height, _width, positionMapper, rampSegmentAccessor, model, surfaceFriction, wallsBounce, __surfaceFrictionStrategy, _wallsExist, wallRange, thermalEnergyStrategy) {

  //todo privatize
  object velocityMode
  object positionMode
  object accelerationMode

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
      //todo: maybe better to estimate 2nd derivative of position instead of 1st derivative of velocity?
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

abstract class MovingManStrategy(bead: ForceBead) extends MotionStrategy(bead) {
  def position2D = bead.positionMapper(bead.position)

  def getAngle = 0.0
}

class PositionMotionStrategy(bead: ForceBead) extends MovingManStrategy(bead) {
  def isCrashed = false

  def getMemento = new MotionStrategyMemento {
    def getMotionStrategy(bead: ForceBead) = new PositionMotionStrategy(bead)
  }

  def stepInTime(dt: Double) = {
    val mixingFactor = 0.5
    //maybe a better assumption is constant velocity or constant acceleration ?
    val dst = bead.desiredPosition * mixingFactor + bead.position * (1 - mixingFactor)
    bead.setPosition(dst) //attempt at filtering

    //todo: move closer to bead computation of acceleration derivatives
    val timeData = for (i <- 0 until java.lang.Math.min(15, bead.stateHistory.length))
    yield new TimeData(bead.stateHistory(bead.stateHistory.length - 1 - i).position, bead.stateHistory(bead.stateHistory.length - 1 - i).time)
    val vel = MotionMath.estimateDerivative(timeData.toArray)
    bead.setVelocity(vel)
    bead.setTime(bead.time + dt)
  }
}

class VelocityMotionStrategy(bead: ForceBead) extends MovingManStrategy(bead) {
  def isCrashed = false

  def getMemento = new MotionStrategyMemento {
    def getMotionStrategy(bead: ForceBead) = new VelocityMotionStrategy(bead)
  }

  def stepInTime(dt: Double) = {
    bead.setPosition(bead.position + bead.velocity * dt)
    bead.setTime(bead.time + dt)
  }
}