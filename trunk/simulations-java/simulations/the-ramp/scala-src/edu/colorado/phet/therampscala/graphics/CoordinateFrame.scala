package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.PhetPPath
import java.awt.geom.Line2D
import java.awt.{BasicStroke, Color}
import model.{CoordinateFrameModel, RampModel}
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import java.lang.Math._
import scalacommon.Predef._

class SynchronizedAxisModel(_ang: Double, length: Double, coordinateFrameModel: CoordinateFrameModel) extends AxisModel(_ang, length) {
  val offset = _ang

  //adapters for going between local and global models
  coordinateFrameModel.addListenerByName(angle = coordinateFrameModel.angle + offset)
  addListenerByName(coordinateFrameModel.angle = angle - offset)
}

class CoordinateFrame(val model: RampModel, val transform: ModelViewTransform2D) extends PNode {
  addChild(new PText("Coordinate Frame"))

  val yAxisModel = new SynchronizedAxisModel(PI / 2, 7, model.coordinateFrameModel)
  val yAxis = new AxisNodeWithModel(transform, "y", yAxisModel)
  addChild(yAxis)

  val xAxisModel = new SynchronizedAxisModel(0, 7, model.coordinateFrameModel)
  val xAxis = new AxisNodeWithModel(transform, "x", xAxisModel)
  addChild(xAxis)
}