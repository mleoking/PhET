package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.event.CursorHandler
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}

import model.Bead
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
      bead.appliedForce = (bead.appliedForce + modelDelta)
    }

    override def mouseReleased(event: PInputEvent) = {
      bead.appliedForce = new Vector2D
    }
  })
}

class BeadNode(bead: Bead, transform: ModelViewTransform2D, imageName: String) extends PNode {
  val shapeNode = new PhetPPath(Color.green)
  //  addChild(shapeNode)//TODO remove after debug done

  val cabinetImage = RampResources.getImage(imageName)
  val imageNode = new PImage(cabinetImage)

  def setImage(im: BufferedImage) = imageNode.setImage(im)
  addChild(imageNode)

  defineInvokeAndPass(bead.addListenerByName) {
    shapeNode.setPathTo(transform.createTransformedShape(new Circle(bead.position2D, 0.3)))

    //TODO consolidate/refactor with BugNode, similar graphics transform code
    imageNode.setTransform(new AffineTransform)

    val modelPosition = bead.position2D
    val viewPosition = transform.modelToView(modelPosition)
    val delta = new Vector2D(imageNode.getImage.getWidth(null), imageNode.getImage.getHeight(null))

    //    todo: why is scale factor 4 here?
    //    val scale = transform.modelToViewDifferentialXDouble(1 * 4) / cabinetImage.getWidth
    val modelHeight = bead.height
    val scale = -transform.modelToViewDifferentialYDouble(modelHeight) / cabinetImage.getHeight

    imageNode.translate(viewPosition.x - delta.x / 2 * scale, viewPosition.y - delta.y * scale)
    imageNode.scale(scale)
    imageNode.rotateAboutPoint(bead.getAngleInvertY,
      imageNode.getFullBounds.getCenter2D.getX - (viewPosition.x - delta.x / 2),
      imageNode.getFullBounds.getMaxY - (viewPosition.y - delta.y))

  }
}
