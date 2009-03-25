package edu.colorado.phet.therampscala.swing


import java.awt.event.{ActionListener, ActionEvent}
import scala.swing.CheckBox

class MyCheckBox(text: String, setter: Boolean => Unit, getter: => Boolean, addListener: (() => Unit) => Unit) extends CheckBox(text) {
  addListener(update)
  update()
  peer.addActionListener(new ActionListener() {
    def actionPerformed(ae: ActionEvent) = setter(peer.isSelected)
  });
  def update() = peer.setSelected(getter)
}