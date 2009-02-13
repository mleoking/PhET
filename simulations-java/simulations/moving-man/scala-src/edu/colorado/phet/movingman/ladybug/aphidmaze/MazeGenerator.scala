package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.scala.runtime.RichInt
import scala.collection.mutable.{HashSet, ArrayBuffer}

case class Wall(x: Double, y: Double, dx: Double, dy: Double)
class MazeGenerator {
  //see http://home.att.net/~srschmitt/script_maze_generator.html#the%20source%20code
  //see also http://www.mazeworks.com/mazegen/mazetut/index.htm
  //see also wikipedia

  case class Location(x: Double, y: Double) {
    def distance(other: Location) = {
      Math.abs(x - other.x) + Math.abs(y - other.y)
    }
  }
  val walls = new HashSet[Wall]
  val locations = new HashSet[Location]

  val maxX = 5;
  val maxY = 5;

  val random = new Random

  def getXPoints = -maxX to maxX

  def getYPoints = -maxY to maxY

  for (i <- getXPoints) {
    for (j <- getYPoints) {
      locations += Location(i, j)
    }
  }
  for (a <- locations) {
    val adjList = getAdjacent(a)
    for (b <- adjList) {
      walls += getWallBetween(a, b)
    }
  }

  val stack = new ArrayBuffer[Location]
  var current = Location(getXPoints(0), getYPoints(0))
  val visited = new HashSet[Location]

  while (visited != locations) {
    val unvisitedNeighbors = getAdjacent(current) -- visited
    //todo: only keep those with all walls intact for a perfect maze

    if (unvisitedNeighbors.size > 0) {
      val chosenCell = unvisitedNeighbors.toSeq(random.nextInt(unvisitedNeighbors.size))
      walls -= getWallBetween(current, chosenCell) //todo: needs to be improved for floating point
      stack += current
      current = chosenCell
      visited += current
    } else {
      current = stack.remove(stack.length - 1)
    }
  }

//  for (i <- getXPoints) {
//    for (j <- getYPoints) {
//      if (i == getXPoints(0)) {
//        walls += Wall(i, j, 0, 1)
//      }
//      if (i == getXPoints(getXPoints.length - 1)) {
//        walls += Wall(i + 1, j, 0, 1)
//      }
//      if (j == getYPoints(0)) {
//        walls += Wall(i, j, 1, 0)
//      }
//      if (j == getYPoints(getYPoints.length - 1)) {
//        walls += Wall(i, j + 1, 1, 0)
//      }
//    }
//  }

  def getWallBetween(a: Location, b: Location): Wall = {
    new Wall(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(a.x - b.x), Math.abs(a.y - b.y))
  }

  def getAdjacent(location: Location): HashSet[Location] = {
    val dx = Math.abs(getXPoints(0) - getXPoints(1))
    val iterable = locations.filter(_.distance(location) <= dx * 1.01)
    val set = new HashSet[Location]
    set ++ iterable
    set
  }

}

object Test {
  def main(args: Array[String]) {
    new MazeGenerator
  }
}