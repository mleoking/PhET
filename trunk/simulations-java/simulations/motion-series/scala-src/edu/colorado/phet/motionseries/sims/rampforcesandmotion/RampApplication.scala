package edu.colorado.phet.motionseries.sims.rampforcesandmotion

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.motionseries.graphics.RampCanvas
import java.awt.geom.Rectangle2D
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication

import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.recordandplayback.gui.RecordAndPlaybackControlPanel
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationLauncher, PhetApplicationConfig}
import edu.colorado.phet.motionseries.controls.MotionSeriesControlPanel
import edu.colorado.phet.motionseries.sims.rampforcesandmotion.robotmovingcompany.RobotMovingCompanyModule
import edu.colorado.phet.motionseries.{StageContainerArea, MotionSeriesDefaults, MotionSeriesModule}
import edu.colorado.phet.motionseries.charts.RampForceChartNode

/**
 * This is the parent class for the various Modules for the ramp simulation.
 * It has many parameters since the application has many tabs with different requirements.
 * @author Sam Reid
 */
class BasicRampModule(frame: PhetFrame,
                      name: String, //the name of the module
                      coordinateSystemEnabled: Boolean, //true if the coordinate frame should be shown
                      objectComboBoxEnabled: Boolean, //true if the objects should be selectable via combo box in the play area
                      showAppliedForceSlider: Boolean, //true if the applied force should be shown as a slider in the play area
                      initialPosition: Double, //the start location of the MotionSeriesObject
                      pausedOnReset: Boolean,
                      initialAngle: Double,
                      rampLayoutArea: Rectangle2D,
                      stageContainerArea: StageContainerArea,
                      freeBodyDiagramPopupOnly: Boolean) //should the free body diagram be available only as a popup, or also in the play area 
        extends MotionSeriesModule(frame, new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT), name, initialPosition, pausedOnReset, initialAngle, freeBodyDiagramPopupOnly) {
  //Create a default Ramp Canvas and set it as the simulation panel
  val rampCanvas = new RampCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, !objectComboBoxEnabled, showAppliedForceSlider, initialAngle != 0.0, rampLayoutArea, stageContainerArea)
  setSimulationPanel(rampCanvas)

  //Create the control panel and set it as the simulation control panel
  val rampControlPanel = new MotionSeriesControlPanel(motionSeriesModel, fbdModel, coordinateSystemModel, vectorViewModel, resetRampModule, coordinateSystemEnabled, objectComboBoxEnabled, motionSeriesModel, true, true, true, "more.controls.title".translate)
  setControlPanel(rampControlPanel)

  //Set the clock control panel to use one that has record and playback capabilities
  setClockControlPanel(new RecordAndPlaybackControlPanel(motionSeriesModel, rampCanvas, MotionSeriesDefaults.MAX_CHART_DISPLAY_TIME))
}

/**
 * The introductory module, removed much functionality so the user can focus on the motion behavior.
 */
class IntroRampModule(frame: PhetFrame) extends BasicRampModule(frame, "module.introduction".translate,
  coordinateSystemEnabled = false,
  objectComboBoxEnabled = false,
  showAppliedForceSlider = true,
  initialPosition = -6.0,
  pausedOnReset = false,
  initialAngle = MotionSeriesDefaults.defaultRampAngle,
  rampLayoutArea = MotionSeriesDefaults.rampIntroViewport,
  stageContainerArea = MotionSeriesDefaults.fullScreenArea,
  freeBodyDiagramPopupOnly = false)

/**
 * The module that focuses on coordinate frames.
 */
class CoordinatesRampModule(frame: PhetFrame)
        extends BasicRampModule(frame, "module.coordinates".translate, true, false, true, -3, false,
          MotionSeriesDefaults.defaultRampAngle, MotionSeriesDefaults.rampIntroViewport, MotionSeriesDefaults.fullScreenArea, false) {
  coordinateSystemModel.adjustable = true //user is allowed to reorient the coordinate frames in this tab
}

/**
 * This module is the parent class for modules that use graphs in the play area.
 */
class GraphingModule(frame: PhetFrame,
                     name: String,
                     showEnergyGraph: Boolean,
                     rampLayoutArea: Rectangle2D,
                     stageContainerArea: StageContainerArea)
        extends BasicRampModule(frame, name, false, true, false, -2, true, MotionSeriesDefaults.defaultRampAngle, rampLayoutArea, stageContainerArea, true) {
  coordinateSystemModel.adjustable = false //user is not allowed to reorient the coordinate frames in this tab
}

/**
 * The ForceGraphsModule is a GraphingModule that graphs the forces on an object as a function of time.
 */
class ForceGraphsModule(frame: PhetFrame) extends GraphingModule(frame, "module.force-graphs".translate, false, MotionSeriesDefaults.oneGraphViewport, MotionSeriesDefaults.oneGraphArea) {
  rampCanvas.addScreenNode(new RampForceChartNode(rampCanvas, motionSeriesModel))
  def createRecordAndPlaybackPanel = new RecordAndPlaybackControlPanel(motionSeriesModel, rampCanvas, 20) {
    setTimelineNodeVisible(false)
  }
}

//For debugging, just shows the force graphs tab
class ForceGraphsApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new ForceGraphsModule(getPhetFrame))
}

object ForceGraphsApplication {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "ramp-forces-and-motion".literal, classOf[ForceGraphsApplication])
}

/**
 * Main class for the Ramp application and all its modules.
 */
class RampApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new IntroRampModule(getPhetFrame))
  addModule(new ForceGraphsModule(getPhetFrame))

  //At 7-6-2010 Meeting we Decided to remove the Coordinate tab and Work/Energy tab, but keep the code for possible use in the future
  //  addModule(new CoordinatesRampModule(getPhetFrame))
  //  addModule(new WorkEnergyModule(getPhetFrame))

  addModule(new RobotMovingCompanyModule(getPhetFrame))
}

/**
 * Main application for The Ramp simulation.
 * @author Sam Reid
 */
object RampApplication {
  def main(args: Array[String]) = {
    new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "ramp-forces-and-motion".literal, classOf[RampApplication])
  }
}

object Tester {
  def main(args: Array[String]) = RampApplication.main(args)
}