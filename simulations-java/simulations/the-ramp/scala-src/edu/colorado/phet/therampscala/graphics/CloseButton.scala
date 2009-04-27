package edu.colorado.phet.therampscala.graphics


import common.phetcommon.resources.PhetCommonResources
import model.RampModel
import umd.cs.piccolo.nodes.PImage
import common.piccolophet.event.CursorHandler
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import scalacommon.Predef._

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Apr 27, 2009
 * Time: 9:56:11 AM
 * To change this template use File | Settings | File Templates.
 */

trait CloseButton extends BeadNode {
  val closeButton = new PImage(PhetCommonResources.getImage("buttons/closeButton.png"))
  closeButton.addInputEventListener(new CursorHandler)

  val openButton = new PImage(PhetCommonResources.getImage("buttons/maximizeButton.png"))
  openButton.addInputEventListener(new CursorHandler)

  addChild(closeButton)
  addChild(openButton)
  update()

  def model: RampModel

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
    imageNode.setVisible(model.walls)
    imageNode.setPickable(model.walls)
    imageNode.setChildrenPickable(model.walls)

    closeButton.setVisible(model.walls)
    closeButton.setPickable(model.walls)
    closeButton.setChildrenPickable(model.walls)

    openButton.setVisible(!model.walls)
    openButton.setPickable(!model.walls)
    openButton.setChildrenPickable(!model.walls)
  }
}