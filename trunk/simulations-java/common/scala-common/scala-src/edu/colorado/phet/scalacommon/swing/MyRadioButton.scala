package edu.colorado.phet.scalacommon.swing

import scala.swing.RadioButton
import java.awt.event.{ActionEvent, ActionListener}

class MyRadioButton(text: String, actionListener: => Unit, getter: => Boolean, addListener: (() => Unit) => Unit) extends RadioButton(text) {
  addListener(update)
  update()
  peer.addActionListener(new ActionListener() {
    def actionPerformed(ae: ActionEvent) = {
      actionListener
      update()//to handle when they press the same button twice in a row
    }
  });
  def update() = peer.setSelected(getter)
}