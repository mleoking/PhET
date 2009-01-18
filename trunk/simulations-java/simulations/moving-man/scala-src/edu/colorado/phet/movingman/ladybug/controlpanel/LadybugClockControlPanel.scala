package edu.colorado.phet.movingman.ladybug.controlpanel

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.resources.PhetResources
import javax.swing.{Icon, JButton, ImageIcon, JPanel}
import model.LadybugModel

class LadybugClockControlPanel(module: LadybugModule) extends JPanel {
  implicit def stringToIcon(string: String): Icon = new ImageIcon(PhetCommonResources.getImage("clock/" + string))
  add(new RecordingControl(module.model))
  add(new MyButton("Playback", "Play24.gif", () => {
    module.model.setPlayback(1.0)
    module.model.setPaused(false)
  }))
  add(new MyButton("Slow Playback", "StepForward24.gif", () => {
    module.model.setPlayback(0.5)
    module.model.setPaused(false)
  }))

  val pauseButton = new MyButton("Pause", "Pause24.gif", () => module.model.setPaused(!module.model.isPaused()))

  def updatePauseEnabled(a: LadybugModel) = pauseButton.setEnabled(true)
  updatePauseEnabled(module.model)
  module.model.addListener(updatePauseEnabled)
  add(pauseButton)
  add(new MyButton("Rewind", "Rewind24.gif", () => module.model.rewind))
}