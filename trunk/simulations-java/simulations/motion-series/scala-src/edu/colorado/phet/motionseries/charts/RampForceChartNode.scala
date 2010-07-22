package edu.colorado.phet.motionseries.charts

import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.motion.charts._
import java.awt.event.{ComponentEvent, ComponentAdapter}
import java.awt.Color
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.common.piccolophet.PhetPCanvas

/**
 * @author Sam Reid
 */
class RampForceChartNode(canvas: PhetPCanvas, motionSeriesModel: MotionSeriesModel) extends MultiControlChart(Array(new RampForceMinimizableControlChart(motionSeriesModel))) {
  canvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = {
      val insetX = 6
      val insetY = 6
      setBounds(0 + insetX / 2, canvas.getHeight / 2 + insetY / 2, canvas.getWidth - insetX, canvas.getHeight / 2 - insetY)
    }
  })
}

class ForcesAndMotionChartNode(canvas: PhetPCanvas, model: MotionSeriesModel) extends MultiControlChart(Array(
  new RampForceMinimizableControlChart(model),
  new MinimizableControlChart("properties.acceleration".translate, new SingleSeriesChart(model, () => model.bead.acceleration, 50).chart) {setMaximized(false)},
  new MinimizableControlChart("properties.velocity".translate, new SingleSeriesChart(model, () => model.bead.velocity, 25).chart) {setMaximized(false)},
  new MinimizableControlChart("properties.position".translate, new SingleSeriesChart(model, () => model.bead.position, 10).chart) {setMaximized(false)})) {
  canvas.addComponentListener(new ComponentAdapter { //todo: remove duplicate code from above
    override def componentResized(e: ComponentEvent) = {
      val insetX = 6
      val insetY = 6
      val chartProportionY = 0.7;
      setBounds(0 + insetX / 2, canvas.getHeight * (1 - chartProportionY) + insetY / 2, canvas.getWidth - insetX, canvas.getHeight * chartProportionY - insetY)
    }
  })
}

class SingleSeriesChart(motionSeriesModel: MotionSeriesModel, _value: () => Double, maxY: Double) {
  val temporalChart = new TemporalChart(new java.awt.geom.Rectangle2D.Double(0, -maxY, 20, maxY * 2), motionSeriesModel.chartCursor)
  val chart = new ControlChart(new PNode(), new PNode(), temporalChart, new ChartZoomControlNode(temporalChart))

  val accelerationVariable = new MutableDouble(_value()) {
    motionSeriesModel.stepListeners += (() => {value = _value()})
  }
  val accelerationSeries = new MSDataSeries("properties.acceleration".translate, MotionSeriesDefaults.accelerationColor, "m/s/s", accelerationVariable, motionSeriesModel) //TODO il8n for units
  temporalChart.addDataSeries(accelerationSeries, accelerationSeries.color)
}

class RampForceMinimizableControlChart(motionSeriesModel: MotionSeriesModel) extends MinimizableControlChart("forces.parallel-title-with-units".translate, new RampForceControlChart(motionSeriesModel).chart)

class RampForceControlChart(motionSeriesModel: MotionSeriesModel) {
  val temporalChart = new TemporalChart(new java.awt.geom.Rectangle2D.Double(0, -2000, 20, 4000), motionSeriesModel.chartCursor)

  def parallelFriction = motionSeriesModel.bead.frictionForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector

  def parallelGravity = motionSeriesModel.bead.gravityForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector

  def parallelWall = motionSeriesModel.bead.wallForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector

  def parallelTotalForce = motionSeriesModel.bead.totalForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector

  val parallelAppliedForceVariable = new MutableDouble(motionSeriesModel.bead.parallelAppliedForce) {
    motionSeriesModel.bead.parallelAppliedForceListeners += (() => {value = motionSeriesModel.bead.parallelAppliedForce})
    addListener(() => {motionSeriesModel.bead.parallelAppliedForce = value})
  }
  val frictionVariable = new MutableDouble(parallelFriction) {
    motionSeriesModel.stepListeners += (() => {value = parallelFriction})
  }
  val gravityVariable = new MutableDouble(parallelGravity) {
    motionSeriesModel.stepListeners += (() => {value = parallelGravity})
  }
  val wallVariable = new MutableDouble(parallelWall) {
    motionSeriesModel.stepListeners += (() => {value = parallelWall})
  }
  val totalForceVariable = new MutableDouble(parallelTotalForce) {
    motionSeriesModel.stepListeners += (() => {value = parallelTotalForce})
  }

  val appliedForceSeries = new MSDataSeries("<html>F<sub>applied ||</sub></html>", MotionSeriesDefaults.appliedForceColor, "N", parallelAppliedForceVariable, motionSeriesModel)
  val frictionForceSeries = new MSDataSeries("<html>F<sub>friction ||</sub></html>", MotionSeriesDefaults.frictionForceColor, "N", frictionVariable, motionSeriesModel)
  val gravityForceSeries = new MSDataSeries("<html>F<sub>gravity ||</sub></html>", MotionSeriesDefaults.gravityForceColor, "N", gravityVariable, motionSeriesModel)
  val wallForceSeries = new MSDataSeries("<html>F<sub>wall ||</sub></html>", MotionSeriesDefaults.wallForceColor, "N", wallVariable, motionSeriesModel)
  val totalForceSeries = new MSDataSeries("<html>F<sub>total ||</sub></html>", MotionSeriesDefaults.totalForceColor, "N", totalForceVariable, motionSeriesModel) //todo: il8n for units and names

  temporalChart.addDataSeries(appliedForceSeries, appliedForceSeries.color)
  temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
  temporalChart.addDataSeries(gravityForceSeries, gravityForceSeries.color)
  temporalChart.addDataSeries(wallForceSeries, wallForceSeries.color)
  temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
  temporalChart.addDataSeries(totalForceSeries, totalForceSeries.color)

  val additionalSerieses: List[MSDataSeries] = frictionForceSeries :: gravityForceSeries :: wallForceSeries :: totalForceSeries :: Nil
  val controlPanel = new PNode {
    addChild(new PSwing(new SeriesSelectionControl("forces.parallel-title-with-units".translate, 5) {
      addToGrid(appliedForceSeries, createEditableLabel)
      for (s <- additionalSerieses) addToGrid(s)
    }))
  }

  val chart = new ControlChart(controlPanel, createSliderNode(new PText("hello"), Color.green, temporalChart), temporalChart, new ChartZoomControlNode(temporalChart))

  def createSliderNode(thumb: PNode, highlightColor: Color, chart: TemporalChart) =
    new TemporalChartSliderNode(chart, Color.green) { //TODO: add vertical label to the slider node
      addListener(new MotionSliderNode.Adapter() {
        override def sliderDragged(value: java.lang.Double) = {parallelAppliedForceVariable.value = value.doubleValue}
      })
      parallelAppliedForceVariable.addListener(() => {setValue(parallelAppliedForceVariable.value)})
    }
}

class MutableDouble(private var _value: Double) extends Observable {
  def value = _value

  def value_=(x: Double) = {
    _value = x
    notifyListeners()
  }

  def apply() = value
}

class MSDataSeries(_title: String, _color: Color, _units: String, value: MutableDouble, motionSeriesModel: MotionSeriesModel) extends TemporalDataSeries {
  def title = _title
  motionSeriesModel.stepListeners += (() => {addPoint(value(), motionSeriesModel.getTime)})

  def addValueChangeListener(listener: () => Unit) = value.addListener(listener)

  def getValue = value()

  def setValue(v: Double) = {value.value = v}

  def units = _units

  def color = _color
}