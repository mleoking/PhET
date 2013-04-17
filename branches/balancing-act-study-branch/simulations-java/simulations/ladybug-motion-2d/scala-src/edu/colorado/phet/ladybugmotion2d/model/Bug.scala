package edu.colorado.phet.ladybugmotion2d.model

import java.awt.geom.Rectangle2D
import edu.colorado.phet.scalacommon.math.Vector2D

trait Bug {
  def addListener(listener: () => Unit)

  def getPosition: Vector2D

  def getAngleInvertY: Double

  def getRadius: Double

  def getBounds: Rectangle2D
}