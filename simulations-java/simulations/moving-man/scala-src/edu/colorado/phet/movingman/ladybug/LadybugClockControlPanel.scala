package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.resources.PhetResources
import javax.swing.{Icon, JButton, ImageIcon, JPanel}

class LadybugClockControlPanel extends JPanel {
  implicit def stringToIcon(string: String): Icon = new ImageIcon(PhetCommonResources.getImage("clock/" + string))
  add(new RecordingControl)
  add(new JButton("Playback", "Play24.gif"))
  add(new JButton("Slow Playback", "StepForward24.gif"))
  add(new JButton("Pause", "Pause24.gif"))
  add(new JButton("Rewind", "Rewind24.gif"))
}