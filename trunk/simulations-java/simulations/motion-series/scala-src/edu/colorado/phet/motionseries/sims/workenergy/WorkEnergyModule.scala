package edu.colorado.phet.motionseries.sims.workenergy

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.common.phetcommon.application.Module
import java.awt.event.{ComponentEvent, ComponentAdapter}
import edu.umd.cs.piccolox.pswing.PSwing
import swing.Button
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.sims.rampforcesandmotion.GraphingModule
import edu.colorado.phet.motionseries.Predef._

/**
 * The WorkEnergyModule is a tab that focuses on work and energy issues.
 * @author Sam Reid
 */
class WorkEnergyModule(frame: PhetFrame) extends GraphingModule(frame, "module.energy".translate, true, MotionSeriesDefaults.oneGraphViewport, MotionSeriesDefaults.oneGraphArea) {
  //  rampCanvas.addScreenNode(new RampForceEnergyChartNode(rampCanvas, motionSeriesModel))
  val workEnergyChartVisibilityModel = new WorkEnergyChartVisibilityModel

  //create a "show energy chart" button and add it as a PSwing near the top-middle of the play area
  val showEnergyChartButton = Button("controls.show-energy-chart".translate) {
    workEnergyChartVisibilityModel.visible = true
  }
  val showEnergyButtonPSwing = new PSwing(showEnergyChartButton.peer)
  rampCanvas.getLayer.addChild(showEnergyButtonPSwing) //todo: why doesn't addScreenChild work here?  It seems like it has the wrong transform.

  //update the location of the "Show energy chart" button when the screen changes size
  def updateEnergyButtonLocation() = {
    val insetX = 4
    val insetY = insetX
    showEnergyButtonPSwing.setOffset(rampCanvas.getWidth / 2 - showEnergyButtonPSwing.getWidth - insetX, insetY)
  }
  rampCanvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = updateEnergyButtonLocation()
  })
  updateEnergyButtonLocation() //initialize the location correctly on startup

  //make sure the visibility of the "show energy" button is correct
  workEnergyChartVisibilityModel.addListener(() => showEnergyButtonPSwing.setVisible(!workEnergyChartVisibilityModel.visible))

  //create the work-energy chart; its visibility is determined by the visibility model
  val workEnergyChart = new WorkEnergyChart(workEnergyChartVisibilityModel, motionSeriesModel.asInstanceOf[WorkEnergyModel], frame)//TODO: this will throw cast exception, but we won't invest in fixing this until we make more decisions about the work energy sim

  /**When the sim is reset, also reset the the chart visibility.*/
  override def resetAll() = {
    super.reset()
    workEnergyChartVisibilityModel.reset()
  }

  //Minimize the energy chart window when changing tabs, and restore it when returning to this tab (if it needs restoring)
  addListener(new Module.Listener() {
    var energyChartVisibleOnDeactivate = false

    def activated() = {
      workEnergyChartVisibilityModel.visible = energyChartVisibleOnDeactivate
    }

    def deactivated() = {
      energyChartVisibleOnDeactivate = workEnergyChartVisibilityModel.visible
      workEnergyChartVisibilityModel.visible = false
    }
  })
}