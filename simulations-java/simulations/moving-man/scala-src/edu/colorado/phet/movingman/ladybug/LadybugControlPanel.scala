package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.ControlPanel
import _root_.edu.colorado.phet.common.phetcommon.view.ResetAllButton
import java.awt.Dimension
import javax.swing.{Box, JButton, JRadioButton, JLabel}

class LadybugControlPanel(module: LadybugModule) extends ControlPanel(module) {
  addControl(new JRadioButton("Show velocity vector"))
  addControl(new JRadioButton("Show acceleration vector"))
  addControl(new JRadioButton("Show both"))
  addControl(new JRadioButton("Hide vectors"))
  addControl(Box.createRigidArea(new Dimension(50, 50)))

  addControl(new JLabel("Choose Motion"))
  addControl(new JRadioButton("Manual"))
  addControl(new JRadioButton("Linear"))
  addControl(new JRadioButton("Circular"))
  addControl(new JRadioButton("Ellipse"))
  addControl(Box.createRigidArea(new Dimension(50, 50)))

  addControl(new JLabel("Trace"))
  addControl(new JRadioButton("Solid"))
  addControl(new JRadioButton("Dots"))
  addControl(new JRadioButton("Off"))
  addControl(new JButton("Clear Trace"))
  addControl(Box.createRigidArea(new Dimension(50, 50)))

  addControl(new RemoteControl)
  addControl(Box.createRigidArea(new Dimension(50, 50)))
  addControl(new ResetAllButton(this))
}