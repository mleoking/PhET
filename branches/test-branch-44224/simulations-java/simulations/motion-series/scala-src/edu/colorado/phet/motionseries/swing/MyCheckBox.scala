package edu.colorado.phet.motionseries.swing

import java.awt.event.{ActionListener, ActionEvent}
import javax.swing.JCheckBox
import scala.swing.CheckBox

class MyCheckBox(text: String, setter: Boolean => Unit, getter: => Boolean, addListener: (() => Unit) => Unit) extends CheckBox(text) {
  addListener(update)
  update()
  peer.addActionListener(new ActionListener() {
    def actionPerformed(ae: ActionEvent) = setter(peer.isSelected)
  })
  def update() = peer.setSelected(getter)
}

//version without wrapper in case it's easier to use
class MyJCheckBox(text: String, setter: Boolean => Unit, getter: => Boolean, addListener: (() => Unit) => Unit) extends JCheckBox(text) {
  addListener(update)
  update()
  addActionListener(new ActionListener() {
    def actionPerformed(ae: ActionEvent) = setter(isSelected)
  })
  def update() = setSelected(getter)
}