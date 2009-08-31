package edu.colorado.phet.therampscala.forcesandmotion

import common.phetcommon.view.controls.valuecontrol.{HorizontalLayoutStrategy, AbstractValueControl}
import common.phetcommon.view.VerticalLayoutPanel
import java.awt.image.BufferedImage
import java.util.Hashtable
import javax.swing._
import model.Bead
import RampResources._
import RampDefaults._
import swing.ScalaValueControl

class MyValueControl(min: Double, max: Double, getter: () => Double, setter: Double => Unit, title: String, numberFormat: String, units: String, bead: Bead)
        extends ScalaValueControl(min, max, title, numberFormat, units, getter, setter, bead.addListener, new HorizontalLayoutStrategy) {
  getSlider.setPaintTicks(false)
  getSlider.setPaintLabels(false)
}

class FrictionPlayAreaControlPanel(bead: Bead) extends VerticalLayoutPanel {
  setFillHorizontal()
  val staticFriction = new MyValueControl(0.0, 5.0, () => bead.staticFriction, bead.staticFriction = _, "Coefficient of static friction", "0.0".literal, "".literal, bead)
  val kineticFriction = new MyValueControl(0.0, 5.0, () => bead.kineticFriction, bead.kineticFriction = _, "Coefficient of kinetic friction", "0.0".literal, "".literal, bead)
  val objectMass = new MyValueControl(0.0, 200, () => bead.mass, bead.mass = _, "Object Mass", "0.0".literal, "kg", bead)
  val gravity = new MyValueControl(0.1, sliderMaxGravity, () => bead.gravity.abs, x => bead.gravity = -x, "Gravity", "0.0".literal, "N/kg", bead)
  val sliderArray = Array[AbstractValueControl](staticFriction, kineticFriction, objectMass, gravity)
  //  new AlignedSliderSetLayoutStrategy(sliderArray).doLayout()//fails horribly

  val table = new Hashtable[Double, JComponent]
  class MyLabel(name: String, imageName: String) extends JLabel(name, SwingConstants.CENTER) {
    if (imageName != null)
      setIcon(new ImageIcon(RampResources.getImage(imageName)))
    else
      setIcon(new ImageIcon(new BufferedImage(2, 10, BufferedImage.TYPE_INT_RGB)))
    setVerticalTextPosition(SwingConstants.BOTTOM)
    setHorizontalTextPosition(SwingConstants.CENTER)
  }
  table.put(moonGravity, new MyLabel("moon", null))
  table.put(earthGravity, new MyLabel("earth", null))
  table.put(jupiterGravity, new MyLabel("jupiter", null))
  gravity.getSlider.setPaintLabels(true)
  gravity.setTickLabels(table)

  for (s <- sliderArray) add(s)
}

//object TestSurfaceControlPanel {
//  def main(args: Array[String]) {
//    val frame = new JFrame
//    frame.setContentPane(new FrictionPlayAreaControlPanel(new Bead()))
//    frame.pack()
//    frame.setVisible(true)
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
//  }
//}