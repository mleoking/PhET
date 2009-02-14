package edu.colorado.phet.movingman.ladybug.canvas

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import controlpanel.VectorVisibilityModel
import java.awt.image.BufferedImage
import java.awt.{Point, Color, Cursor}
import model.{Ladybug, Vector2D, LadybugModel}
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.util.PImageFactory
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.umd.cs.piccolo.PNode
import java.awt.geom.{AffineTransform, Point2D}

import umd.cs.piccolo.event.{PInputEventListener, PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PImage}
import LadybugUtil._
import util.ToggleListener

class LadybugNode(model: LadybugModel, ladybug: Ladybug, transform: ModelViewTransform2D, vectorVisibilityModel: VectorVisibilityModel) extends PNode {
  var interactive = true //todo: do we need both draggable and interactive?
  var draggable = true
  model.addListener(() => updateInteractive())
  def updateInteractive() = {interactive = model.readyForInteraction}

  val arrowSetNode = new ArrowSetNode(ladybug, transform, vectorVisibilityModel)
  //  val pimage = new PImage(BufferedImageUtils.multiScale(MovingManResources.loadBufferedImage("ladybug/ladybug.png"), 0.6))
  val bufferedImage=MovingManResources.loadBufferedImage("ladybug/ladybug.png")
  val pimage = new PImage(bufferedImage)

  ladybug.addListener(updateLadybug)
  updateLadybug()

  addChild(arrowSetNode)
  addChild(pimage)

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = {
      updateLadybug()
    }
  })

  def getLadybugCenter() = pimage.getFullBounds.getCenter2D

  addInputEventListener(new ToggleListener(new CursorHandler, () => draggable && interactive))

  private def recordPoint(event: PInputEvent) = {
    model.startRecording()
    model.setPenDown(true)
    model.setSamplePoint(transform.viewToModel(event.getPositionRelativeTo(getParent)))
  }

  val inputHandler = new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      recordPoint(event)

      if (LadybugDefaults.HIDE_MOUSE_DURING_DRAG && draggable) {
        event.getComponent.pushCursor(java.awt.Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "invisibleCursor"))
      }
    }

    override def mousePressed(event: PInputEvent) = {
      recordPoint(event)
    }

    override def mouseReleased(event: PInputEvent) = {
      model.setPenDown(false)
    }

    override def mouseExited(event: PInputEvent) = {
      model.setPenDown(false)
    }
  }
  addInputEventListener(new ToggleListener(inputHandler, () => draggable && interactive))
  updateInteractive()

  def updateLadybug(): Unit = {

    val modelPosition = ladybug.getPosition
    val viewPosition = transform.modelToView(modelPosition)
    pimage.setTransform(new AffineTransform)
    val dx = new Vector2D(pimage.getImage.getWidth(null), pimage.getImage.getHeight(null))

    val scale=transform.modelToViewDifferentialXDouble(ladybug.getRadius)/bufferedImage.getWidth

    pimage.translate(viewPosition.x - dx.x / 2*scale, viewPosition.y - dx.y / 2*scale)
    pimage.scale(scale)
    pimage.rotateAboutPoint(ladybug.getAngleInvertY,
      pimage.getFullBounds.getCenter2D.getX - (viewPosition.x - dx.x / 2),
      pimage.getFullBounds.getCenter2D.getY - (viewPosition.y - dx.y / 2))

  }

  def setDraggable(d: Boolean) = this.draggable = d;
}