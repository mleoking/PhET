// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.motionseries.sims.forcesandmotionbasics

import edu.colorado.phet.motionseries.model.MotionSeriesObject
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl
import java.util.Hashtable
import javax.swing.{ImageIcon, SwingConstants, JLabel, JComponent}
import java.awt.image.BufferedImage
import java.awt.{GridBagConstraints, GridBagLayout}
import edu.colorado.phet.motionseries.sims.forcesandmotion.MyValueControl
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults._

class BasicsFrictionPlayAreaControlPanel(motionSeriesObject: MotionSeriesObject) extends VerticalLayoutPanel {
  setFillHorizontal()
  setBackground(MotionSeriesDefaults.EARTH_COLOR)

  val friction = new MyValueControl(0.0, 2.0, () => motionSeriesObject.kineticFriction, (f: Double) => {motionSeriesObject.kineticFriction = f; motionSeriesObject.staticFriction = f},
                                    "controls.friction".translate, "0.0".literal, "".literal, motionSeriesObject.kineticFrictionProperty)
  val objectMass = new MyValueControl(1, 200, () => motionSeriesObject.mass, motionSeriesObject.mass = _,
                                      "property.object-mass".translate, "0.0".literal, "units.abbr.kg".translate, motionSeriesObject.massProperty)
  val gravity = new MyValueControl(0.1, sliderMaxGravity, () => motionSeriesObject.gravity.abs, x => motionSeriesObject.gravity = -x,
                                   "forces.gravity".translate, "0.0".literal, "properties.acceleration.units".translate, motionSeriesObject.gravityProperty)

  val sliderArray = Array[AbstractValueControl](friction, objectMass, gravity)

  val table = new Hashtable[Double, JComponent]

  class TickLabel(name: String) extends JLabel(name, SwingConstants.CENTER) {
    setIcon(new ImageIcon(new BufferedImage(2, 10, BufferedImage.TYPE_INT_RGB)))
    setVerticalTextPosition(SwingConstants.BOTTOM)
    setHorizontalTextPosition(SwingConstants.CENTER)
  }

  table.put(moonGravity, new TickLabel("bodies.moon".translate))
  table.put(earthGravity, new TickLabel("bodies.earth".translate))
  table.put(jupiterGravity, new TickLabel("bodies.jupiter".translate))
  gravity.getSlider.setPaintLabels(true)
  gravity.setTickLabels(table)

  setLayout(new GridBagLayout)
  val constraints = new GridBagConstraints
  constraints.gridy = 0
  constraints.gridx = GridBagConstraints.RELATIVE
  for ( s: AbstractValueControl <- sliderArray ) {
    //Right justify the slider label
    constraints.anchor = GridBagConstraints.LINE_END
    add(s.getValueLabel, constraints)
    constraints.anchor = new GridBagConstraints().anchor

    add(s.getSlider, constraints)
    add(s.getTextField, constraints)

    //Left justify the units
    constraints.anchor = GridBagConstraints.LINE_START
    add(s.getUnitsLabel, constraints)
    constraints.anchor = new GridBagConstraints().anchor

    constraints.gridy = constraints.gridy + 1
  }
}
