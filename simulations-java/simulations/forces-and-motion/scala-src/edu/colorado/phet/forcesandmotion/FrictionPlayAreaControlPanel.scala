package edu.colorado.phet.motionseries.sims.forcesandmotion

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import java.awt.image.BufferedImage
import java.util.Hashtable
import javax.swing._
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults._
import edu.colorado.phet.motionseries.swing._
import edu.colorado.phet.motionseries.model.{MotionSeriesModel, ForceBead, Bead}
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.{AlignedSliderSetLayoutStrategy, HorizontalLayoutStrategy, AbstractValueControl}
import java.awt.{GridBagConstraints, GridBagLayout}
import edu.colorado.phet.motionseries.{MotionSeriesDefaults, MotionSeriesResources}

class MyValueControl(min: Double, max: Double, getter: () => Double, setter: Double => Unit, title: String, numberFormat: String, units: String, bead: Bead)
        extends ScalaValueControl(min, max, title, numberFormat, units, getter, setter, bead.addListener, new HorizontalLayoutStrategy) {
  getSlider.setPaintTicks(false)
  getSlider.setPaintLabels(false)
}

class FrictionPlayAreaControlPanel(bead: ForceBead) extends VerticalLayoutPanel {
  setFillHorizontal()

  val obj = MotionSeriesDefaults.custom //todo: this changes the mass for all tabs, not just this tab

  val staticFriction = new MyValueControl(0.0, 2.0, () => bead.staticFriction, bead.staticFriction = _, "property.coefficient-of-static-friction".translate, "0.0".literal, "".literal, bead)
  val kineticFriction = new MyValueControl(0.0, 2.0, () => bead.kineticFriction, bead.kineticFriction = _, "property.coefficient-of-kinetic-friction".translate, "0.0".literal, "".literal, bead)
  val objectMass = new MyValueControl(1, 200, () => obj.mass, obj.mass = _, "property.object-mass".translate, "0.0".literal, "units.abbr.kg".translate, bead)
  val gravity = new MyValueControl(0.1, sliderMaxGravity, () => bead.gravity.abs, x => bead.gravity = -x, "forces.Gravity".translate, "0.0".literal, "properties.acceleration.units".translate, bead)

  val sliderArray = Array[AbstractValueControl](staticFriction, kineticFriction, objectMass, gravity)
  new AlignedSliderSetLayoutStrategy(sliderArray).doLayout() //fails horribly

  val table = new Hashtable[Double, JComponent]
  class MyLabel(name: String, imageName: String) extends JLabel(name, SwingConstants.CENTER) {
    if (imageName != null)
      setIcon(new ImageIcon(MotionSeriesResources.getImage(imageName)))
    else
      setIcon(new ImageIcon(new BufferedImage(2, 10, BufferedImage.TYPE_INT_RGB)))
    setVerticalTextPosition(SwingConstants.BOTTOM)
    setHorizontalTextPosition(SwingConstants.CENTER)
  }
  table.put(moonGravity, new MyLabel("bodies.moon".translate, null))
  table.put(earthGravity, new MyLabel("bodies.earth".translate, null))
  table.put(jupiterGravity, new MyLabel("bodies.jupiter".translate, null))
  gravity.getSlider.setPaintLabels(true)
  gravity.setTickLabels(table)

  setLayout(new GridBagLayout)
  val constraints = new GridBagConstraints
  constraints.gridy = 0
  constraints.gridx = GridBagConstraints.RELATIVE
  for (s <- sliderArray) {
    add(s.getValueLabel, constraints)
    add(s.getSlider, constraints)
    add(s.getTextField, constraints)
    add(s.getUnitsLabel, constraints)
    constraints.gridy = constraints.gridy + 1
  }
}

object TestFrictionPlayAreaControlPanel {
  def main(args: Array[String]) {
    new JFrame {
      setContentPane(new FrictionPlayAreaControlPanel(new MotionSeriesModel(-3.0, true, 0.0).bead))
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      pack()
      setVisible(true)
    }

  }
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