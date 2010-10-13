package edu.colorado.phet.motionseries.util

import edu.colorado.phet.common.phetcommon.model.Property
import edu.colorado.phet.common.phetcommon.util.SimpleObserver

/**
 * @author Sam Reid
 */

case class Range(min: Double, max: Double)

class MutableRange(range: Range) extends Property[Range](range) {
  def addListener(listener: () => Unit) = {
    super.addObserver(new SimpleObserver {
      def update = listener()
    })
  }

  def apply() = getValue
}
