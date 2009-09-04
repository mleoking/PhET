package edu.colorado.phet.motionseries.graphics

import common.phetcommon.view.util.BufferedImageUtils
import phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import phet.common.piccolophet.event.CursorHandler
import model.{Bead}
import motionseries.MotionSeriesResources
import motionseries.MotionSeriesDefaults
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.PNode
import java.awt.geom.AffineTransform
import umd.cs.piccolo.nodes.PImage
import java.awt.image.BufferedImage
import scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.Predef._

class ForceDragBeadNode(bead: Bead,
                        transform: ModelViewTransform2D,
                        imageName: String,
                        dragListener: () => Unit) extends BeadNode(bead, transform, imageName) {
  addInputEventListener(new CursorHandler)
  addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      val delta = event.getCanvasDelta
      val modelDelta = transform.viewToModelDifferential(delta.width, delta.height)
      val sign = modelDelta dot bead.getRampUnitVector
      bead.parallelAppliedForce = bead.parallelAppliedForce + sign / MotionSeriesDefaults.PLAY_AREA_VECTOR_SCALE
      dragListener()
    }

    override def mouseReleased(event: PInputEvent) = {
      bead.parallelAppliedForce = 0.0
    }
  })
}

class PositionDragBeadNode(bead: Bead,
                           transform: ModelViewTransform2D,
                           imageName: String,
                           leftImageName: String,
                           dragListener: () => Unit) extends BeadNode(bead, transform, imageName) {
  addInputEventListener(new CursorHandler)
  addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      bead.setPositionMode()
      val delta = event.getCanvasDelta
      val modelDelta = transform.viewToModelDifferential(delta.width, delta.height)
      bead.setPosition(bead.position + modelDelta.x)
      dragListener()
    }

    override def mouseReleased(event: PInputEvent) = {
      bead.parallelAppliedForce = 0.0
    }
  })
  bead.addListener(() => updateImage())
  updateImage()

  def updateImage() = {
    if (bead.averageVelocity < 0) setImage(MotionSeriesResources.getImage(leftImageName))
    else if (bead.averageVelocity > 0) setImage(BufferedImageUtils.flipX(MotionSeriesResources.getImage(leftImageName)))
    else setImage(MotionSeriesResources.getImage(imageName))
  }
}

class BeadNode(bead: Bead, transform: ModelViewTransform2D, imageName: String) extends PNode {
  val imageNode = new PImage(MotionSeriesResources.getImage(imageName))

  def setImage(im: BufferedImage) = {
    imageNode.setImage(im)
    update()
  }
  addChild(imageNode)

  def update() = {
    //TODO: consolidate/refactor with BugNode, similar graphics transform code
    imageNode.setTransform(new AffineTransform)

    val modelPosition = bead.position2D
    val viewPosition = transform.modelToView(modelPosition)
    val delta = new Vector2D(imageNode.getImage.getWidth(null), imageNode.getImage.getHeight(null))

    val scale = -transform.modelToViewDifferentialYDouble(bead.height) / imageNode.getImage.getHeight(null)

    imageNode.translate(viewPosition.x - delta.x / 2 * scale, viewPosition.y - delta.y * scale)
    imageNode.scale(scale)

    val angle = bead.getAngle
    val vec = new Vector2D(angle)
    val flipY = new Vector2D(vec.x, -vec.y)

    imageNode.rotateAboutPoint(flipY.getAngle,
      imageNode.getFullBounds.getCenter2D.getX - (viewPosition.x - delta.x / 2),
      imageNode.getFullBounds.getMaxY - (viewPosition.y - delta.y))
  }
  bead.addListener(update)
  update()
}
