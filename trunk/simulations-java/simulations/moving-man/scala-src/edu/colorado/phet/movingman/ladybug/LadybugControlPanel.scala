package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.ControlPanel
import _root_.edu.colorado.phet.common.phetcommon.view.ResetAllButton
import java.awt.Dimension
import javax.swing.{Box, JButton, JRadioButton, JLabel}

class LadybugControlPanel(module: LadybugModule) extends ControlPanel(module) {
  def createBox = Box.createRigidArea(new Dimension(10, 10))

  //  val button=new scala.swing.Button
  //  val component:java.awt.Component=button
  //  addControl(b)
  //  addControl(b)

  addControl(new JRadioButton("Show velocity vector"))
  addControl(new JRadioButton("Show acceleration vector"))
  addControl(new JRadioButton("Show both"))
  addControl(new JRadioButton("Hide vectors"))
  addControl(createBox)

  addControl(new JLabel("Choose Motion"))
  addControl(new JRadioButton("Manual"))
  addControl(new JRadioButton("Linear"))
  addControl(new JRadioButton("Circular"))
  addControl(new JRadioButton("Ellipse"))
  addControl(createBox)

  addControl(new JLabel("Trace"))
  addControl(new JRadioButton("Solid"))
  addControl(new JRadioButton("Dots"))
  addControl(new JRadioButton("Off"))
  addControl(new JButton("Clear Trace"))
  addControl(createBox)

  addControl(new RemoteControl)
  addControl(createBox)
  addControl(new ResetAllButton(this))
}