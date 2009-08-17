package edu.colorado.phet.therampscala.charts

import common.motion.graphs.ControlGraphSeries
import java.awt._
import javax.swing._
import common.phetcommon.view.VerticalLayoutPanel
import common.phetcommon.view.util.PhetFont
import common.motion.model.ITemporalVariable
import common.phetcommon.util.DefaultDecimalFormat
import RampResources._
import RampDefaults._
import swing.{MyJCheckBox}

class SeriesSelectionControl(title: String, numRows: Int) extends VerticalLayoutPanel {
  setBackground(EARTH_COLOR)
  val titleLabel = new JLabel(title)
  titleLabel.setFont(new PhetFont(18, true))
  titleLabel.setBackground(EARTH_COLOR)
  add(titleLabel)
  val grid = new JPanel(new GridLayout(numRows, 2))
  grid.setBackground(EARTH_COLOR)

  def addToGrid(series: ControlGraphSeries): Unit = {
    addToGrid(series, createLabel)
  }

  def addToGrid(series: ControlGraphSeries, labelMaker: ControlGraphSeries => JComponent): Unit = {
    grid.add(new SeriesControlSelectorBox(series))
    grid.add(createLabel(series))
  }

  def createLabel(series: ControlGraphSeries) = {
    val label = new JLabel()
    label.setBackground(EARTH_COLOR)
    label.setFont(Defaults.createFont)
    label.setForeground(series.getColor)
    series.getTemporalVariable.addListener(new ITemporalVariable.ListenerAdapter() {
      override def valueChanged = updateLabel()
    })
    def myValue = new DefaultDecimalFormat("0.00".literal).format(series.getTemporalVariable.getValue)
    def labelText = "chart.series-readout.pattern.value_units".translate.messageformat(myValue, series.getUnits)
    def updateLabel() = label.setText(labelText)

    updateLabel()
    label
  }

  add(grid)
}

class SeriesControlSelectorBox(series: ControlGraphSeries) extends MyJCheckBox(series.getTitle, series.setVisible(_), series.isVisible, Defaults.addListener(series, _)) {
  setFont(Defaults.createFont)
  setMargin(new Insets(0, 0, 0, 0)) //allows buttons to fit closer together
  setForeground(series.getColor)
  setBackground(EARTH_COLOR)
}