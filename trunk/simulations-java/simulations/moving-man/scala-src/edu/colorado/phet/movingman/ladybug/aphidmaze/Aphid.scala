package edu.colorado.phet.movingman.ladybug.aphidmaze

import model.{Observable, Vector2D}

class Aphid(x: Double, y: Double) extends Observable {
  def getPosition = new Vector2D(x, y)

  def getAngleInvertY = 0
}