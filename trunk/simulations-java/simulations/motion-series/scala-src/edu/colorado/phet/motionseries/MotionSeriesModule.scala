package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.view.{PhetFrame}
import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.model._
import javax.swing.{RepaintManager}

//TODO: improve inheritance/composition scheme for different applications/modules/canvases/models
class MotionSeriesModule(frame: PhetFrame,
                         clock: ScalaClock,
                         name: String,
                         defaultBeadPosition: Double,
                         pausedOnReset: Boolean,
                         initialAngle: Double,
                         fbdPopupOnly: Boolean)
        extends Module(name, clock) {
  def createMotionSeriesModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) =
    new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle)

  val motionSeriesModel = createMotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle)
  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel(fbdPopupOnly)
  val coordinateSystemModel = new AdjustableCoordinateModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListenerByName(if (coordinateSystemModel.fixed) motionSeriesModel.coordinateFrameModel.proposedAngle = 0)
  private var lastTickTime = System.currentTimeMillis

  //This clock is always running; pausing just pauses the physics
  clock.addClockListener(dt => {
    val paintAndInputTime = System.currentTimeMillis - lastTickTime

    val startTime = System.currentTimeMillis
    motionSeriesModel.stepInTime(dt)
    RepaintManager.currentManager(getSimulationPanel).paintDirtyRegions() //todo: this still shows clipping of incorrect regions, maybe we need to repaint the entire area
    val modelTime = System.currentTimeMillis - startTime

    val elapsed = paintAndInputTime + modelTime
    //this policy causes problems on the mac, see #1832
    //    if (elapsed < 25) {
    //      val toSleep = 25 - elapsed
    //      //      println("had excess time, sleeping: ".literal + toSleep)
    //      Thread.sleep(toSleep) //todo: blocks swing event handler thread and paint thread, should run this clock loop in another thread
    //    }
    lastTickTime = System.currentTimeMillis
  })

  //pause on start/reset, and unpause (and start recording) when the user applies a force
  def initPauseValue() = motionSeriesModel.setPaused(true)
  initPauseValue()

  def resetRampModule(): Unit = {
    motionSeriesModel.resetAll()
    wordModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
    initPauseValue()
    resetAll()
  }

  def resetAll() = {}

  override def deactivate() = {
    fbdModel.windowed = false //to ensure that fbd dialog doesn't show for this module while user is on a different module
    if (fbdModel.popupDialogOnly) fbdModel.visible = false
    super.deactivate()
  }
}