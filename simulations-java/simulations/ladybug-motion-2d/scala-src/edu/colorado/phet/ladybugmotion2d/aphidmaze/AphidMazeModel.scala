package edu.colorado.phet.ladybug2d.aphidmaze

import model.LadybugModel
import scala.collection.mutable.ArrayBuffer
import scalacommon.math.Vector2D
import model.aphidmaze.BarrierSet
import edu.colorado.phet.scalacommon.Predef._

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