package edu.colorado.phet.scalacommon.swing


import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JButton

class MyJButton(text:String,actionListener:()=>Unit) extends JButton(text){
  addActionListener(new ActionListener(){
    def actionPerformed(e: ActionEvent) = actionListener()
  })
}