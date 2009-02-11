package edu.colorado.phet.movingman.ladybug.model

import _root_.scala.collection.mutable.ArrayBuffer
import aphidmaze.{Wall, MazeGenerator}
import java.awt.geom.Rectangle2D
import edu.colorado.phet.movingman.ladybug.LadybugUtil._

class BarrierSet {
  val rectangles = new ArrayBuffer[Rectangle2D]

  rectangles += new Rectangle2D.Double(-10, -9, 20, 1)
  rectangles += new Rectangle2D.Double(-10, 9, 20, 1)
  rectangles += new Rectangle2D.Double(-9, -10, 1, 20)
  rectangles += new Rectangle2D.Double(9, -10, 1, 20)

  def update(ladybug: Ladybug) = {}

  def containsPoint(pt: Vector2D): Boolean = {
    rectangles.foldLeft(false)((value: Boolean, cur: Rectangle2D) => cur.contains(pt) || value)
  }

  val mg = new MazeGenerator

  for (w <- mg.walls) {
    rectangles+=toRectangle(w)
  }

  def toRectangle(w: Wall): Rectangle2D = {
    new Rectangle2D.Double(w.x, w.y, w.dx + 0.1, w.dy + 0.1)
  }

}