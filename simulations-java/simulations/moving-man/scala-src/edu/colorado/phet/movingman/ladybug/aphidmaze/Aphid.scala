package edu.colorado.phet.movingman.ladybug.aphidmaze

import java.awt.geom.Rectangle2D
import model.{Bug, Observable, Vector2D}
import edu.colorado.phet.movingman.ladybug.LadybugUtil._

class Aphid(x: Double, y: Double) extends Bug with Observable {
  def getPosition = new Vector2D(x, y)

  def getAngleInvertY = 0

  def getRadius = 0.5

  def getBounds={
    val bounds=new Rectangle2D.Double
    bounds.setFrameFromCenter(getPosition,getPosition+new Vector2D(getRadius,getRadius))
    bounds
  }
}