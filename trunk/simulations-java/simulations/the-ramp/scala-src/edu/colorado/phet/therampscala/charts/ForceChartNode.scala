package edu.colorado.phet.therampscala.charts

import common.motion.graphs._
import common.motion.model.{UpdateStrategy, UpdateableObject, DefaultTemporalVariable}
import common.phetcommon.model.clock.ConstantDtClock
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.{PhetPCanvas}
import common.timeseries.model.{RecordableModel, TimeSeriesModel}
import java.awt.geom.Point2D
import javax.swing.{JCheckBox}
import model.{ParallelComponent, RampModel}
import swing.MyCheckBox
import umd.cs.piccolo.PNode

class ForceChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel) extends PNode {
  val parallelAppliedForceVariable = new DefaultTemporalVariable() {
    override def setValue(value: Double) = model.bead.parallelAppliedForce = value
  }
  model.stepListeners += (() => parallelAppliedForceVariable.addValue(model.bead.parallelAppliedForce, model.getTime))

  val parallelFriction = new DefaultTemporalVariable()
  model.stepListeners += (() => parallelFriction.addValue(new ParallelComponent(model.bead.frictionForceVector,model.bead).getValue.magnitude, model.getTime))

  val gravityForce= new DefaultTemporalVariable()
  model.stepListeners += (() => gravityForce.addValue(new ParallelComponent(model.bead.gravityForceVector,model.bead).getValue.magnitude, model.getTime))

  val wallForce=new DefaultTemporalVariable()
  model.stepListeners += (()=>wallForce.addValue(new ParallelComponent(model.bead.wallForceVector,model.bead).getValue.magnitude, model.getTime))

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
  val appliedForceSeries = new ControlGraphSeries("Parallel Applied Force", RampDefaults.appliedForceColor, "Fa", "N", "", parallelAppliedForceVariable)
  val frictionSeries=new ControlGraphSeries("Parallel Friction Force", RampDefaults.frictionForceColor, "Ff", "N", "", parallelFriction)
  val gravitySeries=new ControlGraphSeries("Parallel Gravity Force", RampDefaults.gravityForceColor, "Fg", "N", "", gravityForce)
  val wallSeries=new ControlGraphSeries("Parallel Wall Force", RampDefaults.wallForceColor, "Fw", "N", "", wallForce)
  val parallelForceChart = new MotionControlGraph(canvas, appliedForceSeries, "label", "title", -2000, 2000, true, timeseriesModel, updateableObject) {
    setDomainUpperBound(20)
    getJFreeChartNode.setBuffered(false)
    getJFreeChartNode.setPiccoloSeries() //works better on an unbuffered chart
    addSeries(frictionSeries)
    addSeries(gravitySeries)
    addSeries(wallSeries)
  }

  def addListener( series:ControlGraphSeries,listener:()=>Unit)={
    series.addListener(new ControlGraphSeries.Adapter(){
      override def visibilityChanged = listener()
    })
  }
  class SeriesControlSelector(series:ControlGraphSeries) extends MyCheckBox(series.getTitle,series.setVisible(_),series.isVisible,addListener(series,_))
  class SeriesSelectionControl extends VerticalLayoutPanel {
    add(new SeriesControlSelector(appliedForceSeries).peer)
    add(new SeriesControlSelector(frictionSeries).peer)
    add(new SeriesControlSelector(gravitySeries).peer)
    add(new SeriesControlSelector(wallSeries).peer)
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