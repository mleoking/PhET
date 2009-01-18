package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton
import _root_.edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton
import _root_.edu.colorado.phet.common.piccolophet.PhetPCanvas
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.resources.PhetResources
import java.awt.Dimension
import javax.swing.{Icon, JButton, ImageIcon, JPanel}
import model.LadybugModel

class LadybugClockControlPanel(module: LadybugModule) extends PhetPCanvas {
  implicit def stringToIcon(string: String): Icon = new ImageIcon(PhetCommonResources.getImage("clock/" + string))
  addScreenChild(new RecordingControl(module.model))
  add(new MyButton("Playback", "Play24.gif", () => {
    module.model.setPlayback(1.0)
    module.model.setPaused(false)
  }))
  add(new MyButton("Slow Playback", "StepForward24.gif", () => {
    module.model.setPlayback(0.5)
    module.model.setPaused(false)
  }))

  val playPause=new PlayPauseButton(100)
  playPause.setOffset(100,0)
  addScreenChild(playPause)

  val stepButton=new StepButton(100)
  stepButton.setOffset(200,0)
  addScreenChild(stepButton)
  
  setPreferredSize(new Dimension(500,100))

  val pauseButton = new MyButton("Pause", "Pause24.gif", () => module.model.setPaused(!module.model.isPaused()))

  def updatePauseEnabled(a: LadybugModel) = pauseButton.setEnabled(true)
  updatePauseEnabled(module.model)
  module.model.addListener(updatePauseEnabled)
  add(pauseButton)
  add(new MyButton("Rewind", "Rewind24.gif", () => module.model.rewind))
}