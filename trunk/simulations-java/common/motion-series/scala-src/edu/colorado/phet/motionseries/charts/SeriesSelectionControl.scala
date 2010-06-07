package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries
import java.awt._
import javax.swing._
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.motion.model.ITemporalVariable
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults._
import edu.colorado.phet.motionseries.swing.MyJCheckBox

class SeriesSelectionControl(title: String, numRows: Int) extends VerticalLayoutPanel {
  def this(numRows: Int) = this ("".literal, numRows)
  setBackground(EARTH_COLOR)
  val titleLabel = new JLabel(title)
  titleLabel.setFont(new PhetFont(16, true))
  titleLabel.setBackground(EARTH_COLOR)
  add(titleLabel)
  val grid = new JPanel(new GridLayout(numRows, 2))
  grid.setBackground(EARTH_COLOR)
  add(grid)

  val nongrid = new VerticalLayoutPanel()
  nongrid.setBackground(EARTH_COLOR)
  add(nongrid)

  def addToGrid(series: ControlGraphSeries): Unit = addToGrid(series, createLabel)

  def addToGrid(series: ControlGraphSeries, labelMaker: ControlGraphSeries => JComponent): Unit =
    addComponentsToGrid(new SeriesControlSelectorBox(series), labelMaker(series))

  def addComponentsToGrid(component1: JComponent, component2: JComponent) = {
    grid.add(component1)
    grid.add(component2)
  }

  def addComponent(component: JComponent) {
    nongrid.add(component)
  }

  def createLabel(series: ControlGraphSeries) = {
    //Switching from a JLabel to a JTextField makes the entire application run smoothly on the chart tabs
    //I'm not sure why JLabels were a problem.
    val label = new JTextField()
    label.setBorder(null)
    label.setEditable(false)
    label.setBackground(EARTH_COLOR)
    label.setFont(Defaults.createFont)
    label.setForeground(series.getColor)
    series.getTemporalVariable.addListener(new ITemporalVariable.ListenerAdapter() {
      override def valueChanged = updateLabel()
    })
    def myValue = new DefaultDecimalFormat("0.00".literal).format(series.getTemporalVariable.getValue)
    def labelText = "chart.series-readout.pattern.value_units".messageformat(myValue, series.getUnits)
    def updateLabel() = label.setText(labelText)

    updateLabel()
    label
  }
}

trait TitleElement extends JComponent {
  def init() = {
    setFont(Defaults.createFont)
    setForeground(series.getColor)
    setBackground(EARTH_COLOR)
  }

  def series: ControlGraphSeries
}

class SeriesControlTitleLabel(val series: ControlGraphSeries) extends JLabel(series.getTitle) with TitleElement {
  //todo: factor out this code with a trait?
  init()
}

class SeriesControlSelectorBox(val series: ControlGraphSeries)
        extends MyJCheckBox(series.getTitle, series.setVisible(_), series.isVisible, Defaults.addListener(series, _)) with TitleElement {
  setMargin(new Insets(0, 0, 0, 0)) //allows buttons to fit closer together
  init()
}