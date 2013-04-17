// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.gui

import javax.swing.JMenuItem
import java.awt.event.{ActionEvent, ActionListener}

class MyMenuItem(text: String, action: () => Unit) extends JMenuItem(text) {
  addActionListener(new ActionListener {
    def actionPerformed(e: ActionEvent) {
      action()
    }
  })
}