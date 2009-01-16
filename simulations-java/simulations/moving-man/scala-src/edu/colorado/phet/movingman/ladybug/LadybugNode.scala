package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.util.PImageFactory
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.movingman.ladybug.Ladybug
import edu.umd.cs.piccolo.event.PBasicInputEventHandler
import edu.umd.cs.piccolo.event.PInputEvent
import edu.umd.cs.piccolo.PNode
import java.awt.Color
import java.awt.geom.{AffineTransform, Point2D}
import umd.cs.piccolo.nodes.{PPath, PImage}
import LadybugUtil._

class LadybugNode(ladybug: Ladybug, transform: ModelViewTransform2D) extends PNode {
  val arrowSetNode = new ArrowSetNode(ladybug, transform)
  val pimage = new PImage(MovingManResources.loadBufferedImage("ladybug/ladybug.png"))
  val boundNode = new PPath
  boundNode.setPaint(Color.blue)
  boundNode.setPathToRectangle(-4, -4, 8, 8)

  ladybug.addListener(updateLadybug)
  updateLadybug(ladybug)

  addChild(arrowSetNode)
  addChild(pimage)
  addChild(boundNode)

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = {
      updateLadybug(ladybug)
    }
  })

  addInputEventListener(new CursorHandler)
  addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      val diff = transform.viewToModelDifferential(event.getDeltaRelativeTo(getParent).width, event.getDeltaRelativeTo(getParent).height)
      ladybug.translate(diff)
    }
  })

  def updateLadybug(ladybug: Ladybug): Unit = {

    val modelPosition = ladybug.getPosition
    val viewPosition = transform.modelToView(modelPosition)
    pimage.setTransform(new AffineTransform)
    val dx = new Vector2D(pimage.getImage.getWidth(null), pimage.getImage.getHeight(null))

    pimage.translate(viewPosition.x - dx.x / 2, viewPosition.y - dx.y / 2)
    pimage.rotateAboutPoint(ladybug.getAngle,
      pimage.getFullBounds.getCenter2D.getX - (viewPosition.x - dx.x / 2),
      pimage.getFullBounds.getCenter2D.getY - (viewPosition.y - dx.y / 2))

    boundNode.setOffset(viewPosition)
  }
}