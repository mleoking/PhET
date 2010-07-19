package edu.colorado.phet.motionseries.charts

import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.motionseries.graphics.RampCanvas
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.motion.charts._
import java.awt.event.{ComponentEvent, ComponentAdapter}
import java.awt.Color
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.util.Observable

/**
 * @author Sam Reid
 */
class RampForceChartNode(rampCanvas: RampCanvas, motionSeriesModel: MotionSeriesModel) extends MultiControlChart(Array(new RampForceMinimizableControlChart(motionSeriesModel))) {
  rampCanvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = {
      setBounds(0, rampCanvas.getHeight / 2, rampCanvas.getWidth, rampCanvas.getHeight / 2)
    }
  })
}

class RampForceMinimizableControlChart(motionSeriesModel: MotionSeriesModel) extends MinimizableControlChart("title", new RampForceControlChart(motionSeriesModel).chart)

class RampForceControlChart(motionSeriesModel: MotionSeriesModel) {
  val temporalChart = new TemporalChart(new java.awt.geom.Rectangle2D.Double(0, -2000, 20, 4000), new ChartCursor())

  class MutableDouble(private var _value: Double) extends Observable {
    def value = _value

    def value_=(x: Double) = {
      _value = x
      notifyListeners()
    }

    def apply() = value
  }

  class MSControlGraphSeries(_title: String, _color: Color, _units: String, value: MutableDouble) extends TemporalDataSeries with MControlGraphSeries {
    motionSeriesModel.stepListeners += (() => {addPoint(value(), motionSeriesModel.getTime)})
    override def title = _title

    override def setVisible(b: Boolean) = {}

    override def isVisible() = true

    override def color = _color

    override def addValueChangeListener(listener: () => Unit) = {
            value.addListener(listener)
    }

    override def getValue = value()

    override def units = _units
  }

  val parallelAppliedForceVariable = new MutableDouble(motionSeriesModel.bead.parallelAppliedForce) {
    motionSeriesModel.bead.parallelAppliedForceListeners += (() => {value = motionSeriesModel.bead.parallelAppliedForce})
  }
  val frictionVariable = new MutableDouble(motionSeriesModel.bead.frictionForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector) {
    motionSeriesModel.bead.parallelAppliedForceListeners += (() => {value = motionSeriesModel.bead.parallelAppliedForce})
  }
  val gravityVariable = new MutableDouble(motionSeriesModel.bead.gravityForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector)
  val wallVariable = new MutableDouble(motionSeriesModel.bead.wallForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector)
  val appliedForceSeries = new MSControlGraphSeries("<html>F<sub>applied ||</sub></html>", MotionSeriesDefaults.appliedForceColor, "m/s/s", parallelAppliedForceVariable) {
  }
  val frictionForceSeries = new MSControlGraphSeries("<html>F<sub>friction ||</sub></html>", MotionSeriesDefaults.frictionForceColor, "m/s/s", frictionVariable) {
    motionSeriesModel.stepListeners += (() => {addPoint(motionSeriesModel.bead.frictionForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector, motionSeriesModel.getTime)})
  }
  val gravityForceSeries = new MSControlGraphSeries("<html>F<sub>gravity ||</sub></html>", MotionSeriesDefaults.gravityForceColor, "m/s/s", gravityVariable) {
    motionSeriesModel.stepListeners += (() => {addPoint(motionSeriesModel.bead.gravityForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector, motionSeriesModel.getTime)})
  }
  val wallForceSeries = new MSControlGraphSeries("<html>F<sub>wall ||</sub></html>", MotionSeriesDefaults.wallForceColor, "m/s/s", wallVariable) {
    motionSeriesModel.stepListeners += (() => {addPoint(motionSeriesModel.bead.wallForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector, motionSeriesModel.getTime)})
  }
  val totalForceSeries = new MSControlGraphSeries("<html>F<sub>total ||</sub></html>", MotionSeriesDefaults.totalForceColor, "m/s/s", frictionVariable) { //TODO: Fix variable in last arg
    motionSeriesModel.stepListeners += (() => {addPoint(motionSeriesModel.bead.totalForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector, motionSeriesModel.getTime)})
  }
  //applied, friction, gravity, wall, total
  temporalChart.addDataSeries(appliedForceSeries, appliedForceSeries.color)
  temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)
  temporalChart.addDataSeries(gravityForceSeries, gravityForceSeries.color)
  temporalChart.addDataSeries(wallForceSeries, wallForceSeries.color)
  temporalChart.addDataSeries(frictionForceSeries, frictionForceSeries.color)

  val additionalSerieses: List[MControlGraphSeries] = frictionForceSeries :: gravityForceSeries :: wallForceSeries :: totalForceSeries :: Nil
  val controlPanel = new PNode {
    addChild(new PSwing(new SeriesSelectionControl("forces.parallel-title-with-units".translate, 5) {
      addToGrid(appliedForceSeries, createEditableLabel)
      for (s <- additionalSerieses) addToGrid(s)
    }))
  }

  val chart = new ControlChart(controlPanel, createSliderNode(new PText("hello"), Color.green, temporalChart), temporalChart, new ChartZoomControlNode(temporalChart))

  def createSliderNode(thumb: PNode, highlightColor: Color, chart: TemporalChart) = {
    val chartSliderNode = new TemporalChartSliderNode(chart, Color.green) //TODO: add vertical label to the slider node
    chartSliderNode.addListener(new MotionSliderNode.Listener() {
      def sliderDragged(value: java.lang.Double) = {}

      def sliderThumbGrabbed = {}

      def valueChanged = {}
    })
    chartSliderNode
    //    new JFreeChartSliderNode(getJFreeChartNode, thumb, highlightColor) {
    //      val text = new ShadowHTMLNode(defaultSeries.getTitle)
    //      text.setFont(new PhetFont(14, true))
    //      text.setColor(defaultSeries.getColor)
    //      text.rotate(-java.lang.Math.PI / 2)
    //      val textParent = new PNode
    //      textParent.addChild(text)
    //      textParent.setPickable(false)
    //      textParent.setChildrenPickable(false)
    //      addChild(textParent)
    //
    //      override def updateLayout() = {
    //        if (textParent != null) setSliderDecorationInset(textParent.getFullBounds.getWidth + 5)
    //        super.updateLayout()
    //        if (textParent != null)
    //          textParent.setOffset(getTrackFullBounds.getX - textParent.getFullBounds.getWidth - getThumbFullBounds.getWidth / 2,
    //            getTrackFullBounds.getY + textParent.getFullBounds.getHeight + getTrackFullBounds.getHeight / 2 - textParent.getFullBounds.getHeight / 2)
    //      }
    //      updateLayout()
    //    }
  }
}

