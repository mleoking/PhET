package edu.colorado.phet.ladybugmotion2d.controlpanel

import edu.colorado.phet.common.phetcommon.model.property.Property

class PathType {}

object Line extends PathType

object Dots extends PathType

object None extends PathType

class PathVisibilityModel {
  val pathType = new Property[PathType](Line)

  def resetAll() {
    pathType.set(Line)
  }
}