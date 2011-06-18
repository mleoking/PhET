package edu.colorado.phet.scalacommon.view

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JButton

class MyButton(b:JButton){
  def addActionListenerByName( listener: => Unit)={
    b.addActionListener(new ActionListener{
      def actionPerformed(e: ActionEvent) = {listener}
    })
  }
}