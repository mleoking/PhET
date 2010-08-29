package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.common.phetcommon.util.SimpleObserver
import edu.colorado.phet.common.motion.charts.{TemporalChart, ChartCursor}
import edu.colorado.phet.motionseries.Predef._
import graphics.MotionSeriesCanvas
import javax.swing.RepaintManager
//TODO: improve inheritance/composition scheme for different applications/modules/canvases/models
class MotionSeriesModule(frame: PhetFrame,
                         val clock: ScalaClock,
                         name: String,
                         defaultObjectPosition: Double,
                         pausedOnReset: Boolean,
                         initialAngle: Double,
                         fbdPopupOnly: Boolean)
        extends Module(name, clock) {
  TemporalChart.SEC_TEXT = "units.sec".translate; //see doc in SEC_TEXT
  def createMotionSeriesModel(defaultObjectPosition: Double, pausedOnReset: Boolean, initialAngle: Double) =
    new MotionSeriesModel(defaultObjectPosition, pausedOnReset, initialAngle)

  val motionSeriesModel = createMotionSeriesModel(defaultObjectPosition, pausedOnReset, initialAngle)

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
    //TODO: comment out debug info and frame rates
    val t = System.currentTimeMillis
    val delta = t-lastTickTime
    println("delta = \t"+delta)
    val paintAndInputTime = System.currentTimeMillis - lastTickTime

    val startTime = System.currentTimeMillis
    motionSeriesModel.stepInTime(dt)

    //There is a bug that the instantantaneous (one frame) wall force is sometimes not shown or is clipped incorrectly
    //This workaround reduces the probability of having that problem significantly
    //The root of the problem might be that the wall force vector isn't updating at the right times
    //Note that this workaround will increase computational demand, and it will also occur whenever the user is pushing the block against the wall
//    if (motionSeriesModel.motionSeriesObject.wallForce.magnitude > 1E-2) {
//      getSimulationPanel.paintImmediately(0, 0, getSimulationPanel.getWidth, getSimulationPanel.getHeight)
//      RepaintManager.currentManager(getSimulationPanel).paintDirtyRegions()
//    }
//    getSimulationPanel.asInstanceOf[MotionSeriesCanvas].doPaintImmediately()
    val modelTime = System.currentTimeMillis - startTime

    val elapsed = paintAndInputTime + modelTime
    //this policy causes problems on the mac, see #1832
    lastTickTime = t
    clockTickIndex = clockTickIndex + 1
  })
  //This was an investigation into active rendering
  //    var lastTime = System.nanoTime
  //    val t = new Thread(new Runnable() {
  //      def run = {
  //        while (true) {
  //          val t = System.nanoTime
  //          //        SwingUtilities.invokeAndWait(new Runnable(){
  //          //          def run = {
  //          val dt = (t - lastTime) / 1000
  //  //        println(dt+"\t"+t+"\t"+motionSeriesModel.motionSeriesObject.position)
  //          val measure1 = System.currentTimeMillis
  //          SwingUtilities.invokeAndWait(new Runnable() {
  //            def run = {
  //              motionSeriesModel.stepInTime(MotionSeriesDefaults.DT_DEFAULT)
  //              if (getSimulationPanel != null)
  //                getSimulationPanel.asInstanceOf[MotionSeriesCanvas].doPaintImmediately()
  //            }
  //          })
  //          val measure2 = System.currentTimeMillis
  //          val diff = measure2 - measure1
  //          val targetDT = 15
  //          if (diff < targetDT) {
  //            Thread.sleep(targetDT - diff)
  //          }
  //          lastTime=t
  //        }
  //      }
  //    })
  //    t.start()

  motionSeriesModel.motionSeriesObject.addWallCrashListener(() => MotionSeriesResources.crashSound.play())
  motionSeriesModel.motionSeriesObject.addBounceListener(() => MotionSeriesResources.bounceSound.play())
  motionSeriesModel.motionSeriesObject.crashListeners += (() => MotionSeriesResources.crashSound.play())

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