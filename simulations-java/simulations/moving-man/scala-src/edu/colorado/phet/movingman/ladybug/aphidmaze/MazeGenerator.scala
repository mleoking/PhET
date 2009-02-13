package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.scala.runtime.RichInt
import scala.collection.mutable.{HashSet, ArrayBuffer}

case class Wall(x: Double, y: Double, dx: Double, dy: Double)
class MazeGenerator {
  //see http://home.att.net/~srschmitt/script_maze_generator.html#the%20source%20code

  case class Location(x: Int, y: int) {
    def distance(other: Location) = {
      Math.abs(x - other.x) + Math.abs(y - other.y)
    }
  }
  val walls = new ArrayBuffer[Wall]
  val allLocations = new HashSet[Location]
  val maxX = 8;
  val maxY = 8;
  for (i <- -maxX to maxX) {
    for (j <- -maxY to maxY) {
      walls += Wall(i, j, 1, 0)
      walls += Wall(i, j, 0, 1)
      allLocations += Location(i, j)
    }
  }

  val path = new ArrayBuffer[Location]
  var current = Location(0, 0)
  val visited = new HashSet[Location]

  while (visited != allLocations) {
    visit()
  }

  for (i <- -maxX to maxX) {
    for (j <- -maxY to maxY) {
      if (i == -maxX) {
        walls += Wall(i, j, 0, 1)
      }
      if (i == maxX) {
        walls += Wall(i + 1, j, 0, 1)
      }
      if (j == -maxY) {
        walls += Wall(i, j, 1, 0)
      }
      if (j == maxY) {
        walls += Wall(i, j + 1, 1, 0)
      }
    }
  }

  def visit() = {
    val neighbors = getAdjacent(current)
    val remaining = neighbors -- visited

    if (remaining.size > 0) {
      //todo: pick at random
      val nextLoc = remaining.toSeq(0)
      walls -= getWallBetween(current, nextLoc)
      current = nextLoc
      path += current
      visited += current
    } else {
      backtrack()
    }
  }

  def getWallBetween(a: Location, b: Location): Wall = {
    new Wall(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(a.x - b.x), Math.abs(a.y - b.y))
  }

  def backtrack() = {
    path.remove(path.length - 1)
    current = path(path.length - 1)
  }

  def getAdjacent(location: Location): HashSet[Location] = {
    val iterable = allLocations.filter(_.distance(location) <= 1.01)
    val set = new HashSet[Location]
    set ++ iterable
    set
  }

  val adjacent = getAdjacent(current)
}

object Test {
  def main(args: Array[String]) {
    new MazeGenerator
  }
}