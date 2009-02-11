package edu.colorado.phet.movingman.ladybug.model

import _root_.scala.collection.mutable.ArrayBuffer
import java.awt.geom.Rectangle2D

class Maze {
  val rectangles = new ArrayBuffer[Rectangle2D]

  rectangles += new Rectangle2D.Double(0, 0, 10, 10)
}