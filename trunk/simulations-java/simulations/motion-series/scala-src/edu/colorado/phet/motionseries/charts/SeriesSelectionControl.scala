package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries
import java.awt._
import javax.swing._
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults._
import edu.colorado.phet.motionseries.swing.MyJCheckBox
import java.awt.event.{ActionListener, ActionEvent, FocusListener, FocusEvent}
import edu.colorado.phet.motionseries.MotionSeriesDefaults

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

  def addToGrid(series: MSDataSeries): Unit = addToGrid(series, createLabel)

  def addToGrid(series: MSDataSeries, labelMaker: MSDataSeries => JComponent): Unit =
    addComponentsToGrid(new SeriesControlSelectorBox(series), labelMaker(series))

  def addComponentsToGrid(component1: JComponent, component2: JComponent) = {
    grid.add(component1)
    grid.add(component2)
  }

  def addComponent(component: JComponent) {
    nongrid.add(component)
  }

  def createLabel(series: MSDataSeries) = {
    //Switching from a JLabel to a JTextField makes the entire application run smoothly on the chart tabs
    //I'm not sure why JLabels were a problem.
    val label = new JTextField()
    label.setBorder(null)
    label.setEditable(false)
    label.setBackground(EARTH_COLOR)
    label.setFont(Defaults.createFont)
    label.setForeground(series.color)
    series.addValueChangeListener(() => {updateLabel()})
    def myValue = new DefaultDecimalFormat("0.00".literal).format(series.getValue)
    def labelText = "chart.series-readout.pattern.value_units".messageformat(myValue, series.units)
    def updateLabel() = label.setText(labelText)

    updateLabel()
    label
  }

  def createEditableLabel(series: MSDataSeries) = {
    val panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0))
    val textField = new JTextField(6)
    textField.addActionListener(new ActionListener() {
      def actionPerformed(e: ActionEvent) = {
        setValueFromText()
      }
    })
    def setValueFromText() = try {
      series.setValue(new DefaultDecimalFormat("0.00".literal).parse(textField.getText).doubleValue)
    } catch {
      case re: Exception => {}
    }
    textField.addFocusListener(new FocusListener() {
      def focusGained(e: FocusEvent) = {}

      def focusLost(e: FocusEvent) = setValueFromText()
    })

    textField.setFont(Defaults.createFont)
    textField.setForeground(series.color)
    series.addValueChangeListener(() => {updateLabel()})
    def updateLabel() = textField.setText(new DefaultDecimalFormat("0.00".literal).format(series.getValue))

    updateLabel()

    panel.add(textField)
    val unitsLabel = new JLabel(series.units) {
      setFont(Defaults.createFont)
      setForeground(series.color)
      setBackground(MotionSeriesDefaults.EARTH_COLOR)
    }
    panel.add(unitsLabel)
    panel.setBackground(MotionSeriesDefaults.EARTH_COLOR)
    panel
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

class SeriesControlSelectorBox(val series: MSDataSeries) extends MyJCheckBox(series.title, series.setVisible(_), series.isVisible, series.addMSDataSeriesListener) {
  setMargin(new Insets(0, 0, 0, 0)) //allows buttons to fit closer together
  setOpaque(false) //let the background show through, TODO: check whether this works on Mac
  setForeground(series.color)
}

object Defaults {
  def createFont = new PhetFont(12, true)
}