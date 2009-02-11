package edu.colorado.phet.movingman.ladybug

import canvas.LadybugCanvas
import model.{LadybugModel, ScalaClock}

class AphidMazeModule(clock: ScalaClock) extends LadybugModule(clock: ScalaClock, () => new AphidMazeModel,
  (m: LadybugModule) => new AphidMazeCanvas(m.model, m.getVectorVisibilityModel, m.getPathVisibilityModel)) {

  setLadybugDraggable(false)
}