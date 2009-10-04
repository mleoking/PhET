package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import edu.colorado.phet.common.phetcommon.view.{VerticalLayoutPanel, PhetFrame}
import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.model._
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JDialog, JMenuItem, JFrame, RepaintManager}
import swing.ScalaValueControl

//todo: remove the need for this global, perhaps by overriding PhetFrame
object global{
  var inited = false
}

class MotionSeriesConfigDialog(phetFrame:PhetFrame) extends JDialog(phetFrame,false){
  val layoutPanel = new VerticalLayoutPanel
  layoutPanel.add(new ScalaValueControl(1,100,"tail width","0.0","px",
    ()=>MotionSeriesConfig.VectorTailWidth.value, MotionSeriesConfig.VectorTailWidth.value_=,MotionSeriesConfig.VectorTailWidth.addListener))
  layoutPanel.add(new ScalaValueControl(1,100,"head width","0.0","px",
    ()=>MotionSeriesConfig.VectorHeadWidth.value, MotionSeriesConfig.VectorHeadWidth.value_=,MotionSeriesConfig.VectorHeadWidth.addListener))
  setContentPane(layoutPanel)
  pack()
  SwingUtils.centerWindowOnScreen(this)
}

//TODO: improve inheritance/composition scheme for different applications/modules/canvases/models
class MotionSeriesModule(frame: PhetFrame,
                         clock: ScalaClock,
                         name: String,
                         defaultBeadPosition: Double,
                         pausedOnReset: Boolean,
                         initialAngle: Double,
                         fbdPopupOnly:Boolean)
        extends Module(name, clock) {
  if (!global.inited){
    val item= new JMenuItem("Configure Motion Series")
    item.addActionListener(new ActionListener(){
      def actionPerformed(e: ActionEvent) = new MotionSeriesConfigDialog(frame).setVisible(true)
    })
    frame.getDeveloperMenu.add(item)
    global.inited=true
  }
  def createMotionSeriesModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) =
    new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle)

  val motionSeriesModel = createMotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle)
  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel(fbdPopupOnly)
  val coordinateSystemModel = new AdjustableCoordinateModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListenerByName(if (coordinateSystemModel.fixed) motionSeriesModel.coordinateFrameModel.angle = 0)
  private var lastTickTime = System.currentTimeMillis

  //This clock is always running; pausing just pauses the physics
  clock.addClockListener(dt => {
    val paintAndInputTime = System.currentTimeMillis - lastTickTime

    val startTime = System.currentTimeMillis
    motionSeriesModel.update(dt)
    RepaintManager.currentManager(getSimulationPanel).paintDirtyRegions() //todo: this still shows clipping of incorrect regions, maybe we need to repaint the entire area
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
  motionSeriesModel.setPaused(true)

  def resetRampModule(): Unit = {
    motionSeriesModel.resetAll()
    wordModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
    //pause on startup/reset, and unpause (and start recording) when the user applies a force
    motionSeriesModel.setPaused(true)
    resetAll()
  }

  def resetAll() = {}

  override def deactivate() = {
    fbdModel.windowed = false//to ensure that fbd dialog doesn't show for this module while user is on a different module
    super.deactivate()
  }
}