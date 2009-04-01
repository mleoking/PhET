package edu.colorado.phet.ladybug2d.aphidmaze

import canvas.LadybugCanvas
import controlpanel.DigitalTimer
import scalacommon.ScalaClock

class AphidMazeModule(clock: ScalaClock) extends LadybugModule[AphidMazeModel](
  clock: ScalaClock,
  new AphidMazeModel,
  (m: LadybugModule[AphidMazeModel]) => new AphidMazeCanvas(m.model, m.vectorVisibilityModel, m.pathVisibilityModel),
  (m: LadybugModule[AphidMazeModel]) => new AphidMazeControlPanel(m),
  (m: LadybugModule[AphidMazeModel]) => new DigitalTimer(m.model)
  ) {
  //Ladybug is not directly draggable in aphid maze
  setLadybugDraggable(false)
}