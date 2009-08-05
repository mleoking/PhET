package edu.colorado.phet.therampscala.charts


import common.motion.graphs.ControlGraphSeries
import swing.MyCheckBox
import common.phetcommon.view.VerticalLayoutPanel
import common.phetcommon.view.util.PhetFont
import javax.swing.{JPanel, JComponent, JLabel}
import java.awt.GridLayout
import common.motion.model.ITemporalVariable
import common.phetcommon.util.DefaultDecimalFormat
import RampResources._

class SeriesSelectionControl(title: String, numRows: Int) extends VerticalLayoutPanel {
  setBackground(RampDefaults.EARTH_COLOR)
  val jLabel = new JLabel(title)
  jLabel.setFont(new PhetFont(20, true))
  jLabel.setBackground(RampDefaults.EARTH_COLOR)
  add(jLabel)
  val grid = new JPanel(new GridLayout(numRows, 2))
  grid.setBackground(RampDefaults.EARTH_COLOR)

  def addToGrid(series: ControlGraphSeries): Unit = {
    addToGrid(series, createLabel)
  }

  def addToGrid(series: ControlGraphSeries, labelMaker: ControlGraphSeries => JComponent): Unit = {
    grid.add(new SeriesControlSelectorBox(series).peer)
    grid.add(labelMaker(series))
  }

  def createLabel(series: ControlGraphSeries) = {
    val label = new JLabel()
    label.setBackground(RampDefaults.EARTH_COLOR)
    label.setFont(Defaults.createFont)
    label.setForeground(series.getColor)
    series.getTemporalVariable.addListener(new ITemporalVariable.ListenerAdapter() {
      override def valueChanged = updateLabel()
    })
    def myValue = new DefaultDecimalFormat("0.00".literal).format(series.getTemporalVariable.getValue)
    def labelText = "chart.series-readout.pattern.value_units".translate.messageformat(myValue,series.getUnits)
    def updateLabel() = label.setText(labelText)

    updateLabel()
    label
  }

  add(grid)
}

class SeriesControlSelectorBox(series: ControlGraphSeries) extends MyCheckBox(series.getTitle, series.setVisible(_), series.isVisible, Defaults.addListener(series, _)) {
  peer.setFont(Defaults.createFont)
  peer.setForeground(series.getColor)
  peer.setBackground(RampDefaults.EARTH_COLOR)
}