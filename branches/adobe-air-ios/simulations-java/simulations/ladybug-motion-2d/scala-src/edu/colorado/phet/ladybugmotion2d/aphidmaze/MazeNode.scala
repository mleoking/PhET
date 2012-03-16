package edu.colorado.phet.ladybugmotion2d.aphidmaze

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.Line2D
import java.awt.{BasicStroke, Color}
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.umd.cs.piccolo.PNode

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