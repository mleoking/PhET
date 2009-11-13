package edu.colorado.phet.motionseries.tests

import java.awt.BorderLayout
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import javax.swing.{JComboBox, JPanel, JFrame}
import scala.Array
import edu.umd.cs.piccolox.pswing.{PSwing, PComboBox}
import edu.colorado.phet.motionseries.controls.ObjectSelectionComboBox
import edu.colorado.phet.motionseries.model.MotionSeriesModel

//This test is supposed to help us identify why warning signs appear next to some but not all comboboxes, see #1880
object TestComboBox {
  def objectArray(topElm: String): Array[Object] = Array(topElm, "says", "hello")

  def main(args: Array[String]) {
    new JFrame {
      setContentPane(new JPanel(new BorderLayout) {
        add(new PhetPCanvas {
          val comboBox = new PComboBox(objectArray("pcombobox"))
          val swing = new PSwing(comboBox)
          comboBox.setEnvironment(swing,this)
          addScreenChild(swing)

          val c2 = new ObjectSelectionComboBox(new MotionSeriesModel(0.0,true,0.0))
          val s2 = new PSwing(c2)
          c2.setEnvironment(s2,this)
          s2.setOffset(200,50)
          addScreenChild(s2)

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