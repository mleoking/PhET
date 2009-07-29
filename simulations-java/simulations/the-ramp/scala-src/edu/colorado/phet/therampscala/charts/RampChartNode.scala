package edu.colorado.phet.therampscala.charts

import common.phetcommon.util.DefaultDecimalFormat
import common.phetcommon.view.util.{PhetFont}
import common.motion.graphs._
import common.motion.model._
import common.phetcommon.model.clock.ConstantDtClock
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.{ShadowHTMLNode}
import common.piccolophet.{PhetPCanvas}
import common.timeseries.model.{RecordableModel, TimeSeriesModel}
import java.awt.event.{FocusEvent, FocusListener, ActionEvent, ActionListener}
import java.awt.geom.Point2D
import java.awt.{Color}
import javax.swing.{JTextField, JPanel, JLabel}
import model.{RampModel}
import umd.cs.piccolo.PNode
import scalacommon.math.Vector2D

object Defaults {
  def createFont = new PhetFont(15, true)

  def addListener(series: ControlGraphSeries, listener: () => Unit) = {
    series.addListener(new ControlGraphSeries.Adapter() {
      override def visibilityChanged = listener()
    })
  }
}

class RampChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel, showEnergyGraph: Boolean) extends PNode {
  val parallelAppliedForceVariable = new DefaultTemporalVariable() {
    override def setValue(value: Double) = model.bead.parallelAppliedForce = value
  }
  model.stepListeners += (() => parallelAppliedForceVariable.addValue(model.bead.appliedForce.dot(new Vector2D(model.bead.getVelocityVectorDirection)), model.getTime))

  def createVariable(getter: () => Double) = {
    val variable = new DefaultTemporalVariable()
    model.stepListeners += (() => variable.addValue(getter(), model.getTime))
    variable
  }

  def createParallelVariable(getter: () => Vector2D) = {
    val variable = new DefaultTemporalVariable()
    model.stepListeners += (() => variable.addValue(getter().dot(new Vector2D(model.bead.getVelocityVectorDirection)), model.getTime))
    variable
  }

  val parallelFriction = createParallelVariable(() => model.bead.frictionForce)
  val gravityForce = createParallelVariable(() => model.bead.gravityForce)
  val wallForce = createParallelVariable(() => model.bead.wallForce)

  val energyVariable = createVariable(() => model.bead.getTotalEnergy)
  val keVariable = createVariable(() => model.bead.getKineticEnergy)
  val peVariable = createVariable(() => model.bead.getPotentialEnergy)
  val thermalEnergyVariable = createVariable(() => model.bead.getThermalEnergy)
  val appliedWorkVariable = createVariable(() => model.bead.getAppliedWork)
  val gravityWorkVariable = createVariable(() => model.bead.getGravityWork)
  val frictionWorkVariable = createVariable(() => model.bead.getFrictiveWork)

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

  val totalEnergySeries = new ControlGraphSeries("<html>E<sub>total</sub></html>", RampDefaults.totalEnergyColor, "Etot", "J", "", energyVariable)
  val keSeries = new ControlGraphSeries("<html>E<sub>kin</sub></html>", RampDefaults.kineticEnergyColor, "KE", "J", "", keVariable)
  val peSeries = new ControlGraphSeries("<html>E<sub>pot</sub></html>", RampDefaults.potentialEnergyColor, "PE", "J", "", peVariable)
  val thermalEnergySeries= new ControlGraphSeries("<html>E<sub>therm</sub></html>", RampDefaults.thermalEnergyColor, "PE", "J", "", thermalEnergyVariable)
  val appliedWorkSeries = new ControlGraphSeries("<html>W<sub>applied</sub></html>", RampDefaults.appliedWorkColor, "Wapp", "J", "", appliedWorkVariable)
  val gravityWorkSeries = new ControlGraphSeries("<html>W<sub>gravity</sub></html>", RampDefaults.appliedWorkColor, "Wgrav", "J", "", gravityWorkVariable)
  val frictionWorkSeries= new ControlGraphSeries("<html>W<sub>friction</sub></html>", RampDefaults.frictionWorkColor, "Wfric", "J", "", frictionWorkVariable)

