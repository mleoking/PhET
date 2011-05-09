package edu.colorado.phet.motionseries.swing


import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JButton

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Apr 27, 2009
 * Time: 10:15:33 AM
 * To change this template use File | Settings | File Templates.
 */

class ScalaButton(text: String, actionListener: () => Unit) extends JButton(text) {
  addActionListener(new ActionListener {
    def actionPerformed(e: ActionEvent) = {actionListener()}
  })
}