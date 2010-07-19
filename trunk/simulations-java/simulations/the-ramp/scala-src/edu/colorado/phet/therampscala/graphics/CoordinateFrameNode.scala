package edu.colorado.phet.therampscala.graphics

import RampResources._
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import model.{CoordinateSystemModel, CoordinateFrameModel, RampModel}
import umd.cs.piccolo.PNode
import java.lang.Math._
import scalacommon.Predef._

class SynchronizedAxisModel(_ang: Double, length: Double, tail: Boolean, coordinateFrameModel: CoordinateFrameModel) extends AxisModel(_ang, length, tail) {
  val offset = _ang

  //adapters for going between local and global models
  coordinateFrameModel.addListenerByName(angle = coordinateFrameModel.angle + offset)
  addListenerByName(coordinateFrameModel.angle = angle - offset)

  override def dropped() = {
    coordinateFrameModel.dropped()
  }
}

class CoordinateFrameNode(val model: RampModel, coordinateSystemModel: CoordinateSystemModel, val transform: ModelViewTransform2D) extends PNode {
  val yAxisModel = new SynchronizedAxisModel(PI / 2, 7, false, model.coordinateFrameModel)
  val yAxis = new AxisNodeWithModel(transform, "coordinates.y".translate, yAxisModel, coordinateSystemModel.adjustable, PI / 2, PI)
  addChild(yAxis)

  val xAxisModel = new SynchronizedAxisModel(0, 7, false, model.coordinateFrameModel)
  val xAxis = new AxisNodeWithModel(transform, "coordinates.x".translate, xAxisModel, coordinateSystemModel.adjustable, 0, PI / 2)
  addChild(xAxis)

  defineInvokeAndPass(coordinateSystemModel.addListenerByName) {
    setVisible(coordinateSystemModel.adjustable)
  }
}