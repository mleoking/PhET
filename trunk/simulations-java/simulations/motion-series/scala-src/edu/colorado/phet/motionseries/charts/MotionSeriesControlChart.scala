package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.motion.charts._
import java.awt.event.{ComponentEvent, ComponentAdapter}
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.lang.Math.PI
import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel.{HistoryClearListener, HistoryRemainderClearListener}
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.common.piccolophet.nodes.{PhetPPath, ShadowHTMLNode}
import java.awt.geom.{RoundRectangle2D, Rectangle2D}
import java.text.DecimalFormat
import java.awt.{BasicStroke, Color}
import edu.umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty

object ChartDefaults {
  val LABEL_OFFSET_DY = 5 //distance between bottom of the chart and top of the time axis label
}

/**
 * Component resize code to make sure the chart has the right bounds
 */
class ChartComponentListener(canvas: PhetPCanvas, chartProportionY: Double, node: PNode, labelNode: PNode = null) extends ComponentAdapter {
  val insetX = 0.6
  val insetY = insetX
  val reservedLabelSpaceY = ChartDefaults.LABEL_OFFSET_DY + (if (labelNode != null) labelNode.getFullBounds.height else 0)

  override def componentResized(e: ComponentEvent) = {
    node.setBounds(0 + insetX / 2, canvas.getHeight * (1 - chartProportionY) + insetY / 2, canvas.getWidth - insetX, canvas.getHeight * chartProportionY - insetY - reservedLabelSpaceY)
  }
}

class MotionSeriesMultiControlChart(canvas: PhetPCanvas, model: MotionSeriesModel, charts: Array[MinimizableControlChart], sizeFraction: Double) extends MultiControlChart(charts) {
  val labelNode = new PNode {
    addChild(new PText("chart.time-axis-label".translate) {
      setFont(new PhetFont(14, true))
    })
  }
  addChild(labelNode)

  //Show the time axis label under the bottom-most maximized chart
  val updateLabelLocation = new PropertyChangeListener() {
    def propertyChange(evt: PropertyChangeEvent) = {
      val maximizedCharts = for (c <- charts if c.getMaximized.getValue.booleanValue) yield c
      labelNode.setVisible(!maximizedCharts.isEmpty) //only show the time axis label if some charts are showing
      if (!maximizedCharts.isEmpty) {
        val bottomChart = maximizedCharts.last
        val bounds = bottomChart.getChartNode.getFullBounds
        bottomChart.getChartNode.localToGlobal(bounds)
        globalToLocal(bounds)
        labelNode.setOffset(bottomChart.getChartNode.getFullBounds.getCenterX - labelNode.getFullBounds.getWidth / 2, bounds.getMaxY + ChartDefaults.LABEL_OFFSET_DY)
      }
    }
  }
  for (chart <- charts) {
    chart.getChartNode.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, updateLabelLocation)
  }

  canvas.addComponentListener(new ChartComponentListener(canvas, sizeFraction, this, labelNode))
  model.addResetListener(resetAll)

  model.addHistoryClearListener(new HistoryClearListener() {
    def historyCleared = {
      clear()
    }
  })
}

class RampForceChartNode(canvas: PhetPCanvas, motionSeriesModel: MotionSeriesModel) extends MotionSeriesMultiControlChart(canvas, motionSeriesModel, Array(new RampForceMinimizableControlChart(motionSeriesModel)), 0.5)

class ForcesAndMotionChartNode(canvas: PhetPCanvas, model: MotionSeriesModel) extends MotionSeriesMultiControlChart(canvas, model, Array(
  new ForcesAndMotionMinimizableControlChart(model),
  new MinimizableControlChart("properties.acceleration".translate, new SingleSeriesChart(model, () => model.motionSeriesObject.acceleration, 50, "properties.acceleration.units".translate, MotionSeriesDefaults.accelerationColor, "properties.acceleration".translate).chart, false),
  new MinimizableControlChart("properties.velocity".translate, new SingleSeriesChart(model, () => model.motionSeriesObject.state.velocity, 25, "properties.velocity.units".translate, MotionSeriesDefaults.velocityColor, "properties.velocity".translate).chart, false),
  new MinimizableControlChart("properties.position".translate, new SingleSeriesChart(model, () => model.motionSeriesObject.state.position, 10, "properties.position.units".translate, MotionSeriesDefaults.positionColor, "properties.position".translate).chart, false)),
  0.7)

