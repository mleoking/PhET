package edu.colorado.phet.movingman.ladybug.controlpanel

import model.ObservableS
import scala.swing.RadioButton
import java.awt.event.{ActionEvent, ActionListener}

class MyRadioButton(text: String, actionListener: => Unit, getter: => Boolean, observable: ObservableS) extends RadioButton(text) {
  observable.addListener(update)
  update()
  peer.addActionListener(new ActionListener() {
    def actionPerformed(ae: ActionEvent) = actionListener
  });
  def update() = peer.setSelected(getter)
}