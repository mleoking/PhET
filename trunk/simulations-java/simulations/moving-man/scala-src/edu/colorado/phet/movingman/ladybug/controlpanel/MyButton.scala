package edu.colorado.phet.movingman.ladybug.controlpanel

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{Icon, JButton}

class MyButton(text: String, icon: Icon, fun: () => Unit) extends JButton(text, icon) {
  addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = fun()
  })
}