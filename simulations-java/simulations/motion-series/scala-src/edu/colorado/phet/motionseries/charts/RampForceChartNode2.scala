package edu.colorado.phet.motionseries.charts

import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.motionseries.graphics.RampCanvas
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.motion.charts._
import java.awt.geom.Rectangle2D.Double
import java.awt.event.{ComponentEvent, ComponentAdapter}
import java.awt.Color
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.motionseries.MotionSeriesDefaults
/**
 * @author Sam Reid
 */
class RampForceChartNode2(rampCanvas: RampCanvas, motionSeriesModel: MotionSeriesModel) extends MultiControlChart(Array(new RampForceMinimizableControlChart(motionSeriesModel))) {
  rampCanvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = {
      setBounds(0, rampCanvas.getHeight / 2, rampCanvas.getWidth, rampCanvas.getHeight / 2)
    }
  })
}

class RampForceMinimizableControlChart(motionSeriesModel: MotionSeriesModel) extends MinimizableControlChart("title", new RampForceControlChart(motionSeriesModel).chart)

class RampForceControlChart(motionSeriesModel: MotionSeriesModel) {
  val temporalChart = new TemporalChart(new Double(0, -2000, 20, 4000), new ChartCursor())

  val appliedForceSeries = new TemporalDataSeries() {
    motionSeriesModel.stepListeners += (() => {addPoint(motionSeriesModel.bead.parallelAppliedForce, motionSeriesModel.getTime)})
  }
  val gravityForceSeries = new TemporalDataSeries() {
    motionSeriesModel.stepListeners += (() => {addPoint(motionSeriesModel.bead.gravityForceVector.getValue dot motionSeriesModel.bead.getRampUnitVector, motionSeriesModel.getTime)})
  }
  temporalChart.addDataSeries(appliedForceSeries, MotionSeriesDefaults.appliedForceColor)
  temporalChart.addDataSeries(gravityForceSeries, MotionSeriesDefaults.gravityForceColor)

  val seriesList: List[MControlGraphSeries] = new MControlGraphSeries {
    override def color = Color.blue

    override def title = "Applied Force"
  } :: new MControlGraphSeries {
    override def color = Color.blue

    override def title = "Applied Force"
  } :: new MControlGraphSeries {
    override def color = Color.blue

    override def title = "Applied Force"
  } :: Nil
  val appliedForceSeriesM = new MControlGraphSeries {
    override def color = Color.blue

    override def title = "Applied Force"
  }
  val controlPanel = new PNode {
    addChild(new PSwing(new SeriesSelectionControl("forces.parallel-title-with-units".translate, 5) {
      addToGrid(appliedForceSeriesM, createEditableLabel)
      for (s <- seriesList) addToGrid(s)
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

