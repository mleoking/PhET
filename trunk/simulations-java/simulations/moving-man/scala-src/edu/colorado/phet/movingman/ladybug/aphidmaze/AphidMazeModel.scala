package edu.colorado.phet.movingman.ladybug.aphidmaze

import model.{Vector2D, LadybugModel}
import scala.collection.mutable.ArrayBuffer
import model.aphidmaze.BarrierSet
import LadybugUtil._

class AphidMazeModel extends LadybugModel {
  val maze = new BarrierSet
  val aphids = new ArrayBuffer[Aphid]
  aphids += new Aphid(0, 0)
  aphids += new Aphid(4, 3)

  override def update(dt: Double) = {
    val prevPosition = ladybug.getPosition
    super.update(dt)
    val newPosition = ladybug.getPosition

    if (maze.crossedBarrier(prevPosition, newPosition)) {
      println("hit barrier")
      ladybug.setPosition(prevPosition)
      ladybug.setVelocity(new Vector2D)
      ladybug.setAcceleration(new Vector2D)
      resetMotion2DModel
      setSamplePoint(prevPosition)
    }

    maze.update(ladybug)
  }

  def setMazeDim(dim: Int) = {
    maze.setDim(dim)
  }
}