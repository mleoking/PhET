package edu.colorado.phet.ladybugmotion2d.aphidmaze

import edu.colorado.phet.scalacommon.Predef._
import collection.mutable.ArrayBuffer
import edu.colorado.phet.ladybugmotion2d.model.LadybugModel
import edu.colorado.phet.scalacommon.math.Vector2D

class AphidMazeModel extends LadybugModel {
  val maze = new BarrierSet
  val aphids = new ArrayBuffer[Aphid]
  aphids += new Aphid(0, 0)
  aphids += new Aphid(4, 3)
  maze.addListenerByName({
    if (!maze.getBounds.contains(ladybug.getPosition))
      {
        ladybug.setPosition(0.5, 0.5)
        setSamplePoint(ladybug.getPosition)
      }
  })

  override def stepInTime(dt: Double) = {
    val prevPosition = ladybug.getPosition
    super.stepInTime(dt)
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

    aphids.foreach(handleCollision(_))
  }

  def handleCollision(a: Aphid) {
    if (ladybug.getEllipse.intersects(a.getBounds)) {
      println(" hit aphid, ladybug=" + ladybug.getEllipse.getBounds2D + ", aphid=" + a.getBounds)
      eatAphid(a)
    }
  }

  def eatAphid(a: Aphid) = {

  }

  def setMazeDim(dim: Int) = {
    maze.setDim(dim)
  }
}