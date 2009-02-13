package edu.colorado.phet.movingman.ladybug.aphidmaze

import canvas.LadybugCanvas
import model.{LadybugModel, ScalaClock}

class AphidMazeModule(clock: ScalaClock) extends LadybugModule[AphidMazeModel](
  clock: ScalaClock,
  () => new AphidMazeModel,
  (m: LadybugModule[AphidMazeModel]) => new AphidMazeCanvas(m.model, m.getVectorVisibilityModel, m.getPathVisibilityModel),
  (m: LadybugModule[AphidMazeModel]) => new AphidMazeControlPanel(m)) {

  println(model)
  setLadybugDraggable(false)
}