package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.scala.collection.mutable.ArrayBuffer

class MazeGenerator {
  //see http://home.att.net/~srschmitt/script_maze_generator.html#the%20source%20code

  case class Location(x: Int, y: int)
  class Cell {
    var top = true
    var bottom = true
    var left = true
    var right = true

    override def toString = "top=" + top + ", bottom=" + bottom + ", left=" + left + ", right=" + right
  }
  val grid = new Array[Array[Cell]](10, 10)
  for (i <- 0 until 10)
    for (j <- 0 until 10) {
      grid(i)(j) = new Cell
      println("i=" + i + ", j=" + j + ", cell=" + grid(i)(j))
    }

  val current = Location(0, 0)

  def getAdjacent(location: Location) = {

  }

  val adjacent = getAdjacent(current)
}

object Test {
  def main(args: Array[String]) {
    new MazeGenerator
  }
}