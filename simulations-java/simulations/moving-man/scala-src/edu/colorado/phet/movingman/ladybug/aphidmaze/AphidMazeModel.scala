package edu.colorado.phet.movingman.ladybug.aphidmaze

import scala.collection.mutable.ArrayBuffer
import model.LadybugModel
import model.aphidmaze.BarrierSet

class AphidMazeModel extends LadybugModel {
  val maze = new BarrierSet
  val aphids = new ArrayBuffer[Aphid]
  aphids += new Aphid(0, 0)
  aphids += new Aphid(4, 3)

  override def update(dt: Double) = {
    val prevPosition = ladybug.getPosition
    super.update(dt)
    val newPosition = ladybug.getPosition

    if (maze.crossedBarrier(prevPosition, newPosition))
      println("crossed barrier")

    maze.update(ladybug)
  }

  def setMazeDim(dim: Int) = {
    maze.setDim(dim)
  }
}