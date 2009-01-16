package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.ControlPanel
import _root_.edu.colorado.phet.common.phetcommon.view.ResetAllButton
import _root_.scala.swing._
import java.awt.Dimension
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{Box, JButton, JRadioButton, JLabel}

class LadybugControlPanel(module: LadybugModule) extends ControlPanel(module) {
  val myModule = module;
  def createBox = Box.createRigidArea(new Dimension(10, 10))

  implicit def scalaSwingToAWT(component: Component) = component.peer

  class MyRadioButton3(text: String, actionListener: => Unit, getter: => Boolean, observable: ObservableS) extends RadioButton(text) {
    observable.addListener(update)
    update()
    peer.addActionListener(new ActionListener() {
      def actionPerformed(ae: ActionEvent) = actionListener
    });
    def update() = peer.setSelected(getter)
  }

  class VectorControlPanel(m: VectorVisibilityModel) extends BoxPanel(Orientation.Vertical) {
    contents += new MyRadioButton3("Show velocity vector", {
      m.velocityVectorVisible = true
      m.accelerationVectorVisible = false
    }
      , m.velocityVectorVisible && !m.accelerationVectorVisible,
      m)

    contents += new MyRadioButton3("Show acceleration vector", {
      m.velocityVectorVisible = false
      m.accelerationVectorVisible = true
    }
      , !m.velocityVectorVisible && m.accelerationVectorVisible,
      m)

    contents += new MyRadioButton3("Show both", {
      m.velocityVectorVisible = true
      m.accelerationVectorVisible = true
    }
      , m.velocityVectorVisible && m.accelerationVectorVisible,
      m)

    contents += new MyRadioButton3("Hide Vectors", {
      m.velocityVectorVisible = false
      m.accelerationVectorVisible = false
    }
      , !m.velocityVectorVisible && !m.accelerationVectorVisible,
      m)

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