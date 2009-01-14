package edu.colorado.phet.movingman.ladybug

import scala.collection.mutable.ArrayBuffer

class LadybugModel {
  val ladybug = new Ladybug
  private val history = new ArrayBuffer[(Double, LadybugState)]
  private var time: Double = 0;

  def update(dt: Double) = {
    time += dt;
    history += (time, ladybug.getState)

    if (estimateVelocity.magnitude > 1E-6)
      ladybug.setAngle(estimateAngle)

    ladybug.setVelocity(estimateVelocity)
  }

  def estimateAngle: Double = estimateVelocity.getAngle

  def estimateVelocity: Vector2D = {
    val dtList = for (elm <- history) yield elm._1;
    val pos = for (elm <- history) yield elm._2.position

    val posX = for (p <- pos) yield p.x
    val posY = for (p <- pos) yield p.y

    val size = posX.size
    if (size > 2) {
      val x2 = posX(size - 1)
      val x1 = posX(size - 2)

      val y2 = posY(size - 1)
      val y1 = posY(size - 2)

      val t2 = dtList(size - 1)
      val t1 = dtList(size - 2)

      val derX = (x2 - x1) / (t2 - t1)
      val derY = (y2 - y1) / (t2 - t1)
      new Vector2D(derX, derY)

    } else
      new Vector2D
  }
}