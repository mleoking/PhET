package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.colorado.phet.scalacommon.Predef._

//TODO: why is Rotatble needed, can't we just use RotationModel?
trait Rotatable extends Observable with RotationModel {
  def startPoint: Vector2D

  def endPoint_=(newPt: Vector2D)

  def length: Double

  def unitVector: Vector2D

  def endPoint: Vector2D

  def startPoint_=(newPt: Vector2D)

  def angle_=(a: Double) = {
    endPoint = new Vector2D(a) * length
  }
}

trait RotationModel {
  def pivot: Vector2D

  def angle: Double = 0.0

  def angle_=(a: Double)
}

/**
 * This PInputEventHandler handles drag events on rotatable objects such as the liftable ramp segment
 * and the rotatable coorindate frames.
 * @author Sam Reid
 */
class RotationHandler(val transform: ModelViewTransform2D,
                      val node: PNode,
                      val rotatable: RotationModel,
                      min: Double,
                      max: Double)
        extends PBasicInputEventHandler {
  private var relativeAngle = 0.0

  override def mousePressed(event: PInputEvent) = {
    val pointerAngle = (toModelPoint(event) - pivot).angle
    val modelAngle = rotatable.angle
    relativeAngle = modelAngle - pointerAngle
  }

  override def mouseDragged(event: PInputEvent) = {
    val pointerAngle = (toModelPoint(event) - pivot).angle
    val proposedAngle = pointerAngle + relativeAngle
    val angle = getSnapAngle(if (proposedAngle > max) max else if (proposedAngle < min) min else proposedAngle)
    rotatable.angle = angle
  }

  def getSnapAngle(proposedAngle: Double) = proposedAngle

  def pivot = rotatable.pivot

  def toModelPoint(event: PInputEvent) = transform.viewToModel(event.getPositionRelativeTo(node.getParent))
}