package edu.colorado.phet.therampscala.forcesandfriction


import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import java.awt.Dimension
import java.util.Hashtable
import javax.swing._
import model.SurfaceModel

class SurfaceControlPanel extends JPanel {
  add(new JLabel("No Friction"))
  val slider = new LinearValueControl(0.0, 5.0, "Friction", "0.0", "")
  val table = new Hashtable[Double, JComponent]
  class MyLabel(name: String, imageName: String) extends JLabel(name, SwingConstants.CENTER) {
    setIcon(new ImageIcon(RampResources.getImage(imageName)))
    setVerticalTextPosition(SwingConstants.BOTTOM)
    setHorizontalTextPosition(SwingConstants.CENTER)
  }
  val surfaceModel = new SurfaceModel
  table.put(0.0, new MyLabel("ice", "robotmovingcompany/ice.gif"))
  table.put(2.5, new MyLabel("concrete", "robotmovingcompany/concrete.gif"))
  table.put(5.0, new MyLabel("carpet", "robotmovingcompany/carpet.gif"))
  slider.setTickLabels(table)
  slider.getSlider.setPreferredSize(new Dimension(400, slider.getPreferredSize.height))
  add(slider)
  add(new JLabel("Lots of Friction"))
}

object TestSurfaceControlPanel {
  def main(args: Array[String]) {
    val frame = new JFrame
    frame.setContentPane(new SurfaceControlPanel)
    frame.pack()
    frame.setVisible(true)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  }
}