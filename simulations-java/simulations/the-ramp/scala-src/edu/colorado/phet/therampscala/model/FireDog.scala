package edu.colorado.phet.therampscala.model

import collection.mutable.ArrayBuffer
import common.phetcommon.math.MathUtil
import scalacommon.math.Vector2D
import java.lang.Math._

class FireDog(rampModel: RampModel) {
  val removedListeners = new ArrayBuffer[() => Unit]
  val height = 2
  val width = 2
  val dogbead = rampModel.createBead(-15, height, width)
  private var raindropCount = 0
  private val incomingSpeed = 0.5
  private val outgoingSpeed = 1.0
  private val random = new java.util.Random()
  private val stoppingDist = -5 + random.nextDouble * 5 - 2.5

  def stepInTime(dt: Double) = {
    if (dogbead.position < stoppingDist && raindropCount < rampModel.maxDrops) {
      dogbead.setPosition(dogbead.position + incomingSpeed)
    } else if (raindropCount < rampModel.maxDrops) {
      val raindrop = new Raindrop(dogbead.position2D + new Vector2D(width / 2.0, height / 3.0), 10 + random.nextGaussian * 3, PI / 4 + random.nextGaussian() * PI / 16, rampModel)
      rampModel.raindrops += raindrop
      raindropCount = raindropCount + 1
      rampModel.raindropAddedListeners.foreach(_(raindrop))
    } else if (dogbead.position > -15) {
      dogbead.setPosition(dogbead.position - outgoingSpeed)
    } else {
      remove()
    }
  }

  def remove() = {
    rampModel.fireDogs -= this
    removedListeners.foreach(_())
  }
}

class Raindrop(p: Vector2D, rainSpeed: Double, angle: Double, rampModel: RampModel) {
  val removedListeners = new ArrayBuffer[() => Unit]
  val rainbead = rampModel.createBead(0.0, 0.3, 0.5)
  private var _angle = 0.0
  rainbead.motionStrategy = new Airborne(p, new Vector2D(angle) * rainSpeed, 0.0, rainbead) {
    override def getAngle = velocity2D.getAngle + PI / 2
  }
  def stepInTime(dt: Double) = {
    val origPosition = rainbead.position2D
    rainbead.stepInTime(dt)
    val didCrash = rainbead.motionStrategy match {
      case x: Crashed => true
      case _ => false
    }
    val newPosition = rainbead.position2D
    import scalacommon.Predef._
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