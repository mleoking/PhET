package edu.colorado.phet.motionseries.tests

import java.awt.BorderLayout
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import javax.swing.{JComboBox, JPanel, JFrame}
import scala.Array
import edu.umd.cs.piccolox.pswing.{PSwing, PComboBox}

//This test is supposed to help us identify why warning signs appear next to some but not all comboboxes, see #1880
object TestComboBox {
  def objectArray(topElm: String): Array[Object] = Array(topElm, "says", "hello")

  def main(args: Array[String]) {
    new JFrame {
      setContentPane(new JPanel(new BorderLayout) {
        add(new PhetPCanvas {
          addScreenChild(new PSwing(new PComboBox(objectArray("pcombobox"))))
        }, BorderLayout.CENTER)
        add(new JPanel {
          add(new JComboBox(objectArray("jcombobox")))
        }, BorderLayout.SOUTH)
      })
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      setSize(800, 600)
      setVisible(true)
    }
  }
}