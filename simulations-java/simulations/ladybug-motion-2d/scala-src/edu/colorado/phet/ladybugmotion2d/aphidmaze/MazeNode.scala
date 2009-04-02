package edu.colorado.phet.ladybugmotion2d.canvas.aphidmaze

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.colorado.phet.ladybug2d.aphidmaze.{AphidMazeModel}
import java.awt.geom.{AffineTransform, Line2D, Rectangle2D, Point2D}
import java.awt.{BasicStroke, Point, Color, Cursor}
import umd.cs.piccolo.PNode
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import controlpanel.VectorVisibilityModel
import java.awt.image.BufferedImage
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.util.PImageFactory
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.umd.cs.piccolo.PNode
import umd.cs.piccolo.event.{PInputEventListener, PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PImage}
import edu.colorado.phet.scalacommon.Predef._
import util.ToggleListener

class MazeNode(model: AphidMazeModel, transform: ModelViewTransform2D) extends PNode {
  model.ladybug.addListener(updateGraphics)
  updateGraphics()

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = {
      updateGraphics()
    }
  })
  model.maze.addListenerByName(updateGraphics)

  def updateGraphics(): Unit = {

    removeAllChildren()

    model.maze.lines.foreach((a: Line2D.Double) => {
      val viewShape = transform.getAffineTransform.createTransformedShape(a)
      val path = new PhetPPath(viewShape, new BasicStroke(2), Color.blue)
      addChild(path)
    })

  }

}