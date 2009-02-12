package edu.colorado.phet.movingman.ladybug.aphidmaze

import scala.collection.mutable.ArrayBuffer
import model.LadybugModel
import model.aphidmaze.BarrierSet

class AphidMazeModel extends LadybugModel {
  val maze = new BarrierSet
  val aphids = new ArrayBuffer[Aphid]

  override def positionMode(dt: Double) = {
    super.positionMode(dt)

    if (maze.containsPoint(ladybug.getPosition))
      println("collision")

    maze.update(ladybug)
  }
}