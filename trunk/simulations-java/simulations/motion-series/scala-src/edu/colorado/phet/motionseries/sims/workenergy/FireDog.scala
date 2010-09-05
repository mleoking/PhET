package edu.colorado.phet.motionseries.model

import collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.math.MathUtil
import edu.colorado.phet.scalacommon.math.Vector2D
import java.lang.Math._
import edu.colorado.phet.motionseries.sims.workenergy.WorkEnergyModel

/**
 * The fire dog is the character that appears when the user presses "clear heat" to remove heat from the ramp and object
 * by spraying it with a fire hose.
 *
 * @author Sam Reid
 */
class FireDog(model: WorkEnergyModel) {
  val removalListeners = new ArrayBuffer[() => Unit]
  val height = 2
  val width = 2
  val dog = new MotionSeriesObject(model, -15, height, width)
  private var raindropCount = 0
  private val incomingSpeed = 0.5 * 1.25
  private val outgoingSpeed = 1.0 * 1.25
  private val random = new java.util.Random()
  private val stoppingDist = -5 + random.nextDouble * 5 - 2.5

  def stepInTime(dt: Double) = {
    if (dog.position < stoppingDist && raindropCount < model.maxDrops) {
      dog.setPosition(dog.position + incomingSpeed)
    } else if (raindropCount < model.maxDrops) {
      val raindrop = new Raindrop(dog.position2D + new Vector2D(width / 2.0, height / 3.0), 10 + random.nextGaussian * 3, PI / 4 + random.nextGaussian() * PI / 16, model)
      model.raindrops += raindrop
      raindropCount = raindropCount + 1
      model.raindropAddedListeners.foreach(_(raindrop))
    } else if (dog.position > -15) {
      dog.setPosition(dog.position - outgoingSpeed)
    } else {
      remove()
    }
  }

  def remove() = {
    model.fireDogs -= this
    removalListeners.foreach(_())
  }
}

class Raindrop(p: Vector2D, rainSpeed: Double, angle: Double, rampModel: WorkEnergyModel) {
  val removedListeners = new ArrayBuffer[() => Unit]
  val motionSeriesObject = new MotionSeriesObject(rampModel, 0.0, 0.3, 0.5)
  private var _angle = 0.0
  motionSeriesObject.setVelocity(rainSpeed) //have to set the speed here so that energy conservation in Airborne.step won't make the water drops appear underground
  motionSeriesObject.motionStrategy = new Airborne(p, new Vector2D(angle) * rainSpeed, 0.0, motionSeriesObject) {
    override def getAngle = velocity2D.angle + PI / 2
  }
  def stepInTime(dt: Double) = {
    val origPosition = motionSeriesObject.position2D
    motionSeriesObject.stepInTime(dt)
    val didCrash = motionSeriesObject.motionStrategy match {
      case x: Crashed => true
      case _ => false
    }
    val newPosition = motionSeriesObject.position2D
    import edu.colorado.phet.scalacommon.Predef._
    val intersection = MathUtil.getLineSegmentsIntersection(origPosition, newPosition, rampModel.rampSegments(1).startPoint, rampModel.rampSegments(1).endPoint)
    val hitRamp = if (java.lang.Double.isNaN(intersection.x) || java.lang.Double.isNaN(intersection.y)) false else true
    if (hitRamp || didCrash) {
      remove()
      rampModel.rainCrashed()
    }
  }

  def remove() = {
    rampModel.raindrops -= this
    removedListeners.foreach(_())
  }
}