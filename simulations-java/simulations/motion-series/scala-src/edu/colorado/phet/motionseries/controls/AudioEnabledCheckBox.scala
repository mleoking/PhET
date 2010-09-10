package edu.colorado.phet.motionseries.controls

import edu.colorado.phet.motionseries.MotionSeriesResources
import javax.swing.{JPanel, JLabel, ImageIcon}
import edu.colorado.phet.motionseries.util.ScalaMutableBoolean
import edu.colorado.phet.scalacommon.swing.MyRadioButton
import edu.colorado.phet.motionseries.Predef._

/** This control allows the user to see whether audio is enabled, and to change whether it is or not.
 * @author Sam Reid
 */
class AudioEnabledCheckBox(soundEnabled: ScalaMutableBoolean) extends JPanel {
  add(new JLabel(new ImageIcon(MotionSeriesResources.getImage("sound-icon.png"))))
  add(new MyRadioButton("audio.on".translate, soundEnabled.value = true, soundEnabled.booleanValue, soundEnabled.addListener).peer)
  add(new MyRadioButton("audio.off".translate, soundEnabled.value = false, !soundEnabled.booleanValue, soundEnabled.addListener).peer)
}