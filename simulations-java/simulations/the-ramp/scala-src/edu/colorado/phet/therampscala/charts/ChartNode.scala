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

class ChartNode(transform: ModelViewTransform2D, canvas: PhetPCanvas, model: RampModel) extends PNode {

  val temp = new DefaultTemporalVariable(){
    override def setValue(value: Double) = model.bead.parallelAppliedForce = value
  }
  model.stepListeners += (() => temp.addValue(model.bead.parallelAppliedForce,model.getTime))
//  model.addListener()
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
  val controlGraphSeries = new ControlGraphSeries("title",Color.blue,"abbr","N","character",temp)
  val x = new MotionControlGraph(canvas, controlGraphSeries, "label", "title", -2000, 2000, true, timeseriesModel, updateableObject){
    setDomainUpperBound(20)
  }
  val y = new MotionControlGraph(canvas, controlGraphSeries, "label", "title", 0, 10, true, timeseriesModel, updateableObject){
    setDomainUpperBound(20)
  }
  val set = new GraphSetNode(new GraphSetModel(new GraphSuite(Array(new MinimizableControlGraph("x", x), new MinimizableControlGraph("y", y)))))
  set.setAlignedLayout()

  addChild(set)

  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    val viewBounds = transform.getViewBounds
    set.setBounds(viewBounds.getX, viewLoc.y, viewBounds.getWidth, viewBounds.getHeight - viewLoc.y)
  }
  updatePosition()
}