package edu.colorado.phet.motionseries.charts.bargraphs

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import edu.colorado.phet.common.piccolophet.nodes.barchart.BarChartNode
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.Color
import java.awt.event.{ComponentEvent, ComponentAdapter, WindowEvent, WindowAdapter}
import javax.swing.{JDialog, JFrame}
import edu.colorado.phet.scalacommon.swing.MyJButton
import edu.colorado.phet.motionseries.MotionSeriesDefaults

import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.colorado.phet.motionseries.MotionSeriesResources._

class WorkEnergyChartVisibilityModel extends Observable {
  private var defaultVisible = false
  private var _visible = defaultVisible

  def visible = _visible

  def visible_=(b: Boolean) = {_visible = b; notifyListeners()}

  def reset() = {visible = defaultVisible}
}

class WorkEnergyChart(workEnergyChartModel: WorkEnergyChartVisibilityModel, model: MotionSeriesModel, owner: JFrame) {
  val dialog = new JDialog(owner, "controls.energy-chart".translate, false)

  def updateDialogVisible() = dialog.setVisible(workEnergyChartModel.visible)
  workEnergyChartModel.addListenerByName {updateDialogVisible()}
  updateDialogVisible()
  val barChartNode = new BarChartNode("forces.energy-title".translate, 0.05, Color.white)
  import MotionSeriesDefaults._
  val totalEnergyVariable = new BarChartNode.Variable("energy.total-energy".translate, 0.0, totalEnergyColor)
  val kineticEnergyVariable = new BarChartNode.Variable("energy.kinetic-energy".translate, 0.0, kineticEnergyColor)
  val potentialEnergyVariable = new BarChartNode.Variable("energy.potential-energy".translate, 0.0, potentialEnergyColor)
  val thermalEnergyVariable = new BarChartNode.Variable("energy.thermal-energy".translate, 0.0, thermalEnergyColor)

  //  val appliedWorkVariable = new BarChartNode.Variable("work.applied-work".translate, 0.0, appliedWorkColor)
  //  val frictionWorkVariable = new BarChartNode.Variable("work.friction-work".translate, 0.0, frictionWorkColor)
  //  val gravityWorkVariable = new BarChartNode.Variable("work.gravity-work".translate, 0.0, gravityWorkColor)

  barChartNode.init(Array(totalEnergyVariable, kineticEnergyVariable, potentialEnergyVariable, thermalEnergyVariable
    //    ,appliedWorkVariable, frictionWorkVariable, gravityWorkVariable
    ))
  val canvas = new PhetPCanvas
  val clearButton = new PSwing(new MyJButton("controls.clear-heat".translate, () => model.clearHeat()))
  val zoomButton = new ZoomControlNode(ZoomControlNode.VERTICAL) {
    addZoomListener(new ZoomControlNode.ZoomListener() {
      val myscale = 1.5

      def doZoom(factor: Double) = barChartNode.setBarScale(barChartNode.getBarScale * factor)

      def zoomedOut = doZoom(1 / myscale)

      def zoomedIn = doZoom(myscale)
    })
  }
  barChartNode.setOffset(20, 20)
  canvas.addWorldChild(barChartNode)
  canvas.addWorldChild(clearButton)
  canvas.addWorldChild(zoomButton)
  dialog.setContentPane(canvas)
  dialog.setSize(175, 768)
  canvas.addComponentListener(new ComponentAdapter() {override def componentResized(e: ComponentEvent) = updateButtonLocations()})
  def updateButtonLocations() = {
    clearButton.setOffset(0, canvas.getHeight - clearButton.getFullBounds.getHeight)
    zoomButton.setOffset(canvas.getWidth - zoomButton.getFullBounds.getWidth, canvas.getHeight - zoomButton.getFullBounds.getHeight)
  }
  updateButtonLocations()
  dialog.addWindowListener(new WindowAdapter() {override def windowClosing(e: WindowEvent) = workEnergyChartModel.visible = false})
  SwingUtils.centerWindowOnScreen(dialog)
  val bead = model.bead
  bead.addListenerByName {
    totalEnergyVariable.setValue(bead.getTotalEnergy)
    kineticEnergyVariable.setValue(bead.getKineticEnergy)
    potentialEnergyVariable.setValue(bead.getPotentialEnergy)
    thermalEnergyVariable.setValue(bead.getThermalEnergy)

    //    appliedWorkVariable.setValue(bead.getAppliedWork)
    //    frictionWorkVariable.setValue(bead.getFrictiveWork)
    //    gravityWorkVariable.setValue(bead.getGravityWork)
    barChartNode.update()
  }
}