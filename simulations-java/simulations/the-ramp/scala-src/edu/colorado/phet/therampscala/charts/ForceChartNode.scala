package edu.colorado.phet.therampscala.charts

import common.motion.graphs._
import common.motion.model.{UpdateStrategy, UpdateableObject, DefaultTemporalVariable}
import common.phetcommon.model.clock.ConstantDtClock
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.{PhetPCanvas}
import common.timeseries.model.{RecordableModel, TimeSeriesModel}
import java.awt.Color
import java.awt.geom.Point2D
import model.RampModel
import umd.cs.piccolo.PNode

class ForceChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel) extends PNode {
  val temp = new DefaultTemporalVariable() {
    override def setValue(value: Double) = model.bead.parallelAppliedForce = value
  }
  model.stepListeners += (() => temp.addValue(model.bead.parallelAppliedForce, model.getTime))
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
  val controlGraphSeries = new ControlGraphSeries("title", Color.blue, "abbr", "N", "character", temp)
  val parallelForceChart = new MotionControlGraph(canvas, controlGraphSeries, "label", "title", -2000, 2000, true, timeseriesModel, updateableObject) {
    setDomainUpperBound(20)
    getJFreeChartNode.setBuffered(false)
    getJFreeChartNode.setPiccoloSeries() //works better on an unbuffered chart
  }
  //  val y = new MotionControlGraph(canvas, controlGraphSeries, "label", "title", 0, 10, true, timeseriesModel, updateableObject) {
  //    setDomainUpperBound(20)
  //    getJFreeChartNode.setBuffered(false)
  //    getJFreeChartNode.setPiccoloSeries()
  //  }
  //  val set = new GraphSetNode(new GraphSetModel(new GraphSuite(Array(new MinimizableControlGraph("x", x), new MinimizableControlGraph("y", y)))))
  val set = new GraphSetNode(new GraphSetModel(new GraphSuite(Array(new MinimizableControlGraph("Parallel Forces(N)", parallelForceChart))))){
    protected override def getMaxAvailableHeight(availableHeight: Double) = availableHeight
  }
  set.setAlignedLayout()

  addChild(set)

  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    val viewBounds = transform.getViewBounds
    println("viewBounds=" + viewBounds)
    val h = viewBounds.getHeight - viewLoc.y
    println("using h=" + h)
    set.setBounds(viewBounds.getX, viewLoc.y, viewBounds.getWidth, h)
//    set.relayout()
  }
  updatePosition()
}