  class RampGraph(defaultSeries: ControlGraphSeries) extends MotionControlGraph(canvas, defaultSeries, "label", "title", -2000, 2000, true, timeseriesModel, updateableObject) {
    getJFreeChartNode.setBuffered(false)
    getJFreeChartNode.setPiccoloSeries() //works better on an unbuffered chart
    override def createSliderNode(thumb: PNode, highlightColor: Color) = {
      new JFreeChartSliderNode(getJFreeChartNode, thumb, highlightColor) {
        val text = new ShadowHTMLNode(appliedForceSeries.getTitle)
        text.setFont(new PhetFont(18, true))
        text.setColor(appliedForceSeries.getColor)
        text.rotate(-java.lang.Math.PI / 2)
        text.setOffset(-text.getFullBounds.getWidth * 1.5, getGlobalFullBounds.getHeight / 4 - text.getFullBounds.getHeight / 2)
        addChild(text)
      }
    }
    //todo: better support for hiding graph time control node
    override def createGraphTimeControlNode(timeSeriesModel: TimeSeriesModel) = new GraphTimeControlNode(timeSeriesModel) {
      override def setEditable(editable: Boolean) = {
        super.setEditable(false)
      }
    }

    override def createReadoutTitleNode(series: ControlGraphSeries) = null
  }

  val parallelForceControlGraph = new RampGraph(appliedForceSeries) {
    setDomainUpperBound(20)
    addSeries(frictionSeries)
    addSeries(gravitySeries)
    addSeries(wallSeries)
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

    textField.setFont(Defaults.createFont)
    textField.setForeground(series.getColor)
    series.getTemporalVariable.addListener(new ITemporalVariable.ListenerAdapter() {
      override def valueChanged = updateLabel()
    })
    def updateLabel() = textField.setText(new DefaultDecimalFormat("0.00").format(series.getTemporalVariable.getValue))

    updateLabel()
    textField

    panel.add(textField)
    val unitsLabel = new JLabel(series.getUnits)
    unitsLabel.setFont(Defaults.createFont)
    unitsLabel.setForeground(series.getColor)
    unitsLabel.setBackground(RampDefaults.EARTH_COLOR)
    panel.add(unitsLabel)
    panel.setBackground(RampDefaults.EARTH_COLOR)
    panel
  }

  parallelForceControlGraph.addControl(new SeriesSelectionControl("Parallel Forces (N)", 4) {
    addToGrid(appliedForceSeries, createEditableLabel)
    addToGrid(frictionSeries)
    addToGrid(gravitySeries)
    addToGrid(wallSeries)
  })

  val workEnergyGraph = new RampGraph(totalEnergySeries) {
    setEditable(false)
    setDomainUpperBound(20)
    getJFreeChartNode.setBuffered(false)
    getJFreeChartNode.setPiccoloSeries()
    addSeries(keSeries)
    addSeries(peSeries)
    addSeries(thermalEnergySeries)
    addSeries(appliedWorkSeries)
    addSeries(gravityWorkSeries)
    addSeries(frictionWorkSeries)
  }
  workEnergyGraph.addControl(new SeriesSelectionControl("Work/Energy (J)", 7) {
    addToGrid(totalEnergySeries)
    addToGrid(keSeries)
    addToGrid(peSeries)
    addToGrid(thermalEnergySeries)
    addToGrid(appliedWorkSeries)
    addToGrid(gravityWorkSeries)
    addToGrid(frictionWorkSeries)
  })

  val graphs = if (showEnergyGraph) Array(new MinimizableControlGraph("Parallel Forces(N)", parallelForceControlGraph), new MinimizableControlGraph("Work/Energy", workEnergyGraph))
  else Array(new MinimizableControlGraph("Parallel Forces(N)", parallelForceControlGraph))

  val graphSetNode = new GraphSetNode(new GraphSetModel(new GraphSuite(graphs))) {
    override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
    setAlignedLayout()
  }

  addChild(graphSetNode)

  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    val viewBounds = transform.getViewBounds
    val h = viewBounds.getHeight - viewLoc.y
    graphSetNode.setBounds(viewBounds.getX, viewLoc.y, viewBounds.getWidth, h)
  }
  updatePosition()
}