class SingleSeriesChart(motionSeriesModel: MotionSeriesModel, _value: () => Double, maxY: Double, units: String, color: Color, title: String) {
  val variable = new MutableDouble(_value()) {
    motionSeriesModel.stepListeners += (() => {value = _value()})
  }
  val temporalChart = new TemporalChart(new Rectangle2D.Double(0, -maxY, 20, maxY * 2), new Rectangle2D.Double(0, -5, 2, 10), new Rectangle2D.Double(0, -10000, 20, 20000), motionSeriesModel.chartCursor)
  val chart = new ControlChart(new PNode() {
    val titleNode = new ShadowHTMLNode(title) {
      setColor(color)
      setFont(new PhetFont(16, true))
      setOffset(0, 20) //To help center it on the chart, so that we don't collide with controls from chart above, and so we don't have to shrink the charts.
    }
    addChild(titleNode)

    val inset = 5
    val readoutTextNode = new PText("0.0 " + units) {
      setFont(new PhetFont(14, true))
      setOffset(10, titleNode.getFullBounds.getMaxY + 2 + inset)
      setTextPaint(color)
    }

    val textBackgroundNode = new PhetPPath(Color.white, new BasicStroke, color)

    addChild(textBackgroundNode)
    addChild(readoutTextNode)

    def updateReadout() = {
      readoutTextNode setText "properties.format.value-units".messageformat(new DecimalFormat("0.00").format(variable()), units)
      val r = readoutTextNode.getFullBounds
      textBackgroundNode.setPathTo(new RoundRectangle2D.Double(r.x - inset, r.y - inset,
        Math.max(textBackgroundNode.getPathReference.getBounds.width, r.width + inset * 2), //allow the background to grow but not shrink, it is too distracting to have it resize too much 
        r.height + inset * 2, 8, 8))
    }
    updateReadout()

    variable.addListener(updateReadout)
  }, new PNode(), temporalChart, new ChartZoomControlNode(temporalChart))

  val series = new MotionSeriesDataSeries(title, color, units, variable, motionSeriesModel, true)
  temporalChart.addDataSeries(series, series.color)

  motionSeriesModel.addResetListener(() => series.clear())
  motionSeriesModel.addHistoryClearListener(new HistoryClearListener() {
    def historyCleared = {
      series.clear()
    }
  })
}

class RampForceMinimizableControlChart(motionSeriesModel: MotionSeriesModel) extends MinimizableControlChart("forces.parallel-title-with-units".translate, new RampControlChart(motionSeriesModel).chart)
class ForcesAndMotionMinimizableControlChart(motionSeriesModel: MotionSeriesModel) extends MinimizableControlChart("forces.parallel-title-with-units".translate, new ForcesAndMotionControlChart(motionSeriesModel).chart)

class RampControlChart(motionSeriesModel: MotionSeriesModel) extends MotionSeriesControlChart(motionSeriesModel, "forces.sum-parallel".translate) {
  def addSerieses() = {
    temporalChart.addDataSeries(appliedForceSeries, appliedForceSeries.color)
    temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
    temporalChart.addDataSeries(gravityForceSeries, gravityForceSeries.color)
    temporalChart.addDataSeries(wallForceSeries, wallForceSeries.color)
    temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
    temporalChart.addDataSeries(sumForceSeries, sumForceSeries.color)
  }

  def additionalSerieses = frictionForceSeries :: gravityForceSeries :: wallForceSeries :: sumForceSeries :: Nil
}
class ForcesAndMotionControlChart(motionSeriesModel: MotionSeriesModel) extends MotionSeriesControlChart(motionSeriesModel, "forces.sum".translate) {
  def addSerieses() = {
    temporalChart.addDataSeries(appliedForceSeries, appliedForceSeries.color)
    temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
    temporalChart.addDataSeries(wallForceSeries, wallForceSeries.color)
    temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
    temporalChart.addDataSeries(sumForceSeries, sumForceSeries.color)
  }

  def additionalSerieses = frictionForceSeries :: wallForceSeries :: sumForceSeries :: Nil

  override def gridSize = 4
}

abstract class MotionSeriesControlChart(motionSeriesModel: MotionSeriesModel, forcesSum: String) {
  motionSeriesModel.addResetListener(resetAll)
  def addSerieses(): Unit

  val temporalChart = new TemporalChart(new java.awt.geom.Rectangle2D.Double(0, -2000, 20, 4000), new Rectangle2D.Double(0, -5, 2, 10), new Rectangle2D.Double(0, -10000, 20, 20000), motionSeriesModel.chartCursor)

  def parallelFriction = motionSeriesModel.motionSeriesObject.frictionForceVector.vector2DModel() dot motionSeriesModel.motionSeriesObject.rampUnitVector

  def parallelGravity = motionSeriesModel.motionSeriesObject.gravityForceVector.vector2DModel() dot motionSeriesModel.motionSeriesObject.rampUnitVector

  def parallelWall = motionSeriesModel.motionSeriesObject.wallForceVector.vector2DModel() dot motionSeriesModel.motionSeriesObject.rampUnitVector

  def parallelTotalForce = motionSeriesModel.motionSeriesObject.totalForceVector.vector2DModel() dot motionSeriesModel.motionSeriesObject.rampUnitVector

