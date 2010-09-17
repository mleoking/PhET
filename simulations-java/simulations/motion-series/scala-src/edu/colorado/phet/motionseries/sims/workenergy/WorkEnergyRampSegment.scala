package edu.colorado.phet.motionseries.sims.workenergy

import collection.mutable.ArrayBuffer
import edu.colorado.phet.motionseries.model.RampSegment

/**
 * @author Sam Reid
 */

class WorkEnergyRampSegment extends RampSegment(null) {
  private var _wetness = 0.0 // 1.0 means max wetness
  def wetness = _wetness

  import java.lang.Math._
  def dropHit() = setWetness(min(_wetness + 0.1, 1.0))

  def setWetness(w: Double) = {
    _wetness = w
    wetnessListeners.foreach(_())
  }

  def resetWetness() = setWetness(0.0)

  def stepInTime(dt: Double) = {
    setWetness(max(_wetness - 0.01, 0.0))
    heatListeners.foreach(_())
  }

  val wetnessListeners = new ArrayBuffer[() => Unit]
  val heatListeners = new ArrayBuffer[() => Unit]

  private var _heat = 0.0 //Joules
  def heat = _heat

  def setHeat(heat: Double) = {
    _heat = heat
    heatListeners.foreach(_())
  }
}