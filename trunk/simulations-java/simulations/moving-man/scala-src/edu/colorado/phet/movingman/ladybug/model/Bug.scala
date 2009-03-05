package edu.colorado.phet.movingman.ladybug.model

import java.awt.geom.Rectangle2D
 import scalacommon.math.Vector2D
trait Bug {
  def addListener(listener: () => Unit)
  def getPosition: Vector2D
  def getAngleInvertY: Double
  def getRadius:Double
  def getBounds:Rectangle2D
}