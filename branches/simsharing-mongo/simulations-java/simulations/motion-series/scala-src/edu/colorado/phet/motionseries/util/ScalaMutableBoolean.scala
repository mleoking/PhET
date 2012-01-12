package edu.colorado.phet.motionseries.util

import edu.colorado.phet.common.phetcommon.util.SimpleObserver
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty

/**
 * Scala Mutable Boolean overcomes incompatibility between java.lang.Boolean and scala.Boolean due to auto boxing in Java
 * @author Sam Reid
 */
class ScalaMutableBoolean(v: Boolean) extends BooleanProperty(v) {
  def booleanValue = super.get().booleanValue //to fix incompatibility between java.lang.Boolean and scala.Boolean
  def value_=(v: Boolean) = set(v)

  def value = get()

  def apply() = get()

  def addListener(listener: () => Unit) = addObserver(new SimpleObserver {def update() = listener()})
}
