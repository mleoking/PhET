package edu.colorado.phet.movingman.ladybug.canvas

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import _root_.edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import _root_.edu.colorado.phet.movingman.ladybug.aphidmaze.Aphid
import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Color}
import model.{Bug, Vector2D}
import umd.cs.piccolo.nodes.PImage
import _root_.edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import _root_.edu.colorado.phet.common.piccolophet.event.CursorHandler
import util.ToggleListener
import java.awt.geom.AffineTransform
import umd.cs.piccolo.event.PInputEvent
import umd.cs.piccolo.PNode
import LadybugUtil._


class BugNode(bug: Bug, transform: ModelViewTransform2D,bufferedImage:BufferedImage) extends PNode {
  val pimage = new PImage(BufferedImageUtils.multiScale(bufferedImage, LadybugDefaults.LADYBUG_SCALE))
  val boundsPPath=new PhetPPath(new BasicStroke(0.1f),Color.blue)


  bug.addListener(updateBug)
  updateBug()

  addChild(pimage)
  addChild(boundsPPath)

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = {
      updateBug()
    }
  })

  def updateBug(): Unit = {

    val modelPosition = bug.getPosition
    val viewPosition = transform.modelToView(modelPosition)
    pimage.setTransform(new AffineTransform)
    val dx = new Vector2D(pimage.getImage.getWidth(null), pimage.getImage.getHeight(null))

    //todo: why is scale factor 4 here?
    val scale=transform.modelToViewDifferentialXDouble(bug.getRadius*4)/bufferedImage.getWidth

    pimage.translate(viewPosition.x - dx.x / 2*scale, viewPosition.y - dx.y / 2*scale)
    pimage.scale(scale)
    pimage.rotateAboutPoint(bug.getAngleInvertY,
      pimage.getFullBounds.getCenter2D.getX - (viewPosition.x - dx.x / 2),
      pimage.getFullBounds.getCenter2D.getY - (viewPosition.y - dx.y / 2))

    boundsPPath.setPathTo(transform.getAffineTransform.createTransformedShape(bug.getBounds))
  }

}