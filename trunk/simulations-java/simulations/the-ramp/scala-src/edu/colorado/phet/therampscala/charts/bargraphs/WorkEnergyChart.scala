package edu.colorado.phet.therampscala.charts.bargraphs

import common.phetcommon.view.util.SwingUtils
import common.piccolophet.nodes.barchart.BarChartNode
import common.piccolophet.PhetPCanvas
import java.awt.Color
import java.awt.event.{WindowEvent, WindowAdapter}
import javax.swing.{JDialog, JFrame}
import scalacommon.util.Observable
import model.RampModel

class WorkEnergyChartModel extends Observable {
  private var defaultVisible = true;
  private var _visible = defaultVisible

  def visible = _visible

  def visible_=(b: Boolean) = {_visible = b; notifyListeners()}

  def reset() = {visible = defaultVisible}
}

class WorkEnergyChart(workEnergyChartModel: WorkEnergyChartModel, model: RampModel,owner:JFrame) {
  val frame = new JDialog(owner,"Work/Energy Chart",false)
  workEnergyChartModel.addListenerByName {frame.setVisible(workEnergyChartModel.visible)}
  val barChartNode = new BarChartNode("Work/Energy", 0.05, Color.white)
  import RampDefaults._
  val totalEnergyVariable = new BarChartNode.Variable("Total Energy", 0.0, totalEnergyColor)
  val kineticEnergyVariable = new BarChartNode.Variable("Kinetic Energy", 0.0, kineticEnergyColor)
  val potentialEnergyVariable = new BarChartNode.Variable("Potential Energy", 0.0, potentialEnergyColor)
  val thermalEnergyVariable = new BarChartNode.Variable("Thermal Energy", 0.0, thermalEnergyColor)

  val appliedWorkVariable = new BarChartNode.Variable("Applied Work", 0.0, appliedWorkColor)
  val frictionWorkVariable = new BarChartNode.Variable("Friction Work", 0.0, frictionWorkColor)
  val gravityWorkVariable = new BarChartNode.Variable("Gravity Work", 0.0, gravityWorkColor)
  val wallWorkVariable = new BarChartNode.Variable("Wall Work", 0.0, Color.black)
  val normalWorkVariable = new BarChartNode.Variable("Normal Work", 0.0, Color.black)

  barChartNode.init(Array(totalEnergyVariable, kineticEnergyVariable, potentialEnergyVariable, thermalEnergyVariable,
    appliedWorkVariable, frictionWorkVariable, gravityWorkVariable, wallWorkVariable, normalWorkVariable))
  val canvas = new PhetPCanvas
  barChartNode.setOffset(20, 20)
  canvas.addWorldChild(barChartNode)
  frame.setContentPane(canvas)
  frame.setSize(300, 768)
  frame.addWindowListener(new WindowAdapter() {override def windowClosing(e: WindowEvent) = workEnergyChartModel.visible = false})
  SwingUtils.centerWindowOnScreen(frame)
  val bead = model.bead
  bead.addListenerByName {
    totalEnergyVariable.setValue(bead.getTotalEnergy)
    kineticEnergyVariable.setValue(bead.getKineticEnergy)
    potentialEnergyVariable.setValue(bead.getPotentialEnergy)
    thermalEnergyVariable.setValue(bead.getThermalEnergy)

    appliedWorkVariable.setValue(bead.getAppliedWork)
    frictionWorkVariable.setValue(bead.getFrictiveWork)
    gravityWorkVariable.setValue(bead.getGravityWork)
    wallWorkVariable.setValue(bead.getWallWork)
    normalWorkVariable.setValue(bead.getNormalWork)
    barChartNode.update()
  }
}