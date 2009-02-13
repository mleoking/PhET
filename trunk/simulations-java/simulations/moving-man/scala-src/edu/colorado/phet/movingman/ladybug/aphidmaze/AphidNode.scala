package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import umd.cs.piccolo.nodes.PImage
import _root_.edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import _root_.edu.colorado.phet.common.piccolophet.event.CursorHandler
import util.ToggleListener
import java.awt.geom.AffineTransform
import umd.cs.piccolo.event.PInputEvent
import model.Vector2D
import umd.cs.piccolo.PNode
import LadybugUtil._

class AphidNode(aphid: Aphid, transform:ModelViewTransform2D) extends PNode {
  val pimage = new PImage(BufferedImageUtils.multiScale(MovingManResources.loadBufferedImage("ladybug/valessiobrito_Bug_Buddy_Vec.png"), LadybugDefaults.LADYBUG_SCALE))

  aphid.addListener(updateAphid)
  updateAphid()

  addChild(pimage)

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = {
      updateAphid()
    }
  })

  def updateAphid(): Unit = {

    val modelPosition = aphid.getPosition
    val viewPosition = transform.modelToView(modelPosition)
    pimage.setTransform(new AffineTransform)
    val dx = new Vector2D(pimage.getImage.getWidth(null), pimage.getImage.getHeight(null))

    pimage.translate(viewPosition.x - dx.x / 2, viewPosition.y - dx.y / 2)
    pimage.rotateAboutPoint(aphid.getAngleInvertY,
      pimage.getFullBounds.getCenter2D.getX - (viewPosition.x - dx.x / 2),
      pimage.getFullBounds.getCenter2D.getY - (viewPosition.y - dx.y / 2))

  }

}