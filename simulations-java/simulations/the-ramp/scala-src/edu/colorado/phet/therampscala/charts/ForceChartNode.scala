package edu.colorado.phet.therampscala.charts

import common.phetcommon.util.DefaultDecimalFormat
import common.phetcommon.view.util.{PhetFont}
import common.motion.graphs._
import common.motion.model._
import common.phetcommon.model.clock.ConstantDtClock
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.{PhetPCanvas}
import common.timeseries.model.{RecordableModel, TimeSeriesModel}
import java.awt.event.{FocusEvent, FocusListener, ActionEvent, ActionListener}
import java.awt.geom.Point2D
import java.awt.{GridLayout}
import javax.swing.{JTextField, JComponent, JPanel, JLabel}
import model.{RampModel}
import swing.MyCheckBox
import umd.cs.piccolo.PNode
import scalacommon.math.Vector2D

class ForceChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel) extends PNode {
  val parallelAppliedForceVariable = new DefaultTemporalVariable() {
    override def setValue(value: Double) = model.bead.parallelAppliedForce = value
  }
  model.stepListeners += (() => parallelAppliedForceVariable.addValue(model.bead.appliedForce.dot(new Vector2D(model.bead.getVelocityVectorDirection)), model.getTime))

  def createVariable(getter: () => Vector2D) = {
    val variable = new DefaultTemporalVariable()
    model.stepListeners += (() => variable.addValue(getter().dot(new Vector2D(model.bead.getVelocityVectorDirection)), model.getTime))
    variable
  }

  val parallelFriction = createVariable(() => model.bead.frictionForce)
  val gravityForce = createVariable(() => model.bead.gravityForce)
  val wallForce = createVariable(() => model.bead.wallForce)

  val recordableModel = new RecordableModel() {
    def getState = "hello"

    def resetTime = {}

    def clear = {}

    def setState(o: Any) = {}

    def stepInTime(simulationTimeChange: Double) = {}
  }
  val timeseriesModel = new TimeSeriesModel(recordableModel, new ConstantDtClock(30, 1.0))
  val updateableObject = new UpdateableObject {
    def setUpdateStrategy(updateStrategy: UpdateStrategy) = {}
  }
  val appliedForceSeries = new ControlGraphSeries("<html>F<sub>applied</sub></html>", RampDefaults.appliedForceColor, "Fa", "N", "", parallelAppliedForceVariable)
  val frictionSeries = new ControlGraphSeries("<html>F<sub>friction</sub></html>", RampDefaults.frictionForceColor, "Ff", "N", "", parallelFriction)
  val gravitySeries = new ControlGraphSeries("<html>F<sub>gravity</sub></html>", RampDefaults.gravityForceColor, "Fg", "N", "", gravityForce)
  val wallSeries = new ControlGraphSeries("<html>F<sub>wall</sub></html>", RampDefaults.wallForceColor, "Fw", "N", "", wallForce)
  val parallelForceChart = new MotionControlGraph(canvas, appliedForceSeries, "label", "title", -2000, 2000, true, timeseriesModel, updateableObject) {
    setDomainUpperBound(20)
    getJFreeChartNode.setBuffered(false)
    getJFreeChartNode.setPiccoloSeries() //works better on an unbuffered chart
    addSeries(frictionSeries)
    addSeries(gravitySeries)
    addSeries(wallSeries)
  }

  def addListener(series: ControlGraphSeries, listener: () => Unit) = {
    series.addListener(new ControlGraphSeries.Adapter() {
      override def visibilityChanged = listener()
    })
  }

  def createFont = new PhetFont(16, true)

  class SeriesControlSelectorBox(series: ControlGraphSeries) extends MyCheckBox(series.getTitle, series.setVisible(_), series.isVisible, addListener(series, _)) {
    peer.setFont(createFont)
    peer.setForeground(series.getColor)
    peer.setBackground(RampDefaults.EARTH_COLOR)
  }

