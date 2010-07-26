package edu.colorado.phet.motionseries.graphics

import _root_.edu.colorado.phet.motionseries.MotionSeriesResources
import _root_.edu.umd.cs.piccolo.nodes.PImage
import _root_.java.awt.geom.{AffineTransform, Point2D}
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
                            val coordinateFrameModel: CoordinateFrameModel)
        extends AxisModel(offset, length, tail) {
  //adapters for going between local and global models
  coordinateFrameModel.addListenerByName(angle = coordinateFrameModel.angle + offset)
}

class CoordinateFrameNode(val model: MotionSeriesModel,
                          adjustableCoordinateModel: AdjustableCoordinateModel,
                          val transform: ModelViewTransform2D)
        extends PNode {
  import java.lang.Math.PI
  //see CoordinateFrameModel.proposedAngle_= for additional constraint on drag angle
  val xAxisModel = new SynchronizedAxisModel(0, 0.0, PI / 2, 7, false, model.coordinateFrameModel)
  val xAxis = new AxisNodeWithModel(transform, "coordinates.x".translate, xAxisModel, adjustableCoordinateModel)
  addChild(xAxis)

  val yAxisModel = new SynchronizedAxisModel(PI / 2, PI / 2, PI, 7, false, model.coordinateFrameModel)
  val yAxis = new AxisNodeWithModel(transform, "coordinates.y".translate, yAxisModel, adjustableCoordinateModel) with HandleNode
  addChild(yAxis)

  defineInvokeAndPass(adjustableCoordinateModel.addListenerByName) {
    val v = adjustableCoordinateModel.adjustable
    setVisible(v)
    setPickable(v)
    setChildrenPickable(v)
  }
}

trait HandleNode extends AxisNodeWithModel {
  val bufferedImage = MotionSeriesResources.getImage("handle_1.png".literal)
  val handleNode = new PImage(bufferedImage)
  addChild(handleNode)
  updateTipAndTailLocations()
  override def setTipAndTailLocations(tip: Point2D, tail: Point2D) = {
    super.setTipAndTailLocations(tip, tail)
    if (handleNode != null) {
      handleNode.setTransform(new AffineTransform)
      handleNode.setOffset(tip)
      handleNode.scale(1.6)
      val angle = Math.atan2(tip.getY - tail.getY, tip.getX - tail.getX)
      handleNode.rotate(angle)
      handleNode.translate(-50, -bufferedImage.getHeight)
    }
  }
  attachListener(handleNode)
}