package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.common.phetcommon.util.SimpleObserver
import edu.colorado.phet.common.motion.charts.ChartCursor
//TODO: improve inheritance/composition scheme for different applications/modules/canvases/models
class MotionSeriesModule(frame: PhetFrame,
                         val clock: ScalaClock,
                         name: String,
                         defaultBeadPosition: Double,
                         pausedOnReset: Boolean,
                         initialAngle: Double,
                         fbdPopupOnly: Boolean)
        extends Module(name, clock) {
  def createMotionSeriesModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) =
    new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle)

  val motionSeriesModel = createMotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle)

  private def updateCursorVisibility(model: MotionSeriesModel): Unit = {
    model.chartCursor.setVisible(motionSeriesModel.isPlayback && motionSeriesModel.getNumRecordedPoints > 0)
  }
  motionSeriesModel.addObserver(new SimpleObserver {
    def update: Unit = {
      updateCursorVisibility(motionSeriesModel)
    }
  })

  updateCursorVisibility(motionSeriesModel)

  motionSeriesModel.chartCursor.addListener(new ChartCursor.Adapter {
    override def positionChanged: Unit = {
      motionSeriesModel.setTime(motionSeriesModel.chartCursor.getTime)
    }
  })

  val fbdModel = new FreeBodyDiagramModel(fbdPopupOnly)
  val coordinateSystemModel = new AdjustableCoordinateModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListener(() => if (coordinateSystemModel.fixed) motionSeriesModel.coordinateFrameModel.proposedAngle = 0)

  private var lastTickTime = System.currentTimeMillis
  private var clockTickIndex = 0

  //This clock is always running; pausing just pauses the physics
  clock.addClockListener(dt => {
    //    println("Started clock tick: " + clockTickIndex)
    val paintAndInputTime = System.currentTimeMillis - lastTickTime

    val startTime = System.currentTimeMillis
    motionSeriesModel.stepInTime(dt)
    //    RepaintManager.currentManager(getSimulationPanel).paintDirtyRegions() //todo: this still shows clipping of incorrect regions, maybe we need to repaint the entire area
    //    getSimulationPanel.paintImmediately(0, 0, getSimulationPanel.getWidth, getSimulationPanel.getHeight)
    val modelTime = System.currentTimeMillis - startTime

    val elapsed = paintAndInputTime + modelTime
    //this policy causes problems on the mac, see #1832
    //    if (elapsed < 25) {
    //      val toSleep = 25 - elapsed
    //      //      println("had excess time, sleeping: ".literal + toSleep)
    //      Thread.sleep(toSleep) //todo: blocks swing event handler thread and paint thread, should run this clock loop in another thread
    //    }
    lastTickTime = System.currentTimeMillis
    //    println("Ended   clock tick: " + clockTickIndex + ": elapsed time = " + (System.currentTimeMillis - startTime) + " ms")
    clockTickIndex = clockTickIndex + 1
  })

  //pause on start/reset, and unpause (and start recording) when the user applies a force
  def resetPauseValue() = motionSeriesModel.setPaused(true)
  resetPauseValue()

  def resetRampModule(): Unit = {
    motionSeriesModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
    resetPauseValue()
    resetAll()
  }

  def resetAll() = {}

  override def deactivate() = {
    fbdModel.windowed = false //to ensure that fbd dialog doesn't show for this module while user is on a different module
    if (fbdModel.popupDialogOnly) fbdModel.visible = false
    super.deactivate()
  }
}