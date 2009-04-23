package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.PhetPPath
import java.awt.geom.Line2D
import java.awt.{BasicStroke, Color}
import model.{CoordinateFrameModel, RampModel}
import scalacommon.math.Vector2D
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import java.lang.Math._
import scalacommon.Predef._

class SynchronizedAxisModel(_ang: Double, length: Double, tail: Boolean, coordinateFrameModel: CoordinateFrameModel) extends AxisModel(_ang, length, tail) {
  val offset = _ang

  //adapters for going between local and global models
  coordinateFrameModel.addListenerByName(angle = coordinateFrameModel.angle + offset)
  addListenerByName(coordinateFrameModel.angle = angle - offset)
}

class CoordinateFrameNode(val model: RampModel, coordinateSystemModel: CoordinateSystemModel, val transform: ModelViewTransform2D) extends PNode {
  val yAxisModel = new SynchronizedAxisModel(PI / 2, 7, false, model.coordinateFrameModel)
  val yAxis = new AxisNodeWithModel(transform, "y", yAxisModel,coordinateSystemModel.adjustable)
  addChild(yAxis)

  val xAxisModel = new SynchronizedAxisModel(0, 7, false, model.coordinateFrameModel)
  val xAxis = new AxisNodeWithModel(transform, "x", xAxisModel,coordinateSystemModel.adjustable)
  addChild(xAxis)

  defineInvokeAndPass(coordinateSystemModel.addListenerByName) {
    setVisible(coordinateSystemModel.adjustable)
  }
}