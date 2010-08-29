package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.umd.cs.piccolo.nodes.PImage
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.colorado.phet.scalacommon.Predef._

trait CloseButton extends MotionSeriesObjectNode {
  val closeButton = new PImage(PhetCommonResources.getImage("buttons/closeButton.png".literal))
  closeButton.addInputEventListener(new CursorHandler)

  val openButton = new PImage(PhetCommonResources.getImage("buttons/maximizeButton.png".literal))
  openButton.addInputEventListener(new CursorHandler)

  addChild(closeButton)
  addChild(openButton)
  update()

  def model: MotionSeriesModel

  override def update() = {
    super.update()
    if (closeButton != null) {
      closeButton.setOffset(imageNode.getFullBounds.getX, imageNode.getFullBounds.getY)
      openButton.setOffset(imageNode.getFullBounds.getX, imageNode.getFullBounds.getY)
    }
  }
  closeButton.addInputEventListener(new PBasicInputEventHandler {
    override def mousePressed(event: PInputEvent) = model.walls = false
  })
  openButton.addInputEventListener(new PBasicInputEventHandler {
    override def mousePressed(event: PInputEvent) = model.walls = true
  })
  defineInvokeAndPass(model.addListenerByName) {
    imageNode.setVisible(model.walls.booleanValue)
    imageNode.setPickable(model.walls.booleanValue)
    imageNode.setChildrenPickable(model.walls.booleanValue)

    closeButton.setVisible(model.walls.booleanValue)
    closeButton.setPickable(model.walls.booleanValue)
    closeButton.setChildrenPickable(model.walls.booleanValue)

    openButton.setVisible(!model.walls.booleanValue)
    openButton.setPickable(!model.walls.booleanValue)
    openButton.setChildrenPickable(!model.walls.booleanValue)
  }
}