  def createLabel(series: ControlGraphSeries) = {
    val label = new JLabel()
    label.setBackground(RampDefaults.EARTH_COLOR)
    label.setFont(createFont)
    label.setForeground(series.getColor)
    series.getTemporalVariable.addListener(new ITemporalVariable.ListenerAdapter() {
      override def valueChanged = updateLabel()
    })
    def updateLabel() = label.setText("= " + new DefaultDecimalFormat("0.00").format(series.getTemporalVariable.getValue) + " " + series.getUnits)

    updateLabel()
    label
  }

  def createEditableLabel(series: ControlGraphSeries) = {
    val panel = new JPanel
    val textField = new JTextField(6)
    textField.addActionListener(new ActionListener() {
      def actionPerformed(e: ActionEvent) = {
        setValueFromText()
      }
    })
    def setValueFromText() = try {
      series.getTemporalVariable.setValue(new DefaultDecimalFormat("0.00").parse(textField.getText).doubleValue)
    } catch {
      case re: Exception => {}
    }
    textField.addFocusListener(new FocusListener() {
      def focusGained(e: FocusEvent) = {}

      def focusLost(e: FocusEvent) = setValueFromText()
    })

    textField.setFont(createFont)
    textField.setForeground(series.getColor)
    series.getTemporalVariable.addListener(new ITemporalVariable.ListenerAdapter() {
      override def valueChanged = updateLabel()
    })
    def updateLabel() = textField.setText(new DefaultDecimalFormat("0.00").format(series.getTemporalVariable.getValue))

    updateLabel()
    textField

    panel.add(textField)
    val unitsLabel = new JLabel(series.getUnits)
    unitsLabel.setFont(createFont)
    unitsLabel.setForeground(series.getColor)
    unitsLabel.setBackground(RampDefaults.EARTH_COLOR)
    panel.add(unitsLabel)
    panel.setBackground(RampDefaults.EARTH_COLOR)
    panel
  }

  class SeriesSelectionControl extends VerticalLayoutPanel {
    setBackground(RampDefaults.EARTH_COLOR)
    val jLabel = new JLabel("Parallel Forces (N)")
    jLabel.setFont(new PhetFont(20, true))
    jLabel.setBackground(RampDefaults.EARTH_COLOR)
    add(jLabel)
    val grid = new JPanel(new GridLayout(4, 2))
    grid.setBackground(RampDefaults.EARTH_COLOR)

    def addToGrid(series: ControlGraphSeries): Unit = {
      addToGrid(series, createLabel)
    }

    def addToGrid(series: ControlGraphSeries, labelMaker: ControlGraphSeries => JComponent): Unit = {
      grid.add(new SeriesControlSelectorBox(series).peer)
      grid.add(labelMaker(series))
    }
    addToGrid(appliedForceSeries, createEditableLabel)
    addToGrid(frictionSeries)
    addToGrid(gravitySeries)
    addToGrid(wallSeries)

    add(grid)
  }
  parallelForceChart.addControl(new SeriesSelectionControl)

  //  val y = new MotionControlGraph(canvas, controlGraphSeries, "label", "title", 0, 10, true, timeseriesModel, updateableObject) {
  //    setDomainUpperBound(20)
  //    getJFreeChartNode.setBuffered(false)
  //    getJFreeChartNode.setPiccoloSeries()
  //  }
  //  val set = new GraphSetNode(new GraphSetModel(new GraphSuite(Array(new MinimizableControlGraph("x", x), new MinimizableControlGraph("y", y)))))
  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(Array(new MinimizableControlGraph("Parallel Forces(N)", parallelForceChart))))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
  }
  graphSetNode.setAlignedLayout()

  addChild(graphSetNode)

  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    val viewBounds = transform.getViewBounds
    val h = viewBounds.getHeight - viewLoc.y
    graphSetNode.setBounds(viewBounds.getX, viewLoc.y, viewBounds.getWidth, h)
  }
  updatePosition()
}