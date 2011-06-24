package edu.colorado.phet.ladybugmotion2d.canvas

import java.awt.image.BufferedImage
import java.awt.Point
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.ladybugmotion2d.model.{LadybugModel, Ladybug}
import edu.umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import edu.colorado.phet.ladybugmotion2d.controlpanel.VectorVisibilityModel
import edu.colorado.phet.ladybugmotion2d.{LadybugDefaults, LadybugMotion2DResources}
import edu.colorado.phet.scalacommon.view.ToggleListener

class LadybugNode(model: LadybugModel,
                  ladybug: Ladybug,
                  transform: ModelViewTransform2D,
                  vectorVisibilityModel: VectorVisibilityModel)
        extends BugNode(ladybug, transform, LadybugMotion2DResources.getImage("ladybug.png")) {
  var interactive = true //todo: do we need both draggable and interactive?
  var draggable = true
  model.addListenerByName(updateInteractive())

  def updateInteractive() = {
    interactive = model.readyForInteraction
  }

  val arrowSetNode = new ArrowSetNode(ladybug, transform, vectorVisibilityModel)

  addChild(0, arrowSetNode)

  //todo: insert before pimage in super class

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

      if ( LadybugDefaults.HIDE_MOUSE_DURING_DRAG && draggable ) {
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