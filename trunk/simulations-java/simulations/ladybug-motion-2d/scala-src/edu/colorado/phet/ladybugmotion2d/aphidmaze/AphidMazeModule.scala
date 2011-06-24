package edu.colorado.phet.ladybugmotion2d.aphidmaze

import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.ladybugmotion2d.controlpanel.DigitalTimer
import edu.colorado.phet.ladybugmotion2d.LadybugModule

class AphidMazeModule(clock: ScalaClock) extends LadybugModule[AphidMazeModel](
  "aphid-maze",
  clock: ScalaClock,
  new AphidMazeModel,
  (m: LadybugModule[AphidMazeModel]) => new AphidMazeCanvas(m.model, m.vectorVisibilityModel, m.pathVisibilityModel),
  (m: LadybugModule[AphidMazeModel]) => new AphidMazeControlPanel(m),
  (m: LadybugModule[AphidMazeModel]) => new DigitalTimer(m.model)
) {
  //Ladybug is not directly draggable in aphid maze
  setLadybugDraggable(false)
}