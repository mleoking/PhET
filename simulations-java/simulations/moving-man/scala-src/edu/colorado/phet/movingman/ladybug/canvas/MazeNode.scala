package edu.colorado.phet.movingman.ladybug.canvas

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import umd.cs.piccolo.PNode
import _root_.edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import _root_.edu.colorado.phet.common.piccolophet.event.CursorHandler
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

class MazeNode(model: AphidMazeModel, transform: ModelViewTransform2D) extends PNode {
  val pimage = new PImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB))

  model.ladybug.addListener(updateGraphics)
  updateGraphics()

  addChild(pimage)

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = {
      updateGraphics()
    }
  })

  def getLadybugCenter() = pimage.getFullBounds.getCenter2D

  def updateGraphics(): Unit = {

    removeAllChildren()

    val ladybug = model.ladybug
    val modelPosition = ladybug.getPosition
    val viewPosition = transform.modelToView(modelPosition)
    pimage.setTransform(new AffineTransform)
    val dx = new Vector2D(pimage.getImage.getWidth(null), pimage.getImage.getHeight(null))

    pimage.translate(viewPosition.x - dx.x / 2, viewPosition.y - dx.y / 2)
    pimage.rotateAboutPoint(ladybug.getAngleInvertY,
      pimage.getFullBounds.getCenter2D.getX - (viewPosition.x - dx.x / 2),
      pimage.getFullBounds.getCenter2D.getY - (viewPosition.y - dx.y / 2))

  }

}