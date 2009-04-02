package edu.colorado.phet.ladybugmotion2d.aphidmaze

import java.awt.geom.Rectangle2D
import model.Bug
import scalacommon.math.Vector2D

import edu.colorado.phet.scalacommon.Predef._
import scalacommon.util.Observable

class Aphid(x: Double, y: Double) extends Bug with Observable {
  def getPosition = new Vector2D(x, y)

  def getAngleInvertY = 0

  def getRadius = 0.5

  def getBounds = {
    val bounds = new Rectangle2D.Double
    bounds.setFrameFromCenter(getPosition, getPosition + new Vector2D(getRadius, getRadius))
    bounds
  }
}