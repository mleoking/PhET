package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.event.CursorHandler
import model.{Bead}
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import java.awt.BasicStroke

import common.piccolophet.nodes.PhetPPath
import umd.cs.piccolo.PNode
import java.awt.Color
import java.awt.geom.AffineTransform
import umd.cs.piccolo.nodes.PImage
import java.awt.image.BufferedImage
import scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.Predef._

class DraggableBeadNode(bead: Bead, transform: ModelViewTransform2D, imageName: String) extends BeadNode(bead, transform, imageName) {
  addInputEventListener(new CursorHandler)
  addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      val delta = event.getCanvasDelta
      val modelDelta = transform.viewToModelDifferential(delta.width, delta.height)
      val sign = modelDelta dot bead.getRampUnitVector
      bead.parallelAppliedForce_=(bead.parallelAppliedForce + sign / RampDefaults.PLAY_AREA_VECTOR_SCALE)
    }

    override def mouseReleased(event: PInputEvent) = {
      bead.parallelAppliedForce = 0.0
    }
  })
}

class BeadNode(bead: Bead, transform: ModelViewTransform2D, imageName: String) extends PNode {
  val shapeNode = new PhetPPath(new BasicStroke(1), Color.green)
  val imageNode = new PImage(RampResources.getImage(imageName))

  def setImage(im: BufferedImage) = {
    imageNode.setImage(im)
    update()
  }
  addChild(imageNode)
  //  addChild(shapeNode) //TODO remove after debug done

  def update() = {

    shapeNode.setPathTo(transform.createTransformedShape(new Circle(bead.position2D, bead.width / 2)))

    //TODO consolidate/refactor with BugNode, similar graphics transform code
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
