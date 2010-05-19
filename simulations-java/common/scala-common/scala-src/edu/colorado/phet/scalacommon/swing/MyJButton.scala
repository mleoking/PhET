package edu.colorado.phet.scalacommon.swing

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JButton

//Todo: how about using Button instead; it looks like it has good support for this
class MyJButton(text:String,actionListener:()=>Unit) extends JButton(text){
  addActionListener(new ActionListener(){
    def actionPerformed(e: ActionEvent) = actionListener()
  })
}