package edu.colorado.phet.movingman.ladybug

import model.{Maze, LadybugModel}

class AphidMazeModel extends LadybugModel {
  val maze = new Maze

  override def positionMode(dt: Double) = {
    super.positionMode(dt)

    if (maze.containsPoint(ladybug.getPosition))
      println("collision")
  }
}