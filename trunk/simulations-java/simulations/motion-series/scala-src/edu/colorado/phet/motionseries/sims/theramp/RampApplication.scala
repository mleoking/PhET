package edu.colorado.phet.motionseries.sims.theramp

import charts.bargraphs.{WorkEnergyChartModel, WorkEnergyChart}
import java.awt.geom.Rectangle2D
import phet.common.phetcommon.application.{Module, PhetApplicationConfig}
import phet.common.piccolophet.{PiccoloPhetApplication}
import graphics.RampCanvas
import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{Color}
import javax.swing._
import model._
import controls.RampControlPanel
import robotmovingcompany.{RobotMovingCompanyModule, RobotMovingCompanyGameModel, Result, RobotMovingCompanyCanvas}
import scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}

import scalacommon.ScalaClock

class BasicRampModule(frame: JFrame, clock: ScalaClock, name: String,
                      coordinateSystemFeaturesEnabled: Boolean, useObjectComboBox: Boolean, showAppliedForceSlider: Boolean,
                      defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double, rampLayoutArea: Rectangle2D)
        extends MotionSeriesModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
  val rampCanvas = new RampCanvas(rampModel, coordinateSystemModel, fbdModel, vectorViewModel, frame,
    !useObjectComboBox, showAppliedForceSlider, initialAngle != 0.0, rampLayoutArea)
  setSimulationPanel(rampCanvas)
  val rampControlPanel = new RampControlPanel(rampModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, rampModel, true, true)
  setControlPanel(rampControlPanel)
  setClockControlPanel(new RecordModelControlPanel(rampModel, rampCanvas, () => new PlaybackSpeedSlider(rampModel), Color.blue, 20))
}

import motionseries.MotionSeriesResources._

class IntroRampModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "module.introduction".translate, false, false, true, -6, false, MotionSeriesDefaults.defaultRampAngle, MotionSeriesDefaults.defaultViewport)

class CoordinatesRampModule(frame: JFrame, clock: ScalaClock) extends BasicRampModule(frame, clock, "module.coordinates".translate, true, false, true, -6, false, MotionSeriesDefaults.defaultRampAngle, MotionSeriesDefaults.defaultViewport) {
  coordinateSystemModel.adjustable = true
}

class GraphingModule(frame: JFrame, clock: ScalaClock, name: String, showEnergyGraph: Boolean, rampLayoutArea: Rectangle2D)
        extends BasicRampModule(frame, clock, name, false, true, false, -6, true, MotionSeriesDefaults.defaultRampAngle, rampLayoutArea) {
  coordinateSystemModel.adjustable = false
}

class ForceGraphsModule(frame: JFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "module.force-graphs".translate, false, MotionSeriesDefaults.forceGraphViewport) {
  rampCanvas.addScreenNode(new RampForceChartNode(rampCanvas, rampModel))
}

class WorkEnergyModule(frame: JFrame, clock: ScalaClock) extends GraphingModule(frame, clock, "module.work-energy".translate, true, MotionSeriesDefaults.forceEnergyGraphViewport) {
  rampCanvas.addScreenNode(new RampForceEnergyChartNode(rampCanvas, rampModel))
  val workEnergyChartModel = new WorkEnergyChartModel
  val workEnergyChartButton = new JButton("controls.showWorkEnergyCharts".translate)
  workEnergyChartButton.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = {workEnergyChartModel.visible = true}
  })
  rampControlPanel.addToBody(workEnergyChartButton)
  val workEnergyChart = new WorkEnergyChart(workEnergyChartModel, rampModel, frame)

  override def resetAll() = {super.reset(); workEnergyChartModel.reset()}
}

class RampApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)
  addModule(new IntroRampModule(getPhetFrame, newClock))
  addModule(new CoordinatesRampModule(getPhetFrame, newClock))
  addModule(new ForceGraphsModule(getPhetFrame, newClock))
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