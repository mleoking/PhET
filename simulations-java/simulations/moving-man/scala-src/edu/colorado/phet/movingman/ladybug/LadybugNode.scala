package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.piccolophet.util.PImageFactory
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.movingman.ladybug.Ladybug
import edu.umd.cs.piccolo.event.PBasicInputEventHandler
import edu.umd.cs.piccolo.event.PInputEvent
import edu.umd.cs.piccolo.PNode
import java.awt.Color
import java.awt.geom.AffineTransform
import umd.cs.piccolo.nodes.{PPath, PImage}

class LadybugNode(ladybug: Ladybug) extends PNode {
  val pimage = new PImage(MovingManResources.loadBufferedImage("ladybug/ladybug.png"))
  val boundNode = new PPath
  boundNode.setPaint(Color.blue)
  boundNode.setPathToRectangle(-4, -4, 8, 8)

  ladybug.addListener(updateLadybug)
  updateLadybug(ladybug)
  
  addChild(pimage)
  addChild(boundNode)

  addInputEventListener(new CursorHandler)
  addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = ladybug.translate(event.getCanvasDelta.width, event.getCanvasDelta.height)
  })

  def updateLadybug(ladybug: Ladybug): Unit = {


    pimage.setTransform(new AffineTransform)

    val dx2 = new Vector2D(pimage.getImage.getWidth(null), pimage.getImage.getHeight(null))
    val dx = new Vector2D
    pimage.translate(ladybug.getPosition.x - dx.x / 2, ladybug.getPosition.y - dx.y / 2)
    pimage.rotateAboutPoint(ladybug.getAngle,
      pimage.getFullBounds.getCenter2D.getX - ladybug.getPosition.x - dx.x / 2,
      pimage.getFullBounds.getCenter2D.getY - ladybug.getPosition.y - dx.y / 2)

    setOffset(0, 0)
    translate(-dx2.x / 2, -dx2.y / 2)
    boundNode.setOffset(ladybug.getPosition.x + dx2.x / 2, ladybug.getPosition.y + dx2.x / 2)
    //    pimage.translate(ladybug.getPosition.x - pimage.getFullBounds.getWidth / 2, ladybug.getPosition.y - pimage.getFullBounds.getHeight / 2)


  }
}