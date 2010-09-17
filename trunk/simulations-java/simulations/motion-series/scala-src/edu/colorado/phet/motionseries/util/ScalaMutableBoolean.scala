package edu.colorado.phet.motionseries.util

import edu.colorado.phet.common.phetcommon.model.MutableBoolean
import edu.colorado.phet.common.phetcommon.util.SimpleObserver

/**
 * Scala Mutable Boolean overcomes incompatibility between java.lang.Boolean and scala.Boolean due to auto boxing in Java
 * @author Sam Reid
 */
class ScalaMutableBoolean(v: Boolean) extends MutableBoolean(v) {
  def booleanValue = super.getValue().booleanValue //to fix incompatibility between java.lang.Boolean and scala.Boolean
  def value_=(v: Boolean) = setValue(v)

  def value = getValue()

  def apply() = getValue()

  def addListener(listener: () => Unit) = addObserver(new SimpleObserver {def update() = listener()})
}
