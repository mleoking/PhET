package edu.colorado.phet.motionseries.charts

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
import java.lang.Math.PI
import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode
import edu.colorado.phet.common.phetcommon.model.MutableBoolean

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
  new ForcesAndMotionMinimizableControlChart(model),
  new MinimizableControlChart("properties.acceleration".translate, new SingleSeriesChart(model, () => model.bead.acceleration, 50, "m/s/s", MotionSeriesDefaults.accelerationColor, "properties.acceleration".translate).chart) {setMaximized(false)}, //todo: il8n
  new MinimizableControlChart("properties.velocity".translate, new SingleSeriesChart(model, () => model.bead.velocity, 25, "m/s", MotionSeriesDefaults.velocityColor, "properties.velocity".translate).chart) {setMaximized(false)}, //todo: il8n
  new MinimizableControlChart("properties.position".translate, new SingleSeriesChart(model, () => model.bead.position, 10, "m", MotionSeriesDefaults.positionColor, "properties.position".translate).chart) {setMaximized(false)})) { //todo: il8n
  canvas.addComponentListener(new ComponentAdapter { //todo: remove duplicate code from above
    override def componentResized(e: ComponentEvent) = {
      val insetX = 6
      val insetY = 6
      val chartProportionY = 0.7;
      setBounds(0 + insetX / 2, canvas.getHeight * (1 - chartProportionY) + insetY / 2, canvas.getWidth - insetX, canvas.getHeight * chartProportionY - insetY)
    }
  })
}

class SingleSeriesChart(motionSeriesModel: MotionSeriesModel, _value: () => Double, maxY: Double, units: String, color: Color, title: String) {
  val temporalChart = new TemporalChart(new java.awt.geom.Rectangle2D.Double(0, -maxY, 20, maxY * 2), motionSeriesModel.chartCursor)
  val chart = new ControlChart(new PNode() {
    addChild(new ShadowHTMLNode(title) {
      setColor(color)
      setFont(new PhetFont(16, true))
      setOffset(0, 20) //To help center it on the chart, so that we don't collide with controls from chart above, and so we don't have to shrink the charts.
    })
  }, new PNode(), temporalChart, new ChartZoomControlNode(temporalChart))

  val variable = new MutableDouble(_value()) {
    motionSeriesModel.stepListeners += (() => {value = _value()})
  }
  val series = new MSDataSeries("properties.acceleration".translate, color, units, variable, motionSeriesModel)
  temporalChart.addDataSeries(series, series.color)
}

class RampForceMinimizableControlChart(motionSeriesModel: MotionSeriesModel) extends MinimizableControlChart("forces.parallel-title-with-units".translate, new RampControlChart(motionSeriesModel).chart)
class ForcesAndMotionMinimizableControlChart(motionSeriesModel: MotionSeriesModel) extends MinimizableControlChart("forces.parallel-title-with-units".translate, new ForcesAndMotionControlChart(motionSeriesModel).chart)

class RampControlChart(motionSeriesModel: MotionSeriesModel) extends MotionSeriesControlChart(motionSeriesModel) {
  def addSerieses() = {
    temporalChart.addDataSeries(appliedForceSeries, appliedForceSeries.color)
    temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
    temporalChart.addDataSeries(gravityForceSeries, gravityForceSeries.color)
    temporalChart.addDataSeries(wallForceSeries, wallForceSeries.color)
    temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
    temporalChart.addDataSeries(totalForceSeries, totalForceSeries.color)
  }

  def additionalSerieses = frictionForceSeries :: gravityForceSeries :: wallForceSeries :: totalForceSeries :: Nil
}
class ForcesAndMotionControlChart(motionSeriesModel: MotionSeriesModel) extends MotionSeriesControlChart(motionSeriesModel) {
  def addSerieses() = {
    temporalChart.addDataSeries(appliedForceSeries, appliedForceSeries.color)
    temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
    temporalChart.addDataSeries(wallForceSeries, wallForceSeries.color)
    temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
    temporalChart.addDataSeries(totalForceSeries, totalForceSeries.color)
  }

  def additionalSerieses = frictionForceSeries :: wallForceSeries :: totalForceSeries :: Nil

  override def gridSize = 4
}

abstract class MotionSeriesControlChart(motionSeriesModel: MotionSeriesModel) {
  def addSerieses(): Unit

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

  val appliedForceSeries = new MSDataSeries("<html>F<sub>applied</sub></html>", MotionSeriesDefaults.appliedForceColor, "N", parallelAppliedForceVariable, motionSeriesModel)
  val frictionForceSeries = new MSDataSeries("<html>F<sub>friction</sub></html>", MotionSeriesDefaults.frictionForceColor, "N", frictionVariable, motionSeriesModel)
  val gravityForceSeries = new MSDataSeries("<html>F<sub>gravity ||</sub></html>", MotionSeriesDefaults.gravityForceColor, "N", gravityVariable, motionSeriesModel)
  val wallForceSeries = new MSDataSeries("<html>F<sub>wall</sub></html>", MotionSeriesDefaults.wallForceColor, "N", wallVariable, motionSeriesModel)
  val totalForceSeries = new MSDataSeries("<html>F<sub>total ||</sub></html>", MotionSeriesDefaults.totalForceColor, "N", totalForceVariable, motionSeriesModel) //todo: il8n for units and names

  addSerieses();

  def additionalSerieses: List[MSDataSeries]

  def gridSize = 5

  val controlPanel = new PNode {
    addChild(new PSwing(new SeriesSelectionControl("forces.parallel-title-with-units".translate, gridSize) {
      addToGrid(appliedForceSeries, createEditableLabel)
      for (s <- additionalSerieses) addToGrid(s)
    }))
    val goButton = new GoButton(motionSeriesModel, new MutableBoolean(true))
    goButton.setOffset(getFullBounds.getMaxX - goButton.getFullBounds.getWidth * 2, getFullBounds.getMaxY)
    addChild(goButton)
  }

  val chart = new ControlChart(controlPanel, createSliderNode(temporalChart), temporalChart, new ChartZoomControlNode(temporalChart))

  def createSliderNode(chart: TemporalChart) =
    new TemporalChartSliderNode(chart, appliedForceSeries.color) {
      val outer = this
      addListener(new MotionSliderNode.Adapter() {
        override def sliderDragged(value: java.lang.Double) = {parallelAppliedForceVariable.value = value.doubleValue}
      })
      parallelAppliedForceVariable.addListener(() => {setValue(parallelAppliedForceVariable.value)})
      val label = new ShadowHTMLNode("controls.applied-force-x".translate) {
        setFont(new PhetFont(16, true))
        setColor(appliedForceSeries.color)
        setShadowOffset(-1, 1)
        rotate(-PI / 2)
      }
      val rotated = new PNode {
        addChild(label)
        translate(-getFullBounds.getX, -getFullBounds.getY)
      }
      val parent = new PNode {
        addChild(rotated)
        setOffset(-rotated.getFullBounds.getWidth, outer.getTrackNode.getFullBounds.getHeight / 2 - rotated.getFullBounds.getHeight / 2)
      }
      outer.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
        def propertyChange(evt: PropertyChangeEvent) = {
          parent.setOffset(-getSliderThumb.getFullBounds.getWidth / 2 - parent.getFullBounds.getWidth, outer.getTrackNode.getFullBounds.getHeight / 2 - rotated.getFullBounds.getHeight / 2)
        }
      })
      addChild(parent)
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