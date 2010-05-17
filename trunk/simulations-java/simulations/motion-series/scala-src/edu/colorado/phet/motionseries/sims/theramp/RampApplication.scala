package edu.colorado.phet.motionseries.sims.theramp

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.motionseries.graphics.{RampCanvas}
import java.awt.geom.Rectangle2D
import edu.colorado.phet.common.piccolophet.{PiccoloPhetApplication}
import java.awt.{Color}
import edu.colorado.phet.motionseries.controls.RampControlPanel
import robotmovingcompany.{RobotMovingCompanyModule}

import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.charts.bargraphs._
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationLauncher, PhetApplicationConfig}
import edu.colorado.phet.motionseries.{StageContainerArea, MotionSeriesDefaults, MotionSeriesModule}
import swing.Button
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.recordandplayback.gui.{RecordAndPlaybackControlPanel, PlaybackSpeedSlider}

/**
 * This is the parent class for the various Modules for the ramp simulation.
 * It has many parameters since the application has many tabs with different requirements.
 * @author Sam Reid
 */
class BasicRampModule(frame: PhetFrame,
                      name: String,
                      coordinateSystemEnabled: Boolean,
                      objectComboBoxEnabled: Boolean,
                      showAppliedForceSlider: Boolean,
                      initialBeadPosition: Double,
                      pausedOnReset: Boolean,
                      initialAngle: Double,
                      rampLayoutArea: Rectangle2D,
                      stageContainerArea: StageContainerArea,
                      freeBodyDiagramPopupOnly: Boolean) //should the free body diagram be available only as a popup, or also in the play area 
        extends MotionSeriesModule(frame, new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT), name, initialBeadPosition, pausedOnReset, initialAngle, freeBodyDiagramPopupOnly) {
  //Create a default Ramp Canvas and set it as the simulation panel
  val rampCanvas = new RampCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, !objectComboBoxEnabled, showAppliedForceSlider, initialAngle != 0.0, rampLayoutArea, stageContainerArea)
  setSimulationPanel(rampCanvas)

  //Create the control panel and set it as the simulation control panel
  val rampControlPanel = new RampControlPanel(motionSeriesModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel, resetRampModule, coordinateSystemEnabled, objectComboBoxEnabled, motionSeriesModel, true, true, true)
  setControlPanel(rampControlPanel)

  //Set the clock control panel
  setClockControlPanel(new RecordAndPlaybackControlPanel(motionSeriesModel, rampCanvas, 20))
}

/**
 * The introductory module, removed much functionality so the user can focus on the motion behavior.
 */
class IntroRampModule(frame: PhetFrame) extends BasicRampModule(frame, "module.introduction".translate,
  coordinateSystemEnabled = false,
  objectComboBoxEnabled = false,
  showAppliedForceSlider = true,
  initialBeadPosition = -3.0,
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
  coordinateSystemModel.adjustable = true
}

/**
 * This module introduces graphing, it is the parent class for the modules that use graphs in the play area.
 */
class GraphingModule(frame: PhetFrame,
                     name: String,
                     showEnergyGraph: Boolean,
                     rampLayoutArea: Rectangle2D,
                     stageContainerArea: StageContainerArea)
        extends BasicRampModule(frame, name, false, true, false, -6, true, MotionSeriesDefaults.defaultRampAngle, rampLayoutArea, stageContainerArea, true) {
  coordinateSystemModel.adjustable = false
}

class ForceGraphsModule(frame: PhetFrame) extends GraphingModule(frame, "module.force-graphs".translate, false, MotionSeriesDefaults.oneGraphViewport, MotionSeriesDefaults.oneGraphArea) {
  rampCanvas.addScreenNode(new RampForceChartNode(rampCanvas, motionSeriesModel))
}

class WorkEnergyModule(frame: PhetFrame) extends GraphingModule(frame, "module.energy".translate, true, MotionSeriesDefaults.oneGraphViewport, MotionSeriesDefaults.oneGraphArea) {
  rampCanvas.addScreenNode(new RampForceEnergyChartNode(rampCanvas, motionSeriesModel))
  val workEnergyChartVisibilityModel = new WorkEnergyChartVisibilityModel

  //create a "show energy chart" button and add it to the primary part of the control panel
  val showEnergyChartButton = Button("controls.show-energy-chart".translate) {
    workEnergyChartVisibilityModel.visible = true
  }
  rampControlPanel.addPrimaryControl(showEnergyChartButton)

  //todo: which is easier to read?
  //rampControlPanel.addPrimaryControl(Button("controls.show-energy-chart".translate) {workEnergyChartVisibilityModel.visible = true})

  //create the work-energy chart; its visibility is determined by the visibility model
  val workEnergyChart = new WorkEnergyChart(workEnergyChartVisibilityModel, motionSeriesModel, frame)

  /**When the sim is reset, also reset the the chart visibility.*/
  override def resetAll() = {
    super.reset()
    workEnergyChartVisibilityModel.reset()
  }
}

class RampApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new IntroRampModule(getPhetFrame))
  addModule(new ForceGraphsModule(getPhetFrame))
  addModule(new CoordinatesRampModule(getPhetFrame))
  addModule(new WorkEnergyModule(getPhetFrame))
  addModule(new RobotMovingCompanyModule(getPhetFrame))
}

/**
 * Main application for The Ramp simulation.
 * @author Sam Reid
 */
object RampApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "the-ramp".literal, classOf[RampApplication])
}