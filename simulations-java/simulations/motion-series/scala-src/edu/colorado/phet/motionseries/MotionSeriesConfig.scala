package edu.colorado.phet.motionseries

import edu.colorado.phet.scalacommon.util.Observable

class ObservableDouble(initialValue: Double) extends Observable {
  var _value = initialValue

  def value = _value

  def value_=(v: Double) = {
    _value = v
    notifyListeners()
  }

  def apply() = _value
}

object MotionSeriesConfig {
  object VectorTailWidth extends ObservableDouble(4)
  object VectorHeadWidth extends ObservableDouble(10)
}