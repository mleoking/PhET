package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.scala.collection.mutable.{HashSet, ArrayBuffer}

class MazeGenerator {
  //see http://home.att.net/~srschmitt/script_maze_generator.html#the%20source%20code

  case class Wall(x: Int, y: Int, dx: Int, dy: Int)
  case class Location(x: Int, y: int)
  val walls = new ArrayBuffer[Wall]
  val allLocations = new HashSet[Location]
  for (i <- 0 until 10) {
    for (j <- 0 until 10) {
      walls += Wall(i, j, 1, 0)
      walls += Wall(i, j, 0, 1)
      allLocations += Location(i, j)
    }
  }
  val path = new ArrayBuffer[Location]
  val current = Location(0, 0)
  val visited = new HashSet[Location]

  while (visited != allLocations) {
    visit()
  }

  def visit() = {
    val neighbors = getAdjacent(current)
    val remaining = neighbors -- visited

    if (remaining.size > 0) {
      //todo: pick at random
      val nextLoc = remaining.toSeq(0)
      walls -= getWallBetween(current, nextLoc)
    } else {
      backtrack()
    }
  }

  def getWallBetween(a: Location, b: Location): Wall = {
    new Wall(0, 0, 1, 0)
  }

  def backtrack() = {}

  def getAdjacent(location: Location): HashSet[Location] = {
    null
  }

  val adjacent = getAdjacent(current)
}

object Test {
  def main(args: Array[String]) {
    new MazeGenerator
  }
}