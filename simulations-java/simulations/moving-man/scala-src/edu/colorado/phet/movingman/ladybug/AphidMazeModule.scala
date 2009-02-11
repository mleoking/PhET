package edu.colorado.phet.movingman.ladybug

import canvas.LadybugCanvas
import model.{LadybugModel, ScalaClock}

class AphidMazeModule(clock: ScalaClock) extends LadybugModule[AphidMazeModel](clock: ScalaClock, () => new AphidMazeModel,
  (m: LadybugModule[AphidMazeModel]) => new AphidMazeCanvas(m.model, m.getVectorVisibilityModel, m.getPathVisibilityModel)) {
  println(model)
  setLadybugDraggable(false)
}