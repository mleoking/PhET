package edu.colorado.phet.therampscala.forcesandmotion

import common.phetcommon.view.controls.valuecontrol.{HorizontalLayoutStrategy, LinearValueControl}
import common.phetcommon.view.VerticalLayoutPanel
import javax.swing._
import RampResources._

class MyValueControl(min: Double, max: Double, value: Double, title: String, numberFormat: String, units: String)
        extends LinearValueControl(min, max, value, title, numberFormat, units, new HorizontalLayoutStrategy) {
  setMinorTicksVisible(false)
  setMajorTicksVisible(false)
}

class FrictionPlayAreaControlPanel extends VerticalLayoutPanel {
  //  setFillHorizontal()
  setFillHorizontal()
  //  add(new JLabel("controls.no-friction".translate))
  val staticFriction = new MyValueControl(0.0, 5.0, 0.2, "Coefficient of static friction", "0.0".literal, "".literal)
  val kineticFriction = new MyValueControl(0.0, 5.0, 0.2, "Coefficient of kinetic friction", "0.0".literal, "".literal)
  val objectMass = new MyValueControl(0.0, 5.0, 0.2, "Object Mass", "0.0".literal, "kg")
  val gravity = new MyValueControl(0.0, 5.0, 0.2, "Gravity", "0.0".literal, "N/kg")
  add(staticFriction)
  add(kineticFriction)
  add(objectMass)
  add(gravity)

  //  val table = new Hashtable[Double, JComponent]
  //  class MyLabel(name: String, imageName: String) extends JLabel(name, SwingConstants.CENTER) {
  //    setIcon(new ImageIcon(RampResources.getImage(imageName)))
  //    setVerticalTextPosition(SwingConstants.BOTTOM)
  //    setHorizontalTextPosition(SwingConstants.CENTER)
  //  }
  //  val surfaceModel = new SurfaceModel
  //  table.put(0.0, new MyLabel("surface.ice".translate, "robotmovingcompany/ice.gif".literal))
  //  table.put(2.5, new MyLabel("surface.concrete".translate, "robotmovingcompany/concrete.gif".literal))
  //  table.put(5.0, new MyLabel("surface.carpet".translate, "robotmovingcompany/carpet.gif".literal))
  //  //  slider.setTickLabels(table)
  //  //  slider.getSlider.setPreferredSize(new Dimension(400, slider.getPreferredSize.height))
  //  //  add(slider)
  //  add(new JLabel("controls.lots-of-friction".translate))
}

object TestSurfaceControlPanel {
  def main(args: Array[String]) {
    val frame = new JFrame
    frame.setContentPane(new FrictionPlayAreaControlPanel)
    frame.pack()
    frame.setVisible(true)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  }
}