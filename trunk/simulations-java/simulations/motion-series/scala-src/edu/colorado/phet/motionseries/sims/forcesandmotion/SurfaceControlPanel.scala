package edu.colorado.phet.motionseries.sims.forcesandmotion

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl
import java.awt.Dimension
import java.util.Hashtable
import javax.swing._
import edu.colorado.phet.motionseries.model.SurfaceModel
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.motionseries.MotionSeriesResources._

class SurfaceControlPanel extends JPanel {
  add(new JLabel("controls.no-friction".translate))
  val slider = new LinearValueControl(0.0, 5.0, "forces.friction".translate, "0.0".literal, "".literal)
  val table = new Hashtable[Double, JComponent]
  class MyLabel(name: String, imageName: String) extends JLabel(name, SwingConstants.CENTER) {
    setIcon(new ImageIcon(MotionSeriesResources.getImage(imageName)))
    setVerticalTextPosition(SwingConstants.BOTTOM)
    setHorizontalTextPosition(SwingConstants.CENTER)
  }
  val surfaceModel = new SurfaceModel
  table.put(0.0, new MyLabel("surface.ice".translate, "robotmovingcompany/ice.gif".literal))
  table.put(2.5, new MyLabel("surface.concrete".translate, "robotmovingcompany/concrete.gif".literal))
  table.put(5.0, new MyLabel("surface.carpet".translate, "robotmovingcompany/carpet.gif".literal))
  slider.setTickLabels(table)
  slider.getSlider.setPreferredSize(new Dimension(400, slider.getPreferredSize.height))
  add(slider)
  add(new JLabel("controls.lots-of-friction".translate))
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