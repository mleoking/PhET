package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import _root_.edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.Dimension
import javax.swing.{JButton, JRadioButton, JPanel, JLabel}

class RemoteControl extends VerticalLayoutPanel {
  add(new JLabel("Remote Control"))
  val canvas = new PhetPCanvas
  canvas.setPreferredSize(new Dimension(120, 120))
  add(canvas)
  add(new JRadioButton("Position"))
  add(new JRadioButton("Velocity"))
  add(new JRadioButton("Acceleration"))
  add(new JButton("Go"))
}