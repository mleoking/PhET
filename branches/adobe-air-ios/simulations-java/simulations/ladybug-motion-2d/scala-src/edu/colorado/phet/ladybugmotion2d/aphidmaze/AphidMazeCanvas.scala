package edu.colorado.phet.ladybugmotion2d.aphidmaze

import _root_.edu.colorado.phet.common.phetcommon.view.util.RectangleUtils
import edu.colorado.phet.ladybugmotion2d.controlpanel.{VectorVisibilityModel, PathVisibilityModel}
import edu.colorado.phet.ladybugmotion2d.model.LadybugModel
import edu.colorado.phet.ladybugmotion2d.canvas.LadybugCanvas

class AphidMazeCanvas(model: AphidMazeModel,
                      vectorVisibilityModel: VectorVisibilityModel,
                      pathVisibilityModel: PathVisibilityModel)
        extends LadybugCanvas(model: LadybugModel,
                              vectorVisibilityModel: VectorVisibilityModel,
                              pathVisibilityModel: PathVisibilityModel,
                              45,
                              45) {
  addNode(0, new MazeNode(model, transform))

  addNode(new AphidSetNode(model, transform))

  model.maze.addListenerByName({updateTransform()})
  updateTransform()

  def updateTransform() {
    transform.setModelBounds(RectangleUtils.expand(model.maze.getBounds, 1, 1))
  }
}