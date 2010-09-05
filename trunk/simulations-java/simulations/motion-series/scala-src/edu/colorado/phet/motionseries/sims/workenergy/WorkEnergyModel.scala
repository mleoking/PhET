package edu.colorado.phet.motionseries.sims.workenergy

import collection.mutable.ArrayBuffer
import edu.colorado.phet.motionseries.model.{RecordedState, MotionSeriesModel, FireDog, Raindrop}

/**
 * @author Sam Reid
 */


class WorkEnergyModel(defaultPosition: Double,
                        pausedOnReset: Boolean,
                        initialAngle: Double) extends MotionSeriesModel(defaultPosition,pausedOnReset,initialAngle){

  val raindrops = new ArrayBuffer[Raindrop]
  val fireDogs = new ArrayBuffer[FireDog]
  val fireDogAddedListeners = new ArrayBuffer[FireDog => Unit]
  val raindropAddedListeners = new ArrayBuffer[Raindrop => Unit]
  private var totalThermalEnergyOnClear = 0.0
  val maxDrops = (60 * 0.75).toInt
  
  
    /**
     * Instantly clear the heat from the ramps.
     */
    def clearHeatInstantly() {
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
      val reducedEnergy = totalThermalEnergyOnClear / (maxDrops / 2.0)
      motionSeriesObject.thermalEnergy = motionSeriesObject.thermalEnergy - reducedEnergy
      motionSeriesObject.crashEnergy = java.lang.Math.max(motionSeriesObject.crashEnergy - reducedEnergy, 0)
      if (motionSeriesObject.thermalEnergy < 1) motionSeriesObject.thermalEnergy = 0.0
    }


  override def setPlaybackState(state: RecordedState) = {
    super.setPlaybackState(state)
    //based on time constraints, decision was made to not record and playback firedogs + drops, just make sure they clear
    while (raindrops.length > 0)
      raindrops(0).remove()

    while (fireDogs.length > 0)
      fireDogs(0).remove()
  }

  override def stepRecord(dt:Double) = {
    super.stepRecord()
    for (f <- fireDogs) f.stepInTime(dt)
    for (r <- raindrops) r.stepInTime(dt)
    val rampHeat = motionSeriesObject.rampThermalEnergy
  }

  override def frictionless_=(b: Boolean) = {
    super.frictionless_=(b)
    if (b) clearHeatInstantly()
  }
}