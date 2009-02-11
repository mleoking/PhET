package edu.colorado.phet.movingman.ladybug.aphidmaze

import _root_.scala.collection.mutable.ArrayBuffer
import model.{BarrierSet, LadybugModel}

class AphidMazeModel extends LadybugModel {
  val maze = new BarrierSet
  val aphids = new ArrayBuffer[Aphid]

  override def positionMode(dt: Double) = {
    super.positionMode(dt)

    if (maze.containsPoint(ladybug.getPosition))
      println("collision")
  }
}