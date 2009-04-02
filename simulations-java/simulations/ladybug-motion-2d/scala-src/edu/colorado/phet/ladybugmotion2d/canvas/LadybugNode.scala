package edu.colorado.phet.ladybugmotion2d.canvas

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import controlpanel.VectorVisibilityModel
import java.awt.image.BufferedImage
import java.awt.{Point, Color, Cursor}
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.util.PImageFactory
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.umd.cs.piccolo.PNode
import java.awt.geom.{AffineTransform, Point2D}
import model.{Ladybug, LadybugModel}


import umd.cs.piccolo.event.{PInputEventListener, PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PImage}
import edu.colorado.phet.scalacommon.Predef._
import util.ToggleListener

class LadybugNode(model: LadybugModel,
                  ladybug: Ladybug,
                  transform: ModelViewTransform2D,
                  vectorVisibilityModel: VectorVisibilityModel)
        extends BugNode(ladybug, transform, Ladybug2DResources.getImage("ladybug.png")) {
  var interactive = true //todo: do we need both draggable and interactive?
  var draggable = true
  model.addListenerByName(updateInteractive())
  def updateInteractive() = {interactive = model.readyForInteraction}

  val arrowSetNode = new ArrowSetNode(ladybug, transform, vectorVisibilityModel)

  addChild(0, arrowSetNode) //todo: insert before pimage in super class

  def getLadybugCenter() = pimage.getFullBounds.getCenter2D

  addInputEventListener(new ToggleListener(new CursorHandler, () => draggable && interactive))

  private def recordPoint(event: PInputEvent) = {
    model.startRecording()
    model.setPenDown(true)
    model.setSamplePoint(transform.viewToModel(event.getPositionRelativeTo(getParent)))
    model.setUpdateModePosition()
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

  def setDraggable(d: Boolean) = this.draggable = d;
}