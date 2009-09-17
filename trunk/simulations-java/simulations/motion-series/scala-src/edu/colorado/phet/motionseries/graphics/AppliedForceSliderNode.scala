package edu.colorado.phet.motionseries.graphics

import java.awt.Dimension
import java.awt.event.{MouseAdapter, MouseEvent}
import edu.colorado.phet.motionseries.model.{Bead}
import edu.umd.cs.piccolo.PNode
import swing.ScalaValueControl
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.motionseries.MotionSeriesResources._

class AppliedForceSlider(getter: () => Double,
                         setter: Double => Unit,
                         addListener: (() => Unit) => Unit,
                         mousePressHandler: () => Unit)
        extends ScalaValueControl(-MotionSeriesDefaults.MAX_APPLIED_FORCE, MotionSeriesDefaults.MAX_APPLIED_FORCE, "controls.applied-force-x".translate, "0.0".literal, "units.abbr.newtons".translate, getter, setter, addListener) {
  setTextFieldColumns(5)
  //set applied force to zero on slider mouse release
  getSlider.addMouseListener(new MouseAdapter {
    override def mousePressed(e: MouseEvent) = mousePressHandler()

    override def mouseReleased(e: MouseEvent) = setModelValue(0)
  })

  getSlider.setPaintTicks(false) //CM indicated that the without-ticks-and-labels knob renders properly on Mac, see #689
  getSlider.setPaintLabels(false)

  //allow showing values outside the settable range
  override def isValueInRange(value: Double) = true
}

class AppliedForceSliderNode(bead: Bead, mousePressHandler: () => Unit) extends PNode {
  val max = MotionSeriesDefaults.MAX_APPLIED_FORCE
  val control = new AppliedForceSlider(() => bead.parallelAppliedForce, value => bead.parallelAppliedForce = value, bead.addListener, mousePressHandler)
  control.setSize(new Dimension(control.getPreferredSize.width, (control.getPreferredSize.height * 1.45).toInt))
  val pswing = new PSwing(control)
  addChild(pswing)
}