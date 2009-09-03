package edu.colorado.phet.motionseries

import common.phetcommon.application.Module
import javax.swing.{JFrame, RepaintManager}
import scalacommon.ScalaClock
import model._

//TODO: improve inheritance/composition scheme for different applications/modules/canvases/models
class MotionSeriesModule(frame: JFrame, clock: ScalaClock, name: String, defaultBeadPosition: Double, pausedOnReset: Boolean,
                         initialAngle: Double) extends Module(name, clock) {
  val rampModel = createRampModel(defaultBeadPosition, pausedOnReset, initialAngle)

  def createRampModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) = new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle)

  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel
  val coordinateSystemModel = new AdjustableCoordinateModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListenerByName(if (coordinateSystemModel.fixed) rampModel.coordinateFrameModel.angle = 0)
  private var lastTickTime = System.currentTimeMillis

  //This clock is always running; pausing just pauses the physics
  clock.addClockListener(dt => {
    val paintAndInputTime = System.currentTimeMillis - lastTickTime

    val startTime = System.currentTimeMillis
    rampModel.update(dt)
    RepaintManager.currentManager(getSimulationPanel).paintDirtyRegions()
    val modelTime = System.currentTimeMillis - startTime

    val elapsed = paintAndInputTime + modelTime
    if (elapsed < 25) {
      val toSleep = 25 - elapsed
      //      println("had excess time, sleeping: " + toSleep)
      Thread.sleep(toSleep) //todo: blocks swing event handler thread and paint thread, should run this clock loop in another thread
    }
    lastTickTime = System.currentTimeMillis
  })

  //pause on start/reset, and unpause (and start recording) when the user applies a force
  rampModel.setPaused(true)

  def resetRampModule(): Unit = {
    rampModel.resetAll()
    wordModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
    //pause on startup/reset, and unpause (and start recording) when the user applies a force
    rampModel.setPaused(true)
    resetAll()
  }

  def resetAll() = {}
}