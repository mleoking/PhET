package edu.colorado.phet.movingman.ladybug.aphidmaze

import model.{Bug, Observable, Vector2D}

class Aphid(x: Double, y: Double) extends Bug with Observable {
  def getPosition = new Vector2D(x, y)

  def getAngleInvertY = 0

  def getRadius = 1.0
}