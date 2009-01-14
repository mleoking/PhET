package edu.colorado.phet.movingman.ladybug

import scala.collection.mutable.ArrayBuffer

class LadybugModel {
  val ladybug = new Ladybug
  private val history = new ArrayBuffer[(Double, LadybugState)]
  private var time: Double = 0;

  def update(dt: Double) = {
    time += dt;
    history += (time, ladybug.getState)

    if (history.length > 5) {
      if (estimateVelocity.magnitude > 1E-6)
        ladybug.setAngle(estimateAngle())

      ladybug.setVelocity(estimateVelocity)
    }
  }

  def estimateAngle(): Double = estimateVelocity.getAngle

  def estimateVelocity(): Vector2D = {
    val v1 = estimateVelocity(history.length - 1);
    val v2 = estimateVelocity(history.length - 2);
    val v3 = estimateVelocity(history.length - 3);
    return (v1 + v2 + v3) / 3;
  }

  def estimateVelocity(index: Int): Vector2D = {
    val x2 = history(index)._2.position.x
    val x1 = history(index - 1)._2.position.x

    val y2 = history(index)._2.position.y
    val y1 = history(index - 1)._2.position.y

    val t2 = history(index)._1
    val t1 = history(index - 1)._1

    val derX = (x2 - x1) / (t2 - t1)
    val derY = (y2 - y1) / (t2 - t1)
    new Vector2D(derX, derY)
  }

}