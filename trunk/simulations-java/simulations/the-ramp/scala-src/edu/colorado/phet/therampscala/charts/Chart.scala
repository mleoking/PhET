package edu.colorado.phet.therampscala.charts


import common.jfreechartphet.piccolo.JFreeChartNode
import common.motion.graphs.JFreeChartDecorator.{DottedZeroLine, InChartTickMarks}
import common.motion.graphs.{ControlGraph}
import common.piccolophet.PhetPCanvas
import javax.swing.JFrame
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode

class Chart extends PNode {
  //  val dataset = new DefaultXYDataset
  //  val domainAxis = new NumberAxis
  //  domainAxis.setRange(0, 20)
  //  val rangeAxis = new NumberAxis
  //  rangeAxis.setRange(-10, 10)
  //  val plot = new XYPlot(dataset, domainAxis, rangeAxis, new XYLineAndShapeRenderer)
  //  val chart = new JFreeChart(plot)
  val chart = ControlGraph.createDefaultChart("Title")
  chart.getXYPlot.getDomainAxis.setRange(0, 20)
  chart.getXYPlot.getRangeAxis.setRange(-10, 10)
  chart.addJFreeChartNodeGraphic(new InChartTickMarks(2, 2, 10))
  chart.addJFreeChartNodeGraphic(new DottedZeroLine)
  val chartNode = new JFreeChartNode(chart)

  chartNode.setBounds(0, 0, 700, 400)
  addChild(chartNode)
}

class TestPNode(nodes: PNode*) {
  val frame = new JFrame
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setSize(800, 600)
  val canvas = new PhetPCanvas()
  for (node <- nodes)
    canvas.addScreenChild(node)
  frame.setContentPane(canvas)
  frame.setVisible(true)
}

object TestChart {
  def main(args: Array[String]) {
    new TestPNode(new PText("hello"), new Chart)
  }
}