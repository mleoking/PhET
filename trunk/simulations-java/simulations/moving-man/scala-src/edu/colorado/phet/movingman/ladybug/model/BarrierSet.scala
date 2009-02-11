package edu.colorado.phet.movingman.ladybug.model

import _root_.scala.collection.mutable.ArrayBuffer
import java.awt.geom.Rectangle2D
import edu.colorado.phet.movingman.ladybug.LadybugUtil._

class BarrierSet {
  val rectangles = new ArrayBuffer[Rectangle2D]

  rectangles += new Rectangle2D.Double(-10, -9, 20, 1)
  rectangles += new Rectangle2D.Double(-10, 9, 20, 1)
  rectangles += new Rectangle2D.Double(-9, -10, 1, 20)
  rectangles += new Rectangle2D.Double(9, -10, 1, 20)

  def update(ladybug: Ladybug) = {}

  def containsPoint(pt: Vector2D):Boolean = {
    rectangles.foldLeft(false)((value:Boolean,cur:Rectangle2D)=>cur.contains(pt)||value)
  }

}