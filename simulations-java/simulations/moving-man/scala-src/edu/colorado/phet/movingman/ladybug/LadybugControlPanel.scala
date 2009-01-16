package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.ControlPanel
import _root_.edu.colorado.phet.common.phetcommon.view.ResetAllButton
import _root_.scala.swing._
import java.awt.Dimension
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{Box, JButton, JRadioButton, JLabel}

class LadybugControlPanel(module: LadybugModule) extends ControlPanel(module) {
  def createBox = Box.createRigidArea(new Dimension(10, 10))

  implicit def scalaSwingToAWT(component: Component) = component.peer

  class MyRadioButton(text: String, selected: Boolean, actionListener: => Any) extends RadioButton(text) {
    peer.setSelected(selected)
    peer.addActionListener(new ActionListener() {
      def actionPerformed(ae: ActionEvent) = actionListener
    })
  }
  class VectorControlPanel(vectorVisibilityModel: VectorVisibilityModel) extends BoxPanel(Orientation.Vertical) {
    contents += new MyRadioButton("Show velocity vector", vectorVisibilityModel.isVelocityVisible(), {
      vectorVisibilityModel.setVelocityVectorVisible (true)
      println("123")
    })
    contents += new MyRadioButton("Show acceleration vector", vectorVisibilityModel.isVelocityVisible(), println("hello"))
    contents += new MyRadioButton("Show both", vectorVisibilityModel.isVelocityVisible(), println("hello"))
    contents += new MyRadioButton("Hide vectors", vectorVisibilityModel.isVelocityVisible(), println("hello"))
  }
  addControl(new VectorControlPanel(module.getVectorVisibilityModel))

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