  val parallelAppliedForceVariable = new MutableDouble(motionSeriesModel.motionSeriesObject.parallelAppliedForce) {
    motionSeriesModel.motionSeriesObject.parallelAppliedForceProperty.addListener(() => {value = motionSeriesModel.motionSeriesObject.parallelAppliedForce})
    addListener(() => {motionSeriesModel.motionSeriesObject.parallelAppliedForce = value})
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
  val sumForceVariable = new MutableDouble(parallelTotalForce) {
    motionSeriesModel.stepListeners += (() => {value = parallelTotalForce})
  }

  val N = "units.abbr.newtons".translate
  val appliedForceSeries = new MotionSeriesDataSeries("force.pattern".messageformat("forces.applied".translate), MotionSeriesDefaults.appliedForceColor, N, parallelAppliedForceVariable, motionSeriesModel, true)
  val frictionForceSeries = new MotionSeriesDataSeries("force.pattern".messageformat("forces.friction".translate), MotionSeriesDefaults.frictionForceColor, N, frictionVariable, motionSeriesModel, false)
  val gravityForceSeries = new MotionSeriesDataSeries("force.pattern".messageformat("forces.gravity-parallel".translate), MotionSeriesDefaults.gravityForceColor, N, gravityVariable, motionSeriesModel, false)
  val wallForceSeries = new MotionSeriesDataSeries("force.pattern".messageformat("forces.wall".translate), MotionSeriesDefaults.wallForceColor, N, wallVariable, motionSeriesModel, false)
  val sumForceSeries = new MotionSeriesDataSeries("force.pattern".messageformat(forcesSum), MotionSeriesDefaults.sumForceColor, N, sumForceVariable, motionSeriesModel, false)

  //todo: could move this reset function into the MSDataSeries
  def resetAll() = {
    appliedForceSeries.reset()
    frictionForceSeries.reset()
    gravityForceSeries.reset()
    wallForceSeries.reset()
    sumForceSeries.reset()
  }
  addSerieses();

  motionSeriesModel.addHistoryRemainderClearListener(new HistoryRemainderClearListener {
    def historyRemainderCleared() = {
      appliedForceSeries.clearPointsAfter(motionSeriesModel.getTime)
      frictionForceSeries.clearPointsAfter(motionSeriesModel.getTime)
      gravityForceSeries.clearPointsAfter(motionSeriesModel.getTime)
      wallForceSeries.clearPointsAfter(motionSeriesModel.getTime)
      sumForceSeries.clearPointsAfter(motionSeriesModel.getTime)
    }
  })
  motionSeriesModel.addHistoryClearListener(new HistoryClearListener() {
    def historyCleared = {
      appliedForceSeries.clear()
      frictionForceSeries.clear()
      gravityForceSeries.clear()
      wallForceSeries.clear()
      sumForceSeries.clear()
    }
  })

  def additionalSerieses: List[MotionSeriesDataSeries]

  def gridSize = 5

  val sliderNode = createSliderNode(temporalChart)
  val controlPanel = new PNode {
    val goButtonVisible = new BooleanProperty(false) //go button should become visible when user specifies a force by dragging the slider or typing in the text field
    motionSeriesModel.addResetListener(()=>goButtonVisible.reset())
    addChild(new PSwing(new SeriesSelectionControl("forces.parallel-title-with-units".translate, gridSize) {
      val editableLabel = new EditableLabel(appliedForceSeries) {
        override def setValueFromText() = {
          super.setValueFromText()
          goButtonVisible.set(true) //show the go button
        }
      }
      addToGrid(appliedForceSeries, editableLabel)
      for (s <- additionalSerieses) addToGrid(s)
    }))
    sliderNode.addInputEventListener(new PBasicInputEventHandler() {
      override def mouseDragged(event: PInputEvent) = {
        goButtonVisible.set(true)
      }
    })
    val goButton = new GoButton(motionSeriesModel, goButtonVisible)
    goButton.setOffset(getFullBounds.getMaxX - goButton.getFullBounds.getWidth * 2, getFullBounds.getMaxY)
    addChild(goButton)
  }

  val chart = new ControlChart(controlPanel, sliderNode, temporalChart, new ChartZoomControlNode(temporalChart))

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
  def this() = this (0.0)

  def value = _value

  def value_=(x: Double) = {
    if (_value != x) {
      _value = x
      notifyListeners()
    }
  }

  def apply() = value
}

class MotionSeriesDataSeries(_title: String, _color: Color, _units: String, value: MutableDouble, motionSeriesModel: MotionSeriesModel, val visible: Boolean) extends TemporalDataSeries {
  setVisible(visible)
  def title = _title
  motionSeriesModel.recordListeners += (() => {addPoint(value(), motionSeriesModel.getTime)})

  def addValueChangeListener(listener: () => Unit) = value.addListener(listener)

  //Convenience wrapper for scala clients
  def addMSDataSeriesListener(listener: () => Unit) = {
    addListener(new TemporalDataSeries.Adapter() {
      override def visibilityChanged = {
        listener()
      }
    })
  }

  def getValue = value()

  def setValue(v: Double) = {value.value = v}

  def units = _units

  def color = _color

  def reset() = setVisible(visible)
}