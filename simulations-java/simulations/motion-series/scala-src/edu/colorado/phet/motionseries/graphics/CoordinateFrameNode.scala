package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.model.{AdjustableCoordinateModel, CoordinateFrameModel, MotionSeriesModel}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.scalacommon.util.Observable

class RampAngleModel(private var _value: Double) extends Observable {
  def value = _value

  def setValue(value: Double) = {
    _value = value
    notifyListeners()
  }
}

class SynchronizedAxisModel(val offset: Double,
                            val minAngle: Double,
                            val maxAngle: Double,
                            length: Double,
                            tail: Boolean,
                            coordinateFrameModel: CoordinateFrameModel)
        extends AxisModel(offset, length, tail) {
  //adapters for going between local and global models
  coordinateFrameModel.addListenerByName(angle = coordinateFrameModel.angle + offset)
  addListenerByName(coordinateFrameModel.angle = angle - offset)

  override def dropped() = {
    coordinateFrameModel.dropped()
  }

  def getSnapAngle(angle: Double) = coordinateFrameModel.getSnapAngle(angle)

}

class CoordinateFrameNode(val model: MotionSeriesModel,
                          coordinateSystemModel: AdjustableCoordinateModel,
                          val transform: ModelViewTransform2D)
        extends PNode {
  import java.lang.Math.PI
  val xAxisModel = new SynchronizedAxisModel(0, 0.0, PI / 2, 7, false, model.coordinateFrameModel)
  val xAxis = new AxisNodeWithModel(transform, "coordinates.x".translate, xAxisModel, coordinateSystemModel)
  addChild(xAxis)

  val yAxisModel = new SynchronizedAxisModel(PI / 2, PI / 2, PI, 7, false, model.coordinateFrameModel)
  val yAxis = new AxisNodeWithModel(transform, "coordinates.y".translate, yAxisModel, coordinateSystemModel)
  addChild(yAxis)

  defineInvokeAndPass(coordinateSystemModel.addListenerByName) {
    val v = coordinateSystemModel.adjustable
    setVisible(v)
    setPickable(v)
    setChildrenPickable(v)
  }
}