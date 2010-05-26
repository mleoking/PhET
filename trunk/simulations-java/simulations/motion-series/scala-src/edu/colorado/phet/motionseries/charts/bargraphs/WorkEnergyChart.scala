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
import MotionSeriesDefaults._
import BarChartNode.Variable

/**
 * Model class in MVC for determining whether the work energy chart is visible or not.
 * @author Sam Reid
 */
class WorkEnergyChartVisibilityModel extends Observable {
  private var defaultVisible = false
  private var _visible = defaultVisible

  def visible = _visible

  def visible_=(b: Boolean) = {_visible = b; notifyListeners()}

  //Resets the visibility to the default value
  def reset() = {visible = defaultVisible}
}

/**
 * The WorkEnergyChart is the bar graph shown in the JDialog that represents the different types of energy in the object
 */
class WorkEnergyChart(visibilityModel: WorkEnergyChartVisibilityModel, model: MotionSeriesModel, owner: JFrame) {
  val dialog = new JDialog(owner, "controls.energy-chart".translate, false)

  def updateDialogVisible() = dialog.setVisible(visibilityModel.visible)
  visibilityModel.addListener(() => updateDialogVisible())
  updateDialogVisible()
  val barChartNode = new BarChartNode("forces.energy-title".translate, 0.05, Color.white)
  val totalEnergyVariable = new Variable("energy.total-energy".translate, 0.0, totalEnergyColor)
  val kineticEnergyVariable = new Variable("energy.kinetic-energy".translate, 0.0, kineticEnergyColor)
  val potentialEnergyVariable = new Variable("energy.potential-energy".translate, 0.0, potentialEnergyColor)
  val thermalEnergyVariable = new Variable("energy.thermal-energy".translate, 0.0, thermalEnergyColor)

  //We decided not to show work in this chart because it was becoming overloaded.
  //But I'll leave these here in case we change our minds.
  //  val appliedWorkVariable = new BarChartNode.Variable("work.applied-work".translate, 0.0, appliedWorkColor)
  //  val frictionWorkVariable = new BarChartNode.Variable("work.friction-work".translate, 0.0, frictionWorkColor)
  //  val gravityWorkVariable = new BarChartNode.Variable("work.gravity-work".translate, 0.0, gravityWorkColor)

  barChartNode.init(Array(totalEnergyVariable, kineticEnergyVariable, potentialEnergyVariable, thermalEnergyVariable
    //    ,appliedWorkVariable, frictionWorkVariable, gravityWorkVariable
    ))

  val canvas = new PhetPCanvas //canvas to show the bars in.
  val clearButton = new PSwing(new MyJButton("controls.clear-heat".translate, () => model.clearHeat()))
  val zoomButton = new ZoomControlNode(ZoomControlNode.VERTICAL) {
    addZoomListener(new ZoomControlNode.ZoomListener() {
      val zoomScale = 1.5

      def zoom(scaleFactor: Double) = barChartNode.setBarScale(barChartNode.getBarScale * scaleFactor)

      def zoomedOut = zoom(1 / zoomScale)

      def zoomedIn = zoom(zoomScale)
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
  dialog.addWindowListener(new WindowAdapter() {override def windowClosing(e: WindowEvent) = visibilityModel.visible = false})
  SwingUtils.centerWindowOnScreen(dialog)
  val bead = model.bead
  bead.addListenerByName {
    totalEnergyVariable.setValue(bead.getTotalEnergy)
    kineticEnergyVariable.setValue(bead.kineticEnergy)
    potentialEnergyVariable.setValue(bead.potentialEnergy)
    thermalEnergyVariable.setValue(bead.thermalEnergy)

    //    appliedWorkVariable.setValue(bead.getAppliedWork)
    //    frictionWorkVariable.setValue(bead.getFrictiveWork)
    //    gravityWorkVariable.setValue(bead.getGravityWork)
    barChartNode.update()
  }
}