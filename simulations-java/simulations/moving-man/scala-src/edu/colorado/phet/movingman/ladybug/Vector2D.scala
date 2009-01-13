package edu.colorado.phet.movingman.ladybug

class Vector2D(_x: Double, _y: Double) {
  val x = _x
  val y = _y

  override def toString = "x=" + x + ", y=" + y

  def this() = this (0, 0)

  def +(that: Vector2D) = new Vector2D(that.x + x, that.y + y)

  def getAngle = java.lang.Math.atan2(y, x)
}

object TestVector2D {
  def main(args: Array[String]) = {
    val a = new Vector2D(3, 2)
    val b = new Vector2D(5, 5)
    println(a)
    println(a + b)
  }
}