package edu.colorado.phet.therampscala.swing


import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import javax.swing.event.{ChangeListener, ChangeEvent}

class ScalaValueControl(min: Double, max: Double, name: String, decimalFormat: String, units: String,
                       getter: => Double, setter: Double => Unit, addListener: (() => Unit) => Unit) extends LinearValueControl(min, max, name, decimalFormat, units) {
  addListener(update)
  update()
  addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) = setter(getValue)
  });
  def update() = setValue(getter)
}