package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.edu.colorado.phet.common.phetcommon.view.util.RectangleUtils
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import canvas.aphidmaze.MazeNode
import canvas.LadybugCanvas
import java.awt.Color
import java.awt.geom.Rectangle2D
import model.LadybugModel
import controlpanel.{PathVisibilityModel, VectorVisibilityModel}

class AphidMazeCanvas(
                     model: AphidMazeModel,
                     vectorVisibilityModel: VectorVisibilityModel,
                     pathVisibilityModel: PathVisibilityModel)
        extends LadybugCanvas(
          model: LadybugModel,
          vectorVisibilityModel: VectorVisibilityModel,
          pathVisibilityModel: PathVisibilityModel,
          45,
          45) {
  addNode(0, new MazeNode(model, transform))

  addNode(new AphidSetNode(model, transform))

  model.maze.addListenerByName({
    updateTransform
  })
  updateTransform
  def updateTransform() = {
    transform.setModelBounds(RectangleUtils.expand(model.maze.getBounds, 1, 1))
  }
}