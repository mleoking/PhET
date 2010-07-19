package edu.colorado.phet.therampscala.swing


import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import javax.swing.event.{ChangeListener, ChangeEvent}

class ScalaValueControl(min: Double, max: Double, name: String, decimalFormat: String, units: String,
                        private var _getter: () => Double,
                        private var _setter: Double => Unit,
                        addListener: (() => Unit) => Unit) extends LinearValueControl(min, max, name, decimalFormat, units) {
  addListener(update)
  update()
  addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) = _setter(getValue)
  });
  def update() = setValue(_getter())
  setSignifyOutOfBounds(false)

  //make the valuecontrol watch and control another object
  def setModel(getter: () => Double, setter: Double => Unit, removeListener: (() => Unit) => Unit, addListener: (() => Unit) => Unit) {
    _getter = getter
    _setter = setter
    removeListener(update)
    addListener(update)
    update()
  }

  protected def setModelValue(d: Double) = {
    _setter(d)
  }
}