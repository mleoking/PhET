package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.scala.runtime.RichInt
import scala.collection.mutable.{HashSet, ArrayBuffer}

case class Wall(x: Double, y: Double, dx: Double, dy: Double)
class MazeGenerator {
  //see http://home.att.net/~srschmitt/script_maze_generator.html#the%20source%20code

  case class Location(x: Double, y: Double) {
    def distance(other: Location) = {
      Math.abs(x - other.x) + Math.abs(y - other.y)
    }
  }
  val walls = new ArrayBuffer[Wall]
  val allLocations = new HashSet[Location]

  val maxX = 10;
  val maxY = 10;

  val random = new Random

  def getXPoints = -maxX to maxX
  //  def getXPoints = (-100 to 100 by 25).map(_/100d)
  def getYPoints = -maxY to maxY

  for (i <- getXPoints) {
    for (j <- getYPoints) {
      walls += Wall(i, j, 1, 0)
      walls += Wall(i, j, 0, 1)
      allLocations += Location(i, j)
    }
  }

  val stack = new ArrayBuffer[Location]
  var current = Location(getXPoints(0), getYPoints(0))
  val visited = new HashSet[Location]



  //  while (visited != allLocations) {
  visit()
  //  }

  //  val partOfMaze=new ArrayBuffer[Location]


  for (i <- getXPoints) {
    for (j <- getYPoints) {
      if (i == getXPoints(0)) {
        walls += Wall(i, j, 0, 1)
      }
      if (i == getXPoints(getXPoints.length - 1)) {
        walls += Wall(i + 1, j, 0, 1)
      }
      if (j == getYPoints(0)) {
        walls += Wall(i, j, 1, 0)
      }
      if (j == getYPoints(getYPoints.length - 1)) {
        walls += Wall(i, j + 1, 1, 0)
      }
    }
  }



  def visit(): Unit = {
    visited += current
    //    println("visiting current=" + current)
    //    path += current
    val unvisitedNeighbors = getAdjacent(current) -- visited
    //    println("neighbors=" + neighbors + ", remaining=" + remaining)

    if (unvisitedNeighbors.size > 0) {
      val chosenCell = unvisitedNeighbors.toSeq(random.nextInt(unvisitedNeighbors.size))
      stack += current
      //      val contains=walls.contains(getWallBetween(current, nextLoc))
      //      println("contains="+contains)
      walls -= getWallBetween(current, chosenCell) //todo: needs to be improved for floating point
      current = chosenCell
      //      stack += current
      //      visited += current
      visit()
    } else {
      //backtrack
      stack.remove(stack.length - 1)
      current = stack(stack.length - 1)
    }
  }

  def getWallBetween(a: Location, b: Location): Wall = {
    new Wall(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(a.x - b.x), Math.abs(a.y - b.y))
  }

  def getAdjacent(location: Location): HashSet[Location] = {
    val dx = Math.abs(getXPoints(0) - getXPoints(1))
    val iterable = allLocations.filter(_.distance(location) <= dx * 1.01)
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