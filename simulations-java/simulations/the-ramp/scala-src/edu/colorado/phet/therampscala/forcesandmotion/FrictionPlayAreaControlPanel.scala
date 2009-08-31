package edu.colorado.phet.therampscala.forcesandmotion

import common.phetcommon.view.controls.valuecontrol.{AbstractValueControl, HorizontalLayoutStrategy, LinearValueControl}
import common.phetcommon.view.VerticalLayoutPanel
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.util.Hashtable
import javax.swing._
import RampResources._
import RampDefaults._

class MyValueControl(min: Double, max: Double, value: Double, title: String, numberFormat: String, units: String)
        extends LinearValueControl(min, max, value, title, numberFormat, units, new HorizontalLayoutStrategy) {
  getSlider.setPaintTicks(false)
  getSlider.setPaintLabels(false)
}

class FrictionPlayAreaControlPanel extends VerticalLayoutPanel {
  setFillHorizontal()
  val staticFriction = new MyValueControl(0.0, 5.0, 0.2, "Coefficient of static friction", "0.0".literal, "".literal)
  val kineticFriction = new MyValueControl(0.0, 5.0, 0.2, "Coefficient of kinetic friction", "0.0".literal, "".literal)
  val objectMass = new MyValueControl(0.0, 5.0, 0.2, "Object Mass", "0.0".literal, "kg")
  val gravity = new MyValueControl(0.1, sliderMaxGravity, 0.2, "Gravity", "0.0".literal, "N/kg")
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

object TestSurfaceControlPanel {
  def main(args: Array[String]) {
    val frame = new JFrame
    frame.setContentPane(new FrictionPlayAreaControlPanel)
    frame.pack()
    frame.setVisible(true)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  }
}