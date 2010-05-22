package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._

trait Rotatable extends Observable with RotationModel {
  def startPoint: Vector2D

  def endPoint_=(newPt: Vector2D)

  def length: Double

  def getUnitVector: Vector2D

  def endPoint: Vector2D

  def startPoint_=(newPt: Vector2D)

  def angle_=(a: Double) = {
    endPoint = new Vector2D(a) * length
  }
}

trait RotationModel {
  def getPivot: Vector2D

  def angle: Double = 0.0

  def angle_=(a: Double)
}

class RotationHandler(val transform: ModelViewTransform2D,
                      val node: PNode,
                      val rotatable: RotationModel,
                      min: Double,
                      max: Double)
        extends PBasicInputEventHandler {
  //TODO: it seems like these fields and computations should be moved to model objects
  private var totalDelta = 0.0
  private var origAngle = 0.0

  override def mouseDragged(event: PInputEvent) = {
    val modelPt = transform.viewToModel(event.getPositionRelativeTo(node.getParent))

    val deltaView = event.getDeltaRelativeTo(node.getParent)
    val deltaModel = transform.viewToModelDifferential(deltaView.width, deltaView.height)

    val oldPtModel = modelPt - deltaModel

    val oldAngle = (rotatable.getPivot - oldPtModel).getAngle
    val newAngle = (rotatable.getPivot - modelPt).getAngle

    //should be a small delta
    var deltaAngle = newAngle - oldAngle
    while (deltaAngle > PI) deltaAngle = deltaAngle - PI * 2
    while (deltaAngle < -PI) deltaAngle = deltaAngle + PI * 2

    totalDelta += deltaAngle
    val proposedAngle = origAngle + totalDelta

    val angle = getSnapAngle(if (proposedAngle > max) max else if (proposedAngle < min) min else proposedAngle)
    rotatable.angle = angle
  }

  def getSnapAngle(proposedAngle: Double) = proposedAngle

  override def mousePressed(event: PInputEvent) = {
    totalDelta = 0

    val modelPt = transform.viewToModel(event.getPositionRelativeTo(node.getParent))
    val oldAngle = (modelPt - rotatable.getPivot).getAngle
    origAngle = oldAngle
  }
}