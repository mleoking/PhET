package edu.colorado.phet.movingman.ladybug.model

trait Bug {
  def addListener(listener: () => Unit)
  def getPosition: Vector2D
  def getAngleInvertY: Double
  def getRadius:Double
}