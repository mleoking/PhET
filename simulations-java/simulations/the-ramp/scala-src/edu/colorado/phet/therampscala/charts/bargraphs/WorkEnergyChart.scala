package edu.colorado.phet.therampscala.charts.bargraphs

import common.phetcommon.view.util.SwingUtils
import common.piccolophet.nodes.barchart.BarChartNode
import common.piccolophet.PhetPCanvas
import java.awt.Color
import java.awt.event.{WindowEvent, WindowAdapter}
import javax.swing.JFrame
import scalacommon.util.Observable
import model.RampModel

class WorkEnergyChartModel extends Observable {
  private var defaultVisible = true;
  private var _visible = defaultVisible

  def visible = _visible

  def visible_=(b: Boolean) = {_visible = b; notifyListeners()}

  def reset() = {visible = defaultVisible}
}

class WorkEnergyChart(workEnergyChartModel: WorkEnergyChartModel, model: RampModel) {
  val frame = new JFrame("Chart")
  workEnergyChartModel.addListenerByName {frame.setVisible(workEnergyChartModel.visible)}
  val barChartNode = new BarChartNode("Work/Energy", 0.1, Color.white)
  import RampDefaults._
  val totalEnergyVariable = new BarChartNode.Variable("Total Energy", 0.0, totalEnergyColor)
  val kineticEnergyVariable = new BarChartNode.Variable("Kinetic Energy", 0.0, kineticEnergyColor)
  val potentialEnergyVariable = new BarChartNode.Variable("Potential Energy", 0.0, potentialEnergyColor)
  val thermalEnergyVariable = new BarChartNode.Variable("Thermal Energy", 0.0, thermalEnergyColor)
  barChartNode.init(Array(totalEnergyVariable, kineticEnergyVariable, potentialEnergyVariable, thermalEnergyVariable))
  val canvas = new PhetPCanvas
  barChartNode.setOffset(50, 50)
  canvas.addWorldChild(barChartNode)
  frame.setContentPane(canvas)
  frame.setSize(300, 600)
  frame.addWindowListener(new WindowAdapter() {override def windowClosing(e: WindowEvent) = workEnergyChartModel.visible = false})
  SwingUtils.centerWindowOnScreen(frame)
  val bead = model.bead
  bead.addListenerByName {
    totalEnergyVariable.setValue(bead.getTotalEnergy)
    kineticEnergyVariable.setValue(bead.getKineticEnergy)
    potentialEnergyVariable.setValue(bead.getPotentialEnergy)
    thermalEnergyVariable.setValue(bead.getThermalEnergy)
    barChartNode.update()
  }
}