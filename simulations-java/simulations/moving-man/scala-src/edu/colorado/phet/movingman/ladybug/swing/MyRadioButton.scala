package edu.colorado.phet.movingman.ladybug.swing

import model.Observable
import scala.swing.RadioButton
import java.awt.event.{ActionEvent, ActionListener}

class MyRadioButton(text: String, actionListener: => Unit, getter: => Boolean, addListener: (() => Unit) => Unit) extends RadioButton(text) {
  addListener(update)
  update()
  peer.addActionListener(new ActionListener() {
    def actionPerformed(ae: ActionEvent) = actionListener
  });
  def update() = peer.setSelected(getter)
}