package edu.colorado.phet.therampscala.charts.bargraphs

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
  val barChartNode = new BarChartNode("Work/Energy", 1.0, Color.white)
  val v1 = new BarChartNode.Variable("time", 0.0, Color.blue)
  barChartNode.init(Array(v1))
  val canvas = new PhetPCanvas
  canvas.addWorldChild(barChartNode)
  frame.setContentPane(canvas)
  frame.setSize(800, 600)
  frame.addWindowListener(new WindowAdapter() {override def windowClosing(e: WindowEvent) = workEnergyChartModel.visible = false})
  model.addListenerByName{
    v1.setValue(model.getTime)
    barChartNode.update()
  }
}