package edu.colorado.phet.motionseries.sims.theramp

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.motionseries.graphics.{RampCanvas}
import java.awt.geom.Rectangle2D
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationConfig}
import edu.colorado.phet.common.piccolophet.{PiccoloPhetApplication}
import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{Color}
import javax.swing._
import edu.colorado.phet.motionseries.controls.RampControlPanel
import robotmovingcompany.{RobotMovingCompanyModule}
import edu.colorado.phet.scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}

import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.charts.bargraphs._
import edu.colorado.phet.motionseries.{MotionSeriesDefaults, MotionSeriesModule}

trait StageContainerArea {
  def getBounds(w: Double, h: Double): Rectangle2D
}

class BasicRampModule(frame: PhetFrame,
                      clock: ScalaClock,
                      name: String,
                      coordinateSystemFeaturesEnabled: Boolean,
                      useObjectComboBox: Boolean,
                      showAppliedForceSlider: Boolean,
                      defaultBeadPosition: Double,
                      pausedOnReset: Boolean,
                      initialAngle: Double,
                      rampLayoutArea: Rectangle2D,
                      stageContainerArea: StageContainerArea)
        extends MotionSeriesModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val rampCanvas = new RampCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame,
    !useObjectComboBox, showAppliedForceSlider, initialAngle != 0.0, rampLayoutArea, stageContainerArea)
  setSimulationPanel(rampCanvas)
  val rampControlPanel = new RampControlPanel(motionSeriesModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, motionSeriesModel, true, true, true)
  setControlPanel(rampControlPanel)
  setClockControlPanel(new RecordModelControlPanel(motionSeriesModel, rampCanvas, () => new PlaybackSpeedSlider(motionSeriesModel), Color.blue, 20))
}

import edu.colorado.phet.motionseries.MotionSeriesResources._

class IntroRampModule(frame: PhetFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "module.introduction".translate, false, false, true, -6, false, MotionSeriesDefaults.defaultRampAngle, MotionSeriesDefaults.defaultViewport, MotionSeriesDefaults.fullScreenArea)

class CoordinatesRampModule(frame: PhetFrame,
                            clock: ScalaClock)
        extends BasicRampModule(frame, clock, "module.coordinates".translate, true, false, true, -6, false, MotionSeriesDefaults.defaultRampAngle, MotionSeriesDefaults.defaultViewport, MotionSeriesDefaults.fullScreenArea) {
  coordinateSystemModel.adjustable = true
}

class GraphingModule(frame: PhetFrame,
                     clock: ScalaClock,
                     name: String,
                     showEnergyGraph: Boolean,
                     rampLayoutArea: Rectangle2D,
                     stageContainerArea: StageContainerArea)
        extends BasicRampModule(frame, clock, name, false, true, false, -6, true, MotionSeriesDefaults.defaultRampAngle, rampLayoutArea, stageContainerArea) {
  coordinateSystemModel.adjustable = false
}

class ForceGraphsModule(frame: PhetFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "module.force-graphs".translate, false, MotionSeriesDefaults.oneGraphViewport, MotionSeriesDefaults.oneGraphArea) {
  rampCanvas.addScreenNode(new RampForceChartNode(rampCanvas, motionSeriesModel))
}

class WorkEnergyModule(frame: PhetFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "module.energy".translate, true, MotionSeriesDefaults.oneGraphViewport, MotionSeriesDefaults.oneGraphArea) {
  rampCanvas.addScreenNode(new RampForceEnergyChartNode(rampCanvas, motionSeriesModel))
  val workEnergyChartModel = new WorkEnergyChartModel
  val workEnergyChartButton = new JButton("controls.show-energy-chart".translate)
  workEnergyChartButton.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = {workEnergyChartModel.visible = true}
  })
  rampControlPanel.addToBody(workEnergyChartButton)
  val workEnergyChart = new WorkEnergyChart(workEnergyChartModel, motionSeriesModel, frame)

  override def resetAll() = {super.reset(); workEnergyChartModel.reset()}
}

class RampApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)
  addModule(new IntroRampModule(getPhetFrame, newClock))
  addModule(new ForceGraphsModule(getPhetFrame, newClock))
  addModule(new CoordinatesRampModule(getPhetFrame, newClock))
  addModule(new WorkEnergyModule(getPhetFrame, newClock))
  addModule(new RobotMovingCompanyModule(getPhetFrame, newClock))
}
class RampWorkEnergyApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)
  addModule(new WorkEnergyModule(getPhetFrame, newClock))
}

class RobotMovingCompanyApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new RobotMovingCompanyModule(getPhetFrame, new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)))
}