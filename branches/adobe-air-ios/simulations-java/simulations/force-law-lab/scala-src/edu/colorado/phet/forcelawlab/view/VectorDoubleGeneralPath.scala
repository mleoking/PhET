// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.view

import java.awt.geom.Point2D
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath
import edu.colorado.phet.scalacommon.math.Vector2D

//Convenience subclass that makes it possible to curve using Vector2D for drawing the springs holding the masses
class VectorDoubleGeneralPath(pt: Point2D) extends DoubleGeneralPath(pt) {
  def curveTo(control1: Vector2D, control2: Vector2D, target: Vector2D) {
    curveTo(control1.x, control1.y, control2.x, control2.y, target.x, target.y)
  }
}