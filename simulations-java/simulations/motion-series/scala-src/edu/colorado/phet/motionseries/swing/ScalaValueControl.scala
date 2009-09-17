package edu.colorado.phet.motionseries.swing


import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.{ILayoutStrategy, DefaultLayoutStrategy, LinearValueControl}
import javax.swing.event.{ChangeListener, ChangeEvent}

class ScalaValueControl(min: Double, max: Double, name: String, decimalFormat: String, units: String,
                        private var _getter: () => Double,
                        private var _setter: Double => Unit,
                        addListener: (() => Unit) => Unit,
                        layoutStrategy: ILayoutStrategy) extends LinearValueControl(min, max, name, decimalFormat, units, layoutStrategy) {
  def this(min: Double, max: Double, name: String, decimalFormat: String, units: String,
           _getter: () => Double,
           _setter: Double => Unit,
           addListener: (() => Unit) => Unit) = this (min, max, name, decimalFormat, units, _getter, _setter, addListener, new DefaultLayoutStrategy)
  addListener(update)
  update()
  addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) = _setter(getValue)
  })
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