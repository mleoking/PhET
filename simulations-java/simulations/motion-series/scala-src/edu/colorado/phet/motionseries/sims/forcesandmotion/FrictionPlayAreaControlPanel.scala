package edu.colorado.phet.motionseries.sims.forcesandmotion

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import java.awt.image.BufferedImage
import java.util.Hashtable
import javax.swing._
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults._
import edu.colorado.phet.motionseries.swing._
import edu.colorado.phet.motionseries.model.{MotionSeriesModel, ForceMotionSeriesObject, MotionSeriesObject}
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.{HorizontalLayoutStrategy, AbstractValueControl}
import java.awt.{GridBagConstraints, GridBagLayout}
import edu.colorado.phet.motionseries.{MotionSeriesDefaults, MotionSeriesResources}

class MyValueControl(min: Double, max: Double, getter: () => Double, setter: Double => Unit, title: String, numberFormat: String, units: String, bead: MotionSeriesObject)
        extends ScalaValueControl(min, max, title, numberFormat, units, getter, setter, bead.addListener, new HorizontalLayoutStrategy) {
  getSlider.setPaintTicks(false)
  getSlider.setPaintLabels(false)
  getSlider.setBackground(MotionSeriesDefaults.EARTH_COLOR)
  setBackground(MotionSeriesDefaults.EARTH_COLOR)
}

class FrictionPlayAreaControlPanel(bead: ForceMotionSeriesObject) extends VerticalLayoutPanel {
  setFillHorizontal()
  setBackground(MotionSeriesDefaults.EARTH_COLOR)

  private val customObject = MotionSeriesDefaults.custom 

  val staticFriction = new MyValueControl(0.0, 2.0, () => bead.staticFriction, bead.staticFriction = _, "property.coefficient-of-static-friction".translate, "0.0".literal, "".literal, bead)
  val kineticFriction = new MyValueControl(0.0, 2.0, () => bead.kineticFriction, bead.kineticFriction = _, "property.coefficient-of-kinetic-friction".translate, "0.0".literal, "".literal, bead)
  val objectMass = new MyValueControl(1, 200, () => customObject.mass, customObject.mass = _, "property.object-mass".translate, "0.0".literal, "units.abbr.kg".translate, bead) //todo: this changes the mass for all tabs, not just this tab
  val gravity = new MyValueControl(0.1, sliderMaxGravity, () => bead.gravity.abs, x => bead.gravity = -x, "forces.gravity".translate, "0.0".literal, "properties.acceleration.units".translate, bead)

  val sliderArray = Array[AbstractValueControl](staticFriction, kineticFriction, objectMass, gravity)

  val table = new Hashtable[Double, JComponent]
  class TickLabel(name: String, imageName: String) extends JLabel(name, SwingConstants.CENTER) {
    if (imageName != null)
      setIcon(new ImageIcon(MotionSeriesResources.getImage(imageName)))
    else
      setIcon(new ImageIcon(new BufferedImage(2, 10, BufferedImage.TYPE_INT_RGB)))
    setVerticalTextPosition(SwingConstants.BOTTOM)
    setHorizontalTextPosition(SwingConstants.CENTER)
  }
  table.put(moonGravity, new TickLabel("bodies.moon".translate, null))
  table.put(earthGravity, new TickLabel("bodies.earth".translate, null))
  table.put(jupiterGravity, new TickLabel("bodies.jupiter".translate, null))
  gravity.getSlider.setPaintLabels(true)
  gravity.setTickLabels(table)

  setLayout(new GridBagLayout)
  val constraints = new GridBagConstraints
  constraints.gridy = 0
  constraints.gridx = GridBagConstraints.RELATIVE
  for (s <- sliderArray) {
    //Right justify the slider label
    constraints.anchor = GridBagConstraints.LINE_END;
    add(s.getValueLabel, constraints)
    
    constraints.anchor = new GridBagConstraints().anchor
    add(s.getSlider, constraints)
    add(s.getTextField, constraints)
    add(s.getUnitsLabel, constraints)
    constraints.gridy = constraints.gridy + 1
  }
}

object TestFrictionPlayAreaControlPanel {
  def main(args: Array[String]) {
    new JFrame {
      setContentPane(new FrictionPlayAreaControlPanel(new MotionSeriesModel(-3.0, true, 0.0).motionSeriesObject))
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      pack()
      setVisible(true)
    }

  }
}