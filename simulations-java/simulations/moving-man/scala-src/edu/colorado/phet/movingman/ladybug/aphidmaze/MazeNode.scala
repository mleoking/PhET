package edu.colorado.phet.movingman.ladybug.canvas.aphidmaze

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import _root_.edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import _root_.edu.colorado.phet.movingman.ladybug.aphidmaze.{AphidMazeModel}
import java.awt.geom.{AffineTransform, Rectangle2D, Point2D}
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
import umd.cs.piccolo.event.{PInputEventListener, PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PImage}
import LadybugUtil._
import util.ToggleListener

class MazeNode(model: AphidMazeModel, transform: ModelViewTransform2D) extends PNode {
  model.ladybug.addListener(updateGraphics)
  updateGraphics()

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = {
      updateGraphics()
    }
  })

  def updateGraphics(): Unit = {

    removeAllChildren()

    model.maze.rectangles.foreach((a: Rectangle2D) => {
      val viewShape = transform.getAffineTransform.createTransformedShape(a)
      val path = new PhetPPath(viewShape, Color.blue)
      addChild(path)
    })

